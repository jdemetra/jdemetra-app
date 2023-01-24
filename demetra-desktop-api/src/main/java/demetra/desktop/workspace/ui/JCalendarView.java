/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace.ui;

import demetra.desktop.components.JTsGrid;
import demetra.desktop.components.TsSelectionBridge;
import demetra.desktop.components.parts.HasTsCollection.TsUpdateMode;
import demetra.desktop.components.tools.PeriodogramView;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.util.NbComponents;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDomain;
import demetra.timeseries.TsMoniker;
import demetra.timeseries.TsPeriod;
import demetra.timeseries.TsUnit;
import demetra.timeseries.calendars.CalendarDefinition;
import demetra.timeseries.calendars.DayClustering;
import demetra.timeseries.calendars.GenericTradingDays;
import demetra.timeseries.calendars.LengthOfPeriodType;
import demetra.timeseries.calendars.RegularFrequency;
import demetra.timeseries.calendars.TradingDaysType;
import demetra.timeseries.regression.GenericTradingDaysVariable;
import demetra.timeseries.regression.HolidaysCorrectedTradingDays;
import demetra.timeseries.regression.ITsVariable;
import demetra.timeseries.regression.LengthOfPeriod;
import demetra.timeseries.regression.ModellingContext;
import ec.util.list.swing.JLists;
import java.awt.BorderLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import jdplus.math.matrices.FastMatrix;
import jdplus.modelling.regression.HolidaysCorrectionFactory;
import jdplus.modelling.regression.Regression;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
@SwingComponent
public final class JCalendarView extends JComponent {

    // data
    private CalendarDefinition calendar;
    private TsDomain domain;
    private TradingDaysType dtype;
    private LengthOfPeriodType ltype;
    private boolean contrast = true, mean = true;

    // visual controls
    private final PropertySheet propertySheet;
    private final PeriodogramView pView;
    private final JTsGrid tsGrid;

    public JCalendarView() {
        this.calendar = null;
        this.domain = newDomain(12, 1960, 28 * 5);
        this.dtype = TradingDaysType.TD7;
        this.ltype = LengthOfPeriodType.LeapYear;

        this.propertySheet = new PropertySheet();
        propertySheet.setDescriptionAreaVisible(false);
        propertySheet.setNodes(new Node[]{new CalendarViewNode()});

        this.pView = new PeriodogramView();

        this.tsGrid = new JTsGrid();
        tsGrid.setTsUpdateMode(TsUpdateMode.None);
        tsGrid.addPropertyChangeListener(TsSelectionBridge.TS_SELECTION_PROPERTY, evt -> onTsGridSelectionChange());

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
        pView.setTs(selection.isPresent() ? tsGrid.getTsCollection().get(selection.getAsInt()) : null);
    }

    protected void onCalendarProviderChange() {
        if (calendar == null) {
            return;
        }
        int nx = getCmpsCount();
        if (nx == 0) {
            return;
        }

        DayClustering clustering = DayClustering.of(dtype);
        List<ITsVariable> vars = new ArrayList<>();
 
        HolidaysCorrectedTradingDays htd = HolidaysCorrectedTradingDays.builder()
                .clustering(clustering)
                .meanCorrection(mean)
                .contrast(contrast)
                .corrector(HolidaysCorrectionFactory.corrector(calendar, ModellingContext.getActiveContext().getCalendars(), DayOfWeek.SUNDAY))
                .build();
        vars.add(htd);
        if (ltype != LengthOfPeriodType.None) {
            vars.add(new LengthOfPeriod(ltype));
        }
        FastMatrix matrix = Regression.matrix(domain, vars.toArray(new ITsVariable[vars.size()]));

        TsPeriod domainStart = domain.getStartPeriod();
        TsCollection tss = IntStream
                .range(0, nx)
                .mapToObj(i -> Ts.builder().moniker(TsMoniker.of()).name(getCmpName(i)).data(TsData.of(domainStart, matrix.column(i))).build())
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
        int ntd = dtype.getVariablesCount();
        if (contrast) {
            --ntd;
        }
        if (idx < ntd) {
            if (contrast) {
                return GenericTradingDaysVariable.description(DayClustering.of(dtype), idx);
            } else {
                return GenericTradingDaysVariable.description(DayClustering.of(dtype), idx == 0 ? ntd - 1 : idx - 1);
            }
        } else {
            return ltype.name();
        }
    }

    private int getCmpsCount() {
        int n = dtype.getVariablesCount();
        if (contrast) {
            --n;
        }
        if (ltype != LengthOfPeriodType.None) {
            ++n;
        }
        return n;
    }

    // GETTERS/SETTERS >
    public CalendarDefinition getCalendar() {
        return calendar;
    }

    public void setCalendar(CalendarDefinition value) {
        this.calendar = value;
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

    private static TsDomain newDomain(int freq, int startYear, int yearCount) {
        TsUnit unit = TsUnit.ofAnnualFrequency(freq);
        TsPeriod start = TsPeriod.of(unit, LocalDate.of(startYear, 1, 1));
        return TsDomain.of(start, yearCount * freq);
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
            b.withEnum(RegularFrequency.class)
                    .select(this, "freq")
                    .noneOf(RegularFrequency.Undefined)
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
            b.withBoolean()
                    .select(this, "contrast")
                    .display("Contrast")
                    .add();
            b.withBoolean()
                    .select(this, "mean")
                    .display("Long term mean correction")
                    .add();
            result.put(b.build());

            return result;
        }

        public RegularFrequency getFreq() {
            return RegularFrequency.parse(domain.getAnnualFrequency());
        }

        public void setFreq(RegularFrequency freq) {
            setDomain(newDomain(freq.toInt(), domain.getStartPeriod().year(), domain.getLength() / domain.getAnnualFrequency()));
        }

        public int getLength() {

            return domain.getLength() / domain.getAnnualFrequency();
        }

        public void setLength(int length) {
            setDomain(newDomain(domain.getAnnualFrequency(), domain.getStartPeriod().year(), length));
        }

        public int getStart() {
            return domain.getStartPeriod().year();
        }

        public void setStart(int start) {
            setDomain(newDomain(domain.getAnnualFrequency(), start, domain.getLength() / domain.getAnnualFrequency()));
        }

        public TradingDaysType getType() {
            return dtype;
        }

        public void setType(TradingDaysType type) {
            setDType(type);
        }

        public boolean isContrast() {
            return contrast;
        }

        public void setContrast(boolean contrast) {
            JCalendarView.this.contrast = contrast;
            onConfigChange();
        }

        public boolean isMean() {
            return mean;
        }

        public void setMean(boolean mean) {
            JCalendarView.this.mean = mean;
            onConfigChange();
        }

    }
}
