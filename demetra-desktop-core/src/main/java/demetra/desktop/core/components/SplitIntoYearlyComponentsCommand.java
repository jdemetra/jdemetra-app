package demetra.desktop.core.components;

import demetra.data.Range;
import demetra.desktop.components.ComponentCommand;
import demetra.desktop.components.TsSelectionBridge;
import demetra.desktop.components.parts.HasTsCollection;
import demetra.desktop.tsproviders.DataSourceProviderBuddySupport;
import demetra.timeseries.*;
import demetra.tsprovider.util.ObsFormat;
import demetra.desktop.core.tools.JTsChartTopComponent;
import ec.util.list.swing.JLists;

import java.beans.BeanInfo;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.OptionalInt;

public final class SplitIntoYearlyComponentsCommand extends ComponentCommand<HasTsCollection> {

    public static final SplitIntoYearlyComponentsCommand INSTANCE = new SplitIntoYearlyComponentsCommand();

    private SplitIntoYearlyComponentsCommand() {
        super(TsSelectionBridge.TS_SELECTION_PROPERTY);
    }

    @Override
    public boolean isEnabled(HasTsCollection c) {
        OptionalInt selection = JLists.getSelectionIndexStream(c.getTsSelectionModel()).findFirst();
        if (selection.isPresent()) {
            demetra.timeseries.TsData data = c.getTsCollection().get(selection.getAsInt()).getData();
            return !data.isEmpty() && Duration.between(data.getDomain().start(), data.getDomain().end()).toDays() > 365;
        }
        return false;
    }

    @Override
    public void execute(HasTsCollection component) throws Exception {
        Ts ts = (component.getTsCollection().get(component.getTsSelectionModel().getMinSelectionIndex()));
        JTsChartTopComponent c = new JTsChartTopComponent();
        c.getChart().setTitle(ts.getName());
        c.getChart().setObsFormat(ObsFormat.builder().locale(null).dateTimePattern("MMM").build());
        c.getChart().setTsUpdateMode(HasTsCollection.TsUpdateMode.None);
        c.getChart().setTsCollection(split(ts));
        c.setIcon(DataSourceProviderBuddySupport.getDefault().getImage(ts.getMoniker(), BeanInfo.ICON_COLOR_16x16, false));
        c.open();
        c.requestActive();
    }

    private static TsDomain yearsOf(TsDomain domain) {
        return domain.aggregate(TsUnit.YEAR, false);
    }

    private static Ts dataOf(Range<LocalDateTime> year, TsData data) {
        TsData select = data.select(TimeSelector.between(year));
        TsData result = withYear(2000, select);
        return Ts.builder().data(result).name(year.start().getYear() + "").build();
    }

    private static TsData withYear(int year, TsData data) {
        return TsData.of(withYear(year, data.getStart()), data.getValues());
    }

    private static TsPeriod withYear(int year, TsPeriod start) {
        return start.withDate(start.start().withYear(year));
    }

    private static demetra.timeseries.TsCollection split(Ts ts) {
        return yearsOf(ts.getData().getDomain())
                .stream()
                .map(year -> dataOf(year, ts.getData()))
                .collect(TsCollection.toTsCollection());
    }
}
