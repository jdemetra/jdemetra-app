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

import com.google.common.base.Strings;
import demetra.ui.TsManager;
import demetra.ui.beans.PropertyChangeSource;
import ec.nbdemetra.ui.DemetraUI;
import ec.tss.Ts;
import ec.tss.TsIdentifier;
import ec.tss.TsStatus;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.tstoolkit.utilities.Arrays2;
import ec.ui.DemoUtils;
import ec.ui.list.TsFrequencyTableCellRenderer;
import ec.ui.list.TsPeriodTableCellRenderer;
import internal.ui.components.InternalTsTableUI;
import internal.ui.components.TsIdentifierTableCellRenderer;
import internal.ui.components.InternalUI;
import internal.ui.components.NumberTableCellRenderer;
import internal.ui.components.TsDataTableCellRenderer;
import java.awt.Dimension;
import java.beans.Beans;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import javax.swing.JComponent;
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
                .mapper(Ts::getName)
                .build();

        public static final Column FREQ = builder()
                .name("Frequency")
                .type(TsFrequency.class)
                .mapper(ts -> ts.hasData().equals(TsStatus.Valid) ? ts.getTsData().getFrequency() : null)
                .comparator(Comparator.comparingInt(TsFrequency::getAsInt))
                .renderer(o -> new TsFrequencyTableCellRenderer())
                .build();

        public static final Column START = builder()
                .name("Start")
                .type(TsPeriod.class)
                .mapper(ts -> ts.hasData().equals(TsStatus.Valid) ? ts.getTsData().getDomain().getStart() : null)
                .comparator(Comparator.comparing(TsPeriod::firstday))
                .renderer(o -> new TsPeriodTableCellRenderer())
                .build();

        public static final Column LAST = builder()
                .name("End")
                .type(TsPeriod.class)
                .mapper(ts -> ts.hasData().equals(TsStatus.Valid) ? ts.getTsData().getDomain().getLast() : null)
                .comparator(Comparator.comparing(TsPeriod::lastday))
                .renderer(o -> new TsPeriodTableCellRenderer())
                .build();

        public static final Column LENGTH = builder()
                .name("Length")
                .type(Integer.class)
                .mapper(ts -> ts.hasData().equals(TsStatus.Valid) ? ts.getTsData().getLength() : null)
                .renderer(o -> new NumberTableCellRenderer())
                .build();

        public static final Column DATA = builder()
                .name("Data")
                .type(TsData.class)
                .mapper(ts -> {
                    switch (ts.hasData()) {
                        case Valid:
                            return ts.getTsData();
                        case Invalid:
                            String cause = ts.getInvalidDataCause();
                            return !Strings.isNullOrEmpty(cause) ? cause : "Invalid";
                        case Undefined:
                            return "loading";
                        default:
                            return "Unknown error";
                    }
                })
                .comparator((l, r) -> -1)
                .renderer(o -> new TsDataTableCellRenderer(o, DemetraUI.getDefault()))
                .build();

        public static final Column TS_IDENTIFIER = builder()
                .name("TsIdentifier")
                .type(TsIdentifier.class)
                .mapper(TsIdentifier::new)
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
        private Function<Ts, ?> mapper = Function.identity();

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
            setTsCollection(DemoUtils.randomTsCollection(3));
            setTsUpdateMode(TsUpdateMode.None);
            setPreferredSize(new Dimension(200, 150));
        }
    }
}
