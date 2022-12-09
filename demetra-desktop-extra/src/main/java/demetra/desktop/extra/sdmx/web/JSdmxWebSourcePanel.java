package demetra.desktop.extra.sdmx.web;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import demetra.desktop.ColorSchemeManager;
import demetra.desktop.TsManager;
import demetra.desktop.design.SwingAction;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.tsproviders.DataSourceManager;
import demetra.desktop.util.ActionMaps;
import demetra.desktop.util.FontAwesomeUtils;
import demetra.desktop.util.InputMaps;
import demetra.desktop.util.KeyStrokes;
import demetra.desktop.util.ListTableModel;
import demetra.tsp.extra.sdmx.web.SdmxWebBean;
import demetra.tsp.extra.sdmx.web.SdmxWebProvider;
import ec.util.chart.ColorScheme;
import ec.util.chart.swing.SwingColorSchemeSupport;
import ec.util.desktop.Desktop;
import ec.util.desktop.DesktopManager;
import ec.util.table.swing.JTables;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import ec.util.various.swing.StandardSwingColor;
import internal.extra.sdmx.SdmxAutoCompletion;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.beans.BeanInfo;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import nbbrd.io.text.Formatter;
import org.netbeans.swing.etable.ETable;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import sdmxdl.LanguagePriorityList;
import sdmxdl.web.MonitorReport;
import sdmxdl.web.MonitorStatus;
import sdmxdl.web.SdmxWebManager;
import sdmxdl.web.SdmxWebSource;

@SwingComponent
public final class JSdmxWebSourcePanel extends JComponent {

    @SwingAction
    public static final String OPEN_ACTION = "open";

    @SwingAction
    public static final String OPEN_WEBSITE_ACTION = "openWebsite";

    @SwingAction
    public static final String OPEN_MONITOR_ACTION = "openMonitor";

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

        registerActions();
        registerInputs();

        initTable();
        initSupport();

        enableOpenOnDoubleClick();
        enablePopupMenu();
        enableProperties();

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void registerActions() {
        getActionMap().put(OPEN_ACTION, OpenCommand.INSTANCE.toAction(this));
        getActionMap().put(OPEN_WEBSITE_ACTION, OpenWebsiteCommand.INSTANCE.toAction(this));
        getActionMap().put(OPEN_MONITOR_ACTION, OpenMonitorCommand.INSTANCE.toAction(this));
        ActionMaps.copyEntries(getActionMap(), false, table.getActionMap());
    }

    private void registerInputs() {
        KeyStrokes.putAll(getInputMap(), KeyStrokes.OPEN, OPEN_ACTION);
        InputMaps.copyEntries(getInputMap(), false, table.getInputMap());
    }

    private void initTable() {
        int cellPaddingHeight = 2;
        table.setRowHeight(table.getFontMetrics(table.getFont()).getHeight() + cellPaddingHeight * 2 + 1);
//        table.setRowMargin(cellPaddingHeight);

        table.setFullyNonEditable(true);
        table.setShowHorizontalLines(true);
        table.setBorder(null);
        StandardSwingColor.CONTROL.lookup().ifPresent(table::setGridColor);

        table.setModel(new WebSourceModel());
        table.getColumnModel().getColumn(0).setCellRenderer(newNameRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(newStatusRenderer());
        JTables.setWidthAsPercentages(table, .2, .35, .35, .1);

//        table.setDragEnabled(true);
        table.setFillsViewportHeight(true);
    }

    private void initSupport() {
        support = StatusSupport
                .builder()
                .sdmxManager(sdmxManager)
                .executor(Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setPriority(Thread.MIN_PRIORITY).build()))
                .cache(new HashMap<>())
                .fallback(MonitorReport.builder().source("").status(MonitorStatus.UNKNOWN).build())
                .build();
    }

    private void enableOpenOnDoubleClick() {
        ActionMaps.onDoubleClick(getActionMap(), OPEN_ACTION, table);
    }

    private void enablePopupMenu() {
        table.setComponentPopupMenu(buildPopupMenu());
    }

    private void enableProperties() {
        addPropertyChangeListener(event -> {
            switch (event.getPropertyName()) {
                case SDMX_MANAGER_PROPERTY:
                    onSdmxManagerChange();
            }
        });
    }

    private void onSdmxManagerChange() {
        support = support.toBuilder().sdmxManager(sdmxManager).build();
        ((WebSourceModel) table.getModel()).setValues(
                sdmxManager.getSources().values().stream().filter(source -> !source.isAlias()).collect(Collectors.toList()),
                sdmxManager.getLanguages()
        );
    }

    private JPopupMenu buildPopupMenu() {
        ActionMap actionMap = getActionMap();

        JMenu result = new JMenu();

        JMenuItem item;

        item = new JMenuItem(actionMap.get(OPEN_ACTION));
        item.setText("Open");
        item.setAccelerator(KeyStrokes.OPEN.get(0));
        item.setFont(item.getFont().deriveFont(Font.BOLD));
        result.add(item);

        item = new JMenuItem(actionMap.get(OPEN_WEBSITE_ACTION));
        item.setText("Open web site");
        result.add(item);

        item = new JMenuItem(actionMap.get(OPEN_MONITOR_ACTION));
        item.setText("Open monitor");
        result.add(item);

        return result.getPopupMenu();
    }

    private DefaultTableCellRenderer newNameRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof SdmxWebSource source) {
                    result.setText(source.getName());
                    result.setIcon(SdmxAutoCompletion.getFavicon(source.getWebsite(), table::repaint));
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
                    SwingColorSchemeSupport colors = ColorSchemeManager.get().getSupport(ColorSchemeManager.get().getMainColorScheme());
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

    @lombok.Builder(toBuilder = true)
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

    private static final class WebSourceModel extends ListTableModel<SdmxWebSource> {

        private List<SdmxWebSource> values = Collections.emptyList();
        private LanguagePriorityList languages = LanguagePriorityList.ANY;

        public void setValues(List<SdmxWebSource> values, LanguagePriorityList languages) {
            this.values = values;
            this.languages = languages;
            fireTableDataChanged();
        }

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
                    return languages.select(row.getDescriptions());
                case 2:
                    return row.getWebsite();
                case 3:
                    return row;
                default:
                    return null;
            }
        }
    }

    private static final class OpenCommand extends JCommand<JSdmxWebSourcePanel> {

        public static final OpenCommand INSTANCE = new OpenCommand();

        @Override
        public void execute(JSdmxWebSourcePanel c) throws Exception {
            int idx = c.table.convertRowIndexToModel(c.table.getSelectedRow());
            SdmxWebSource source = ((WebSourceModel) c.table.getModel()).getValues().get(idx);
            TsManager.get().getProvider(SdmxWebProvider.class).ifPresent(provider -> {
                SdmxWebBean bean = provider.newBean();
                bean.setSource(source.getName());
                if (DataSourceManager.get().getBeanEditor(provider.getSource(), "Open data source").editBean(bean, Exceptions::printStackTrace)) {
                    provider.open(provider.encodeBean(bean));
                }
            });
        }

        @Override
        public boolean isEnabled(JSdmxWebSourcePanel c) {
            return c.table.getSelectedRowCount() == 1;
        }

        @Override
        public JCommand.ActionAdapter toAction(JSdmxWebSourcePanel c) {
            return super.toAction(c).withWeakListSelectionListener(c.table.getSelectionModel());
        }
    }

    private static final class OpenWebsiteCommand extends JCommand<JSdmxWebSourcePanel> {

        public static final OpenWebsiteCommand INSTANCE = new OpenWebsiteCommand();

        @Override
        public void execute(JSdmxWebSourcePanel c) throws Exception {
            try {
                DesktopManager.get().browse(getSelection(c).getWebsite().toURI());
            } catch (IOException | URISyntaxException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        public boolean isEnabled(JSdmxWebSourcePanel c) {
            return c.table.getSelectedRowCount() == 1
                    && DesktopManager.get().isSupported(Desktop.Action.BROWSE)
                    && getSelection(c).getWebsite() != null;
        }

        @Override
        public JCommand.ActionAdapter toAction(JSdmxWebSourcePanel c) {
            return super.toAction(c).withWeakListSelectionListener(c.table.getSelectionModel());
        }

        private SdmxWebSource getSelection(JSdmxWebSourcePanel c) {
            int idx = c.table.convertRowIndexToModel(c.table.getSelectedRow());
            return ((WebSourceModel) c.table.getModel()).getValues().get(idx);
        }
    }

    private static final class OpenMonitorCommand extends JCommand<JSdmxWebSourcePanel> {

        public static final OpenMonitorCommand INSTANCE = new OpenMonitorCommand();

        @Override
        public void execute(JSdmxWebSourcePanel c) throws Exception {
            try {
                DesktopManager.get().browse(getSelection(c).getMonitorWebsite().toURI());
            } catch (IOException | URISyntaxException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        public boolean isEnabled(JSdmxWebSourcePanel c) {
            return c.table.getSelectedRowCount() == 1
                    && DesktopManager.get().isSupported(Desktop.Action.BROWSE)
                    && getSelection(c).getMonitorWebsite() != null;
        }

        @Override
        public JCommand.ActionAdapter toAction(JSdmxWebSourcePanel c) {
            return super.toAction(c).withWeakListSelectionListener(c.table.getSelectionModel());
        }

        private SdmxWebSource getSelection(JSdmxWebSourcePanel c) {
            int idx = c.table.convertRowIndexToModel(c.table.getSelectedRow());
            return ((WebSourceModel) c.table.getModel()).getValues().get(idx);
        }
    }
}
