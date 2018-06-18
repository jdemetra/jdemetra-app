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
package demetra.ui.components;

import demetra.bridge.TsConverter;
import demetra.demo.DemoTsBuilder;
import demetra.ui.TsManager;
import demetra.ui.beans.PropertyChangeSource;
import ec.nbdemetra.ui.DemetraUI;
import ec.tss.TsIdentifier;
import ec.tstoolkit.utilities.Arrays2;
import ec.util.table.swing.JTables;
import internal.ui.components.InternalTsTableUI;
import internal.ui.components.TsIdentifierTableCellRenderer;
import internal.ui.components.InternalUI;
import internal.ui.components.TsDataTableCellRenderer;
import java.awt.Dimension;
import java.beans.Beans;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Kristof Bayens
 * @author Philippe Charles
 */
public final class JTsTable extends JComponent implements TimeSeriesComponent, PropertyChangeSource,
        HasTsCollection, HasTsAction, HasObsFormat {

    @lombok.Value
    @lombok.Builder
    public static class Column {

        public static final Column NAME = builder()
                .name("Name")
                .type(String.class)
                .mapper(demetra.tsprovider.Ts::getName)
                .build();

        public static final Column FREQ = builder()
                .name("TsUnit")
                .type(demetra.timeseries.TsUnit.class)
                .mapper(ts -> ts.getData().getTsUnit())
                .comparator(Comparator.comparing(demetra.timeseries.TsUnit::toString))
                .renderer(o -> JTables.cellRendererOf(JTsTable::renderTsUnit))
                .build();

        public static final Column START = builder()
                .name("Start")
                .type(demetra.timeseries.TsPeriod.class)
                .mapper(ts -> ts.getData().isEmpty() ? null : ts.getData().getDomain().getStartPeriod())
                .comparator(Comparator.comparing(demetra.timeseries.TsPeriod::start))
                .renderer(o -> JTables.cellRendererOf(JTsTable::renderTsPeriod))
                .build();

        public static final Column LAST = builder()
                .name("End")
                .type(demetra.timeseries.TsPeriod.class)
                .mapper(ts -> ts.getData().isEmpty() ? null : ts.getData().getDomain().getLastPeriod())
                .comparator(Comparator.comparing(demetra.timeseries.TsPeriod::end))
                .renderer(o -> JTables.cellRendererOf(JTsTable::renderTsPeriod))
                .build();

        public static final Column LENGTH = builder()
                .name("Length")
                .type(Integer.class)
                .mapper(ts -> ts.getData().length())
                .renderer(o -> JTables.cellRendererOf(JTsTable::renderTsLength))
                .build();

        public static final Column DATA = builder()
                .name("Data")
                .type(demetra.timeseries.TsData.class)
                .mapper(demetra.tsprovider.Ts::getData)
                .comparator((l, r) -> -1)
                .renderer(o -> new TsDataTableCellRenderer(o, DemetraUI.getDefault()))
                .build();

        public static final Column TS_IDENTIFIER = builder()
                .name("TsIdentifier")
                .type(TsIdentifier.class)
                .mapper(ts -> new TsIdentifier(ts.getName(), TsConverter.fromTsMoniker(ts.getMoniker())))
                .comparator(Comparator.comparing(TsIdentifier::getName))
                .renderer(o -> new TsIdentifierTableCellRenderer())
                .build();

        @lombok.NonNull
        private String name;

        @lombok.NonNull
        @lombok.Builder.Default
        private Class<?> type = Object.class;

        @lombok.NonNull
        @lombok.Builder.Default
        private Function<demetra.tsprovider.Ts, ?> mapper = Function.identity();

        @lombok.NonNull
        @lombok.Builder.Default
        private Comparator<?> comparator = Comparator.naturalOrder();

        @lombok.NonNull
        @lombok.Builder.Default
        private Function<JTsTable, TableCellRenderer> renderer = o -> new DefaultTableCellRenderer();
    }

    public static final String SHOW_HEADER_PROPERTY = "showHeader";
    public static final String COLUMNS_PROPERTY = "columns";
    public static final String WIDTH_AS_PERCENTAGES_PROPERTY = "widthAsPercentages";

    // DEFAULT PROPERTIES
    private static final boolean DEFAULT_SHOW_HEADER = true;
    private static final List<Column> DEFAULT_COLUMNS = Arrays2.unmodifiableList(Column.TS_IDENTIFIER, Column.START, Column.LAST, Column.LENGTH, Column.DATA);

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

    private final InternalUI<JTsTable> internalUI;

    public JTsTable() {
        this.collection = HasTsCollection.of(this::firePropertyChange, TsManager.getDefault());
        this.tsAction = HasTsAction.of(this::firePropertyChange);
        this.obsFormat = HasObsFormat.of(this::firePropertyChange);
        this.showHeader = DEFAULT_SHOW_HEADER;
        this.columns = DEFAULT_COLUMNS;
        this.widthAsPercentages = new double[]{.4, .1, .1, .1, .3};

        this.tsSelectionBridge = new TsSelectionBridge(this::firePropertyChange);
        tsSelectionBridge.register(this);

        this.internalUI = new InternalTsTableUI();
        internalUI.install(this);

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
}
