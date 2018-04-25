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
package ec.ui.commands;

import demetra.ui.TsManager;
import ec.nbdemetra.ui.MonikerUI;
import ec.nbdemetra.ui.tools.ChartTopComponent;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsStatus;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.design.UtilityClass;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDataBlock;
import ec.tstoolkit.timeseries.simplets.TsDataCollector;
import ec.tstoolkit.timeseries.simplets.TsObservation;
import ec.tstoolkit.timeseries.simplets.YearIterator;
import ec.tstoolkit.utilities.Arrays2;
import ec.ui.interfaces.ITsChart;
import static ec.ui.interfaces.ITsChart.LEGEND_VISIBLE_PROPERTY;
import static ec.ui.interfaces.ITsChart.LINES_THICKNESS_PROPERTY;
import static ec.ui.interfaces.ITsChart.TITLE_VISIBLE_PROPERTY;
import ec.ui.interfaces.ITsCollectionView;
import static ec.ui.interfaces.ITsCollectionView.SELECTION_PROPERTY;
import ec.util.various.swing.JCommand;
import java.util.Calendar;
import javax.annotation.Nonnull;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.NbCollections;

/**
 *
 * @author Philippe Charles
 */
@UtilityClass(ITsChart.class)
public final class TsChartCommand {

    private TsChartCommand() {
        // static class
    }

    @Nonnull
    public static JCommand<ITsChart> toggleTitleVisibility() {
        return ToggleTitleVisibilityCommand.INSTANCE;
    }

    @Nonnull
    public static JCommand<ITsChart> toggleLegendVisibility() {
        return ToggleLegendVisibilityCommand.INSTANCE;
    }

    @Nonnull
    public static JCommand<ITsChart> applyLineThickNess(@Nonnull ITsChart.LinesThickness thickness) {
        return ITsChart.LinesThickness.Thick.equals(thickness) ? ApplyLineThickNessCommand.THICK : ApplyLineThickNessCommand.THIN;
    }

    @Nonnull
    public static JCommand<ITsChart> showAll() {
        return ShowAllCommand.INSTANCE;
    }

    @Nonnull
    public static JCommand<ITsChart> splitIntoYearlyComponents() {
        return SplitIntoYearlyComponentsCommand.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final class ToggleTitleVisibilityCommand extends ComponentCommand<ITsChart> {

        public static final ToggleTitleVisibilityCommand INSTANCE = new ToggleTitleVisibilityCommand();

        public ToggleTitleVisibilityCommand() {
            super(TITLE_VISIBLE_PROPERTY);
        }

        @Override
        public boolean isSelected(ITsChart component) {
            return component.isTitleVisible();
        }

        @Override
        public void execute(ITsChart component) throws Exception {
            component.setTitleVisible(!component.isTitleVisible());
        }
    }

    private static final class ToggleLegendVisibilityCommand extends ComponentCommand<ITsChart> {

        public static final ToggleLegendVisibilityCommand INSTANCE = new ToggleLegendVisibilityCommand();

        public ToggleLegendVisibilityCommand() {
            super(LEGEND_VISIBLE_PROPERTY);
        }

        @Override
        public boolean isSelected(ITsChart component) {
            return component.isLegendVisible();
        }

        @Override
        public void execute(ITsChart component) throws Exception {
            component.setLegendVisible(!component.isLegendVisible());
        }
    }

    private static final class ApplyLineThickNessCommand extends ComponentCommand<ITsChart> {

        public static final ApplyLineThickNessCommand THICK = new ApplyLineThickNessCommand(ITsChart.LinesThickness.Thick);
        public static final ApplyLineThickNessCommand THIN = new ApplyLineThickNessCommand(ITsChart.LinesThickness.Thin);
        //
        private final ITsChart.LinesThickness value;

        public ApplyLineThickNessCommand(ITsChart.LinesThickness value) {
            super(LINES_THICKNESS_PROPERTY);
            this.value = value;
        }

        @Override
        public boolean isSelected(ITsChart component) {
            return component.getLinesThickness() == value;
        }

        @Override
        public void execute(ITsChart component) throws Exception {
            component.setLinesThickness(value);
        }
    }

    private static final class ShowAllCommand extends JCommand<ITsChart> {

        public static final ShowAllCommand INSTANCE = new ShowAllCommand();

        @Override
        public void execute(ITsChart component) throws Exception {
            component.showAll();
        }
    }

    private static final class SplitIntoYearlyComponentsCommand extends ComponentCommand<ITsChart> {

        public static final SplitIntoYearlyComponentsCommand INSTANCE = new SplitIntoYearlyComponentsCommand();

        public SplitIntoYearlyComponentsCommand() {
            super(SELECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(ITsChart component) {
            Ts[] selection = component.getSelection();
            return selection.length == 1
                    && selection[0].hasData() == TsStatus.Valid
                    && selection[0].getTsData().getDomain().getYearsCount() > 1;
        }

        @Override
        public void execute(ITsChart component) throws Exception {
            Ts[] selection = component.getSelection();
            if (Arrays2.isNullOrEmpty(selection)) {
                return;
            }
            Ts ts = selection[0];
            ChartTopComponent c = new ChartTopComponent();
            c.getChart().setTitle(ts.getName());
            c.getChart().setDataFormat(new DataFormat(null, "MMM", null));
            c.getChart().setTsUpdateMode(ITsCollectionView.TsUpdateMode.None);
            c.getChart().setTsCollection(split(ts));
            Icon icon = MonikerUI.getDefault().getIcon(ts.getMoniker());
            c.setIcon(icon != null ? ImageUtilities.icon2Image(icon) : null);
            c.open();
            c.requestActive();
        }

        private TsCollection split(Ts ts) {
            TsCollection result = TsManager.getDefault().newTsCollection();
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
                result.quietAdd(TsManager.getDefault().newTs(name, null, tmp));
            }
            return result;
        }
    }
    //</editor-fold>
}
