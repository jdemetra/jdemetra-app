/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package demetra.desktop.components;

import demetra.desktop.DemetraOptions;
import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.components.parts.*;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.jfreechart.TsSparklineCellRenderer;
import demetra.desktop.tsproviders.DataSourceProviderBuddySupport;
import demetra.timeseries.TsData;
import demetra.tsprovider.util.MultiLineNameUtil;
import demetra.tsprovider.util.ObsFormat;
import ec.util.table.swing.JTables;
import ec.util.various.swing.StandardSwingColor;
import internal.ui.components.DemoTsBuilder;
import nbbrd.io.text.Formatter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.beans.BeanInfo;
import java.beans.Beans;
import java.util.List;
import java.util.*;
import java.util.function.Function;

/**
 * @author Kristof Bayens
 * @author Philippe Charles
 */
@SwingComponent
public final class JTsTable extends JComponent implements TimeSeriesComponent, PropertyChangeSource.WithWeakListeners,
        HasTsCollection, HasTsAction, HasObsFormat {

    @lombok.Value
    @lombok.Builder
    public static class Column {

        public static final Column NAME = builder()
                .name("Name")
                .type(String.class)
                .mapper(demetra.timeseries.Ts::getName)
                .build();

        public static final Column FREQ = builder()
                .name("TsUnit")
                .type(demetra.timeseries.TsUnit.class)
                .mapper(ts -> ts.getData().getTsUnit())
                .comparator(Comparator.comparing(demetra.timeseries.TsUnit::toString))
                .renderer(table -> JTables.cellRendererOf(JTsTable::renderTsUnit))
                .build();

        public static final Column START = builder()
                .name("Start")
                .type(demetra.timeseries.TsPeriod.class)
                .mapper(ts -> ts.getData().isEmpty() ? null : ts.getData().getDomain().getStartPeriod())
                .comparator(Comparator.comparing(demetra.timeseries.TsPeriod::start))
                .renderer(table -> JTables.cellRendererOf(JTsTable::renderTsPeriod))
                .build();

        public static final Column LAST = builder()
                .name("End")
                .type(demetra.timeseries.TsPeriod.class)
                .mapper(ts -> ts.getData().isEmpty() ? null : ts.getData().getDomain().getLastPeriod())
                .comparator(Comparator.comparing(demetra.timeseries.TsPeriod::end))
                .renderer(table -> JTables.cellRendererOf(JTsTable::renderTsPeriod))
                .build();

        public static final Column LENGTH = builder()
                .name("Length")
                .type(Integer.class)
                .mapper(ts -> ts.getData().length())
                .renderer(table -> JTables.cellRendererOf(JTsTable::renderTsLength))
                .build();

        public static final Column DATA = builder()
                .name("Data")
                .type(demetra.timeseries.TsData.class)
                .mapper(demetra.timeseries.Ts::getData)
                .comparator((l, r) -> -1)
                .renderer(TsDataTableCellRenderer::new)
                .build();

        public static final Column TS_IDENTIFIER = builder()
                .name("TsIdentifier")
                .type(TsIdentifier.class)
                .mapper(TsIdentifier::of)
                .comparator(Comparator.comparing(TsIdentifier::getName))
                .renderer(table -> new TsIdentifierTableCellRenderer())
                .build();

        @lombok.NonNull
        private String name;

        @lombok.NonNull
        @lombok.Builder.Default
        private Class<?> type = Object.class;

        @lombok.NonNull
        @lombok.Builder.Default
        private Function<demetra.timeseries.Ts, ?> mapper = Function.identity();

        @lombok.NonNull
        @lombok.Builder.Default
        private Comparator<?> comparator = Comparator.naturalOrder();

        @lombok.NonNull
        @lombok.Builder.Default
        private Function<JTsTable, TableCellRenderer> renderer = o -> new DefaultTableCellRenderer();
    }

    @SwingProperty
    public static final String SHOW_HEADER_PROPERTY = "showHeader";

    @SwingProperty
    public static final String COLUMNS_PROPERTY = "columns";

    @SwingProperty
    public static final String WIDTH_AS_PERCENTAGES_PROPERTY = "widthAsPercentages";

    // DEFAULT PROPERTIES
    private static final boolean DEFAULT_SHOW_HEADER = true;
    private static final List<Column> DEFAULT_COLUMNS = Collections.unmodifiableList(Arrays.asList(Column.TS_IDENTIFIER, Column.START, Column.LAST, Column.LENGTH, Column.DATA));

    // PROPERTIES
    private boolean showHeader;
    private List<Column> columns;
    private double[] widthAsPercentages;

    @lombok.experimental.Delegate
    private final HasTsCollection collection;

    @lombok.experimental.Delegate
    private final HasTsAction tsAction;

    @lombok.experimental.Delegate
    private final HasObsFormat obsFormat;

    private final TsSelectionBridge tsSelectionBridge;

    public JTsTable() {
        this.collection = HasTsCollectionSupport.of(this::firePropertyChange);
        this.tsAction = HasTsActionSupport.of(this::firePropertyChange);
        this.obsFormat = HasObsFormatSupport.of(this::firePropertyChange);
        this.showHeader = DEFAULT_SHOW_HEADER;
        this.columns = DEFAULT_COLUMNS;
        this.widthAsPercentages = new double[]{.4, .1, .1, .1, .3};

        this.tsSelectionBridge = new TsSelectionBridge(this::firePropertyChange);
        tsSelectionBridge.register(this);

        ComponentBackend.getDefault().install(this);

        applyDesignTimeProperties();
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        List<Column> old = this.columns;
        this.columns = columns != null ? Collections.unmodifiableList(columns) : DEFAULT_COLUMNS;
        firePropertyChange(COLUMNS_PROPERTY, old, this.columns);
    }

    public double[] getWidthAsPercentages() {
        return widthAsPercentages;
    }

    public void setWidthAsPercentages(double[] widthAsPercentages) {
        double[] old = this.widthAsPercentages;
        this.widthAsPercentages = widthAsPercentages;
        firePropertyChange(WIDTH_AS_PERCENTAGES_PROPERTY, old, this.widthAsPercentages);
    }

    public boolean isShowHeader() {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        boolean old = this.showHeader;
        this.showHeader = showHeader;
        firePropertyChange(SHOW_HEADER_PROPERTY, old, this.showHeader);
    }

    private void applyDesignTimeProperties() {
        if (Beans.isDesignTime()) {
            setTsCollection(DemoTsBuilder.randomTsCollection(3));
            setTsUpdateMode(TsUpdateMode.None);
            setPreferredSize(new Dimension(200, 150));
        }
    }

    private static void renderTsUnit(JLabel label, demetra.timeseries.TsUnit value) {
        label.setHorizontalAlignment(JLabel.LEADING);
        label.setText(value != null ? value.toString() : null);
    }

    private static void renderTsPeriod(JLabel label, demetra.timeseries.TsPeriod value) {
        label.setHorizontalAlignment(JLabel.TRAILING);
        label.setText(value != null ? value.display() : null);
    }

    private static void renderTsLength(JLabel label, Integer value) {
        label.setHorizontalAlignment(JLabel.TRAILING);
        label.setText(value != null ? value.toString() : null);
    }

    private static final class TsIdentifierTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof TsIdentifier) {
                TsIdentifier id = (TsIdentifier) value;
                String text = id.getName();
                if (text.isEmpty()) {
                    result.setText(" ");
                    result.setToolTipText(null);
                } else if (text.startsWith("<html>")) {
                    result.setText(text);
                    result.setToolTipText(text);
                } else {
                    result.setText(MultiLineNameUtil.join(text));
                    result.setToolTipText(MultiLineNameUtil.toHtml(text));
                }
                result.setIcon(DataSourceProviderBuddySupport.getDefault().getIcon(id.getMoniker(), BeanInfo.ICON_COLOR_16x16, false));
            }
            return result;
        }
    }

    private static final class TsDataTableCellRenderer implements TableCellRenderer {

        private final HasObsFormat target;
        private final TsSparklineCellRenderer dataRenderer;
        private final DefaultTableCellRenderer labelRenderer;

        private ObsFormat currentFormat;
        private Formatter<Number> currentFormatter;

        public TsDataTableCellRenderer(HasObsFormat target) {
            this.target = target;
            this.dataRenderer = new TsSparklineCellRenderer();
            this.labelRenderer = new DefaultTableCellRenderer();
            StandardSwingColor.TEXT_FIELD_INACTIVE_FOREGROUND.lookup().ifPresent(labelRenderer::setForeground);
            labelRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            this.currentFormat = null;
            this.currentFormatter = null;
        }

        private ObsFormat lookupObsFormat() {
            ObsFormat result = target.getObsFormat();
            return result != null ? result : DemetraOptions.getDefault().getObsFormat();
        }

        private String formatValue(Number o) {
            ObsFormat x = lookupObsFormat();
            if (!Objects.equals(x, currentFormat)) {
                currentFormat = x;
                currentFormatter = x.numberFormatter();
            }
            return currentFormatter.formatAsString(o);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof TsData) {
                TsData data = (TsData) value;
                switch (data.length()) {
                    case 0:
                        String cause = data.getEmptyCause();
                        return renderUsingLabel(table, cause.isEmpty() ? "loading? invalid?" : cause, isSelected, hasFocus, row, column);
                    case 1:
                        return renderUsingLabel(table, "Single: " + formatValue(data.getValue(0)), isSelected, hasFocus, row, column);
                    default:
                        return renderUsingSparkline(table, value, isSelected, hasFocus, row, column);
                }
            }
            return renderUsingLabel(table, value, isSelected, hasFocus, row, column);
        }

        private Component renderUsingSparkline(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return dataRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        private Component renderUsingLabel(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            labelRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            labelRenderer.setToolTipText(labelRenderer.getText());
            return labelRenderer;
        }
    }
}
