/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import demetra.bridge.TsConverter;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsData;
import demetra.timeseries.TsPeriod;
import demetra.ui.components.parts.HasTsCollection.TsUpdateMode;
import demetra.ui.util.NbComponents;
import demetra.ui.properties.NodePropertySetBuilder;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.timeseries.calendars.IGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.LengthOfPeriodType;
import ec.tstoolkit.timeseries.calendars.TradingDaysType;
import ec.tstoolkit.timeseries.regression.LeapYearVariable;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import demetra.ui.components.JTsGrid;
import ec.ui.view.PeriodogramView;
import ec.util.list.swing.JLists;
import demetra.ui.components.TsSelectionBridge;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
public class CalendarView extends JComponent {

    private static String[] TD = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Leap Year"};
    private static String[] WD = {"Working days", "Leap Year"};
    // data
    private IGregorianCalendarProvider calendarProvider;
    private TsDomain domain;
    private TradingDaysType dtype;
    private LengthOfPeriodType ltype;
    // visual controls
    private final PropertySheet propertySheet;
    private final PeriodogramView pView;
    private final JTsGrid tsGrid;

    public CalendarView() {
        this.calendarProvider = null;
        this.domain = newDomain(TsFrequency.Monthly, 1960, 28 * 5);
        this.dtype = TradingDaysType.TradingDays;
        this.ltype = LengthOfPeriodType.LeapYear;

        this.propertySheet = new PropertySheet();
        propertySheet.setDescriptionAreaVisible(false);
        propertySheet.setNodes(new Node[]{new CalendarViewNode()});

        this.pView = new PeriodogramView();

        this.tsGrid = new JTsGrid();
        tsGrid.setTsUpdateMode(TsUpdateMode.None);
        tsGrid.addPropertyChangeListener(TsSelectionBridge.TS_SELECTION_PROPERTY, evt -> {
            onTsGridSelectionChange();
        });

        JSplitPane sp1 = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, propertySheet, pView);
        sp1.setDividerLocation(.3);
        sp1.setResizeWeight(.3);
        JSplitPane sp2 = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, sp1, tsGrid);
        sp2.setDividerLocation(.3);
        sp2.setResizeWeight(.3);
        setLayout(new BorderLayout());
        add(sp2, BorderLayout.CENTER);
        pView.setDifferencingOrder(0);
        pView.setLimitVisible(false);

    }

    protected void onTsGridSelectionChange() {
        OptionalInt selection = JLists.getSelectionIndexStream(tsGrid.getTsSelectionModel()).findFirst();
        pView.setTs(selection.isPresent() ? tsGrid.getTsCollection().getData().get(selection.getAsInt()) : null);
    }

    protected void onCalendarProviderChange() {
        if (calendarProvider == null) {
            return;
        }
        int nx = getCmpsCount();
        if (nx == 0) {
            return;
        }

        List<DataBlock> buffer = new ArrayList(nx);
        for (int i = 0; i < nx; ++i) {
            buffer.add(new DataBlock(domain.getLength()));
        }
        calendarProvider.calendarData(dtype, domain, buffer, 0);
        if (ltype != LengthOfPeriodType.None) {
            new LeapYearVariable(ltype).data(domain.getStart(), buffer.get(nx - 1));
        }

        TsPeriod domainStart = TsConverter.toTsPeriod(domain.getStart());
        TsCollection tss = IntStream
                .range(0, nx)
                .mapToObj(i -> Ts.builder().name(getCmpName(i)).data(TsData.ofInternal(domainStart, buffer.get(i).getData())).build())
                .collect(TsCollection.toTsCollection());

        tsGrid.setTsCollection(tss);
        tsGrid.getTsSelectionModel().clearSelection();
        tsGrid.getTsSelectionModel().addSelectionInterval(0, 0);
    }

    protected void onConfigChange() {
        //calViewProperties.setProperties(PropertiesPanelFactory.INSTANCE.createProperties(calendarViewProperties2));
        onCalendarProviderChange();
    }

    private String getCmpName(int idx) {
        if (dtype == TradingDaysType.TradingDays) {
            return TD[idx];
        } else if (dtype == TradingDaysType.WorkingDays) {
            return WD[idx];
        } else {
            return WD[1];
        }
    }

    private int getCmpsCount() {
        int n = dtype.getVariablesCount();
        if (ltype != LengthOfPeriodType.None) {
            ++n;
        }
        return n;
    }

    // GETTERS/SETTERS >
    public IGregorianCalendarProvider getCalendarProvider() {
        return calendarProvider;
    }

    public void setCalendarProvider(IGregorianCalendarProvider value) {
        this.calendarProvider = value;
        onCalendarProviderChange();
    }

    public TsDomain getDomain() {
        return domain;
    }

    public void setDomain(TsDomain domain) {
        this.domain = domain;
        onConfigChange();
    }

    public TradingDaysType getDType() {
        return dtype;
    }

    void setDType(TradingDaysType dtype) {
        this.dtype = dtype;
        onConfigChange();
    }
    // < GETTERS/SETTERS

    private static TsDomain newDomain(TsFrequency freq, int startYear, int yearCount) {
        return new TsDomain(freq, startYear, 0, yearCount * freq.intValue());
    }

    public class CalendarViewNode extends AbstractNode {

        public CalendarViewNode() {
            super(Children.LEAF);
            setDisplayName("Calendar view");
        }

        @Override
        protected Sheet createSheet() {
            Sheet result = new Sheet();

            NodePropertySetBuilder b = new NodePropertySetBuilder();
            b.withEnum(TsFrequency.class)
                    .select(this, "freq")
                    .noneOf(TsFrequency.Undefined)
                    .display("Frequency")
                    .add();
            b.with(int.class)
                    .select(this, "start")
                    .display("Start")
                    .add();
            b.withInt()
                    .select(this, "length")
                    .min(1)
                    .display("Length (in years)")
                    .add();
            b.withEnum(TradingDaysType.class)
                    .select(this, "type")
                    .display("Variable type")
                    .add();
            result.put(b.build());

            return result;
        }

        public TsFrequency getFreq() {
            return domain.getFrequency();
        }

        public void setFreq(TsFrequency freq) {
            setDomain(newDomain(freq, domain.getStart().getYear(), domain.getYearsCount()));
        }

        public int getLength() {
            return domain.getYearsCount();
        }

        public void setLength(int length) {
            setDomain(newDomain(domain.getFrequency(), domain.getStart().getYear(), length));
        }

        public int getStart() {
            return domain.getStart().getYear();
        }

        public void setStart(int start) {
            setDomain(newDomain(domain.getFrequency(), start, domain.getYearsCount()));
        }

        public TradingDaysType getType() {
            return dtype;
        }

        public void setType(TradingDaysType type) {
            setDType(type);
        }
    };
}
