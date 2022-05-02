package demetra.desktop.extra.sdmx.web;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import demetra.desktop.ColorSchemeManager;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.util.FontAwesomeUtils;
import demetra.desktop.util.ListTableModel;
import ec.util.chart.ColorScheme;
import ec.util.chart.swing.SwingColorSchemeSupport;
import ec.util.table.swing.JTables;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import internal.extra.sdmx.SdmxAutoCompletion;
import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.BeanInfo;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import nbbrd.io.text.Formatter;
import org.netbeans.swing.etable.ETable;
import org.openide.awt.StatusDisplayer;
import sdmxdl.web.MonitorReport;
import sdmxdl.web.MonitorStatus;
import sdmxdl.web.SdmxWebManager;
import sdmxdl.web.SdmxWebSource;

@SwingComponent
public final class JSdmxWebSourcePanel extends JComponent {

    @SwingProperty
    public static final String SDMX_MANAGER_PROPERTY = "sdmxManager";
    private static final Supplier<SdmxWebManager> DEFAULT_SDMX_MANAGER = SdmxWebManager::ofServiceLoader;
    private SdmxWebManager sdmxManager = DEFAULT_SDMX_MANAGER.get();

    public SdmxWebManager getSdmxManager() {
        return sdmxManager;
    }

    public void setSdmxManager(SdmxWebManager sdmxManager) {
        firePropertyChange(SDMX_MANAGER_PROPERTY, this.sdmxManager, this.sdmxManager = (sdmxManager != null ? sdmxManager : DEFAULT_SDMX_MANAGER.get()));
    }

    private final ETable table;
    private StatusSupport support;

    public JSdmxWebSourcePanel() {
        this.table = new ETable();
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        addPropertyChangeListener(event -> {
            switch (event.getPropertyName()) {
                case SDMX_MANAGER_PROPERTY:
                    updateTable();
            }
        });
    }

    private void updateTable() {
        support = StatusSupport
                .builder()
                .sdmxManager(sdmxManager)
                .executor(Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setPriority(Thread.MIN_PRIORITY).build()))
                .cache(new HashMap<>())
                .fallback(MonitorReport.builder().source("").status(MonitorStatus.UNKNOWN).build())
                .build();

        table.setModel(new ListTableModel<SdmxWebSource>() {
            final List<SdmxWebSource> values = new ArrayList<>(sdmxManager.getSources().values());

            @Override
            protected List<String> getColumnNames() {
                return Arrays.asList("Name", "Description", "Website", "Status");
            }

            @Override
            protected List<SdmxWebSource> getValues() {
                return values;
            }

            @Override
            protected Object getValueAt(SdmxWebSource row, int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return row;
                    case 1:
                        return sdmxManager.getLanguages().select(row.getDescriptions());
                    case 2:
                        return row.getWebsite();
                    case 3:
                        return row;
                    default:
                        return null;
                }
            }
        });
        table.getColumnModel().getColumn(0).setCellRenderer(newNameRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(newStatusRenderer());
        JTables.setWidthAsPercentages(table, .2, .35, .35, .1);
    }

    private DefaultTableCellRenderer newNameRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof SdmxWebSource source) {
                    result.setText(source.getName());
                    result.setIcon(SdmxAutoCompletion.FAVICONS.get(source.getWebsite(), table::repaint));
                }
                return result;
            }
        };
    }

    private DefaultTableCellRenderer newStatusRenderer() {
        return new DefaultTableCellRenderer() {
            final Formatter<Number> uptimeRatioFormatter = Formatter.onNumberFormat(NumberFormat.getPercentInstance());

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof SdmxWebSource source) {
                    MonitorReport report = support.get(source, table::repaint);
                    result.setText("");
                    result.setToolTipText(uptimeRatioFormatter.formatAsString(report.getUptimeRatio()) + " uptime");
                    SwingColorSchemeSupport colors = ColorSchemeManager.getDefault().getSupport(ColorSchemeManager.getDefault().getMainColorScheme());
                    switch (report.getStatus()) {
                        case DOWN ->
                            result.setIcon(FontAwesome.FA_TIMES_CIRCLE.getIcon(colors.getAreaColor(ColorScheme.KnownColor.RED), FontAwesomeUtils.toSize(BeanInfo.ICON_COLOR_16x16)));
                        case UNKNOWN ->
                            result.setIcon(FontAwesome.FA_QUESTION_CIRCLE.getIcon(colors.getAreaColor(ColorScheme.KnownColor.ORANGE), FontAwesomeUtils.toSize(BeanInfo.ICON_COLOR_16x16)));
                        case UP ->
                            result.setIcon(FontAwesome.FA_CHECK_CIRCLE.getIcon(colors.getAreaColor(ColorScheme.KnownColor.GREEN), FontAwesomeUtils.toSize(BeanInfo.ICON_COLOR_16x16)));
                    }
                }
                return result;
            }
        };
    }

    @lombok.Builder
    private static class StatusSupport {

        private final SdmxWebManager sdmxManager;

        @lombok.NonNull
        private final ExecutorService executor;

        private final MonitorReport fallback;

        // do not put URL as key because of very-slow first lookup
        @lombok.NonNull
        private final Map<String, MonitorReport> cache;

        @OnEDT
        public MonitorReport get(SdmxWebSource url, Runnable onUpdate) {
            return url != null ? cache.computeIfAbsent(url.getName(), host -> request(url, onUpdate)) : fallback;
        }

        @OnEDT
        public MonitorReport getOrNull(URL url) {
            return cache.get(url.getHost());
        }

        @OnEDT
        private MonitorReport request(SdmxWebSource url, Runnable onUpdate) {
            executor.execute(() -> loadIntoCache(url, onUpdate));
            return fallback;
        }

        @OnAnyThread
        private void loadIntoCache(SdmxWebSource url, Runnable onUpdate) {
            MonitorReport favicon = load(url);
            if (favicon != null) {
                SwingUtilities.invokeLater(() -> {
                    cache.put(url.getName(), favicon);
                    onUpdate.run();
                });
            }
        }

        @OnAnyThread
        private MonitorReport load(SdmxWebSource url) {
            report("Loading favicon for " + url.getName());
            try {
                return sdmxManager.getMonitorReport(url);
            } catch (IOException ex) {
                report("Cannot retrieve favicon for " + url.getName());
            }
            return null;
        }

        @OnAnyThread
        private void report(String message) {
            StatusDisplayer.getDefault().setStatusText(message);
        }
    }
}
