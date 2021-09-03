package internal.ui.components;

import demetra.bridge.TsConverter;
import demetra.timeseries.TsCollection;
import demetra.tsprovider.util.ObsFormat;
import demetra.desktop.IconManager;
import demetra.desktop.components.ComponentCommand;
import demetra.desktop.components.TsSelectionBridge;
import demetra.desktop.components.parts.HasTsCollection;
import ec.nbdemetra.ui.OldTsUtil;
import ec.nbdemetra.ui.tools.ChartTopComponent;
import ec.tss.Ts;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDataBlock;
import ec.tstoolkit.timeseries.simplets.TsDataCollector;
import ec.tstoolkit.timeseries.simplets.TsObservation;
import ec.tstoolkit.timeseries.simplets.YearIterator;
import ec.util.list.swing.JLists;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.OptionalInt;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.NbCollections;

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
        Ts ts = TsConverter.fromTs(component.getTsCollection().get(component.getTsSelectionModel().getMinSelectionIndex()));
        ChartTopComponent c = new ChartTopComponent();
        c.getChart().setTitle(ts.getName());
        c.getChart().setObsFormat(ObsFormat.of(null, "MMM", null));
        c.getChart().setTsUpdateMode(HasTsCollection.TsUpdateMode.None);
        c.getChart().setTsCollection(split(ts));
        Icon icon = IconManager.getDefault().getIcon(TsConverter.toTsMoniker(ts.getMoniker()));
        c.setIcon(icon != null ? ImageUtilities.icon2Image(icon) : null);
        c.open();
        c.requestActive();
    }

    private demetra.timeseries.TsCollection split(Ts ts) {
        List<demetra.timeseries.Ts> result = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        YearIterator yearIterator = new YearIterator(ts.getTsData());
        for (TsDataBlock o : NbCollections.iterable(yearIterator)) {
            TsDataCollector dc = new TsDataCollector();
            for (TsObservation obs : NbCollections.iterable(o.observations())) {
                cal.setTime(obs.getPeriod().middle());
                cal.set(Calendar.YEAR, 2000);
                dc.addObservation(cal.getTime(), obs.getValue());
            }
            String name = String.valueOf(o.start.getYear());
            TsData tmp = dc.make(o.start.getFrequency(), TsAggregationType.None);
            result.add(OldTsUtil.toTs(name, tmp));
        }
        return TsCollection.of(result);
    }
}
