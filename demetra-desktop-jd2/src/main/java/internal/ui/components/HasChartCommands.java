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
package internal.ui.components;

import demetra.bridge.TsConverter;
import demetra.timeseries.TsCollection;
import demetra.tsprovider.util.ObsFormat;
import demetra.ui.DemetraOptions;
import ec.nbdemetra.ui.OldTsUtil;
import demetra.ui.components.TsSelectionBridge;
import demetra.ui.TsMonikerUI;
import ec.nbdemetra.ui.tools.ChartTopComponent;
import ec.tss.Ts;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDataBlock;
import ec.tstoolkit.timeseries.simplets.TsDataCollector;
import ec.tstoolkit.timeseries.simplets.TsObservation;
import ec.tstoolkit.timeseries.simplets.YearIterator;
import static demetra.ui.components.parts.HasChart.LEGEND_VISIBLE_PROPERTY;
import static demetra.ui.components.parts.HasChart.LINES_THICKNESS_PROPERTY;
import static demetra.ui.components.parts.HasChart.TITLE_VISIBLE_PROPERTY;
import ec.util.various.swing.JCommand;
import java.util.Calendar;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.NbCollections;
import demetra.ui.components.parts.HasChart;
import demetra.ui.components.parts.HasTsCollection;
import demetra.ui.components.parts.HasTsCollection.TsUpdateMode;
import demetra.ui.components.ComponentCommand;
import ec.util.list.swing.JLists;
import ec.util.various.swing.FontAwesome;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import javax.swing.ActionMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class HasChartCommands {

    public static final String TITLE_VISIBLE_ACTION = "titleVisible";

    @NonNull
    public static JCommand<HasChart> toggleTitleVisibility() {
        return ToggleTitleVisibilityCommand.INSTANCE;
    }

    public static JMenuItem newToggleTitleVisibilityMenu(ActionMap am, DemetraOptions demetraUI) {
        JMenuItem result = new JCheckBoxMenuItem(am.get(TITLE_VISIBLE_ACTION));
        result.setText("Show title");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_FONT));
        return result;
    }

    public static final String LEGEND_VISIBLE_ACTION = "legendVisible";

    @NonNull
    public static JCommand<HasChart> toggleLegendVisibility() {
        return ToggleLegendVisibilityCommand.INSTANCE;
    }

    public static JMenuItem newToggleLegendVisibilityMenu(ActionMap am, DemetraOptions demetraUI) {
        JMenuItem result = new JCheckBoxMenuItem(am.get(LEGEND_VISIBLE_ACTION));
        result.setText("Show legend");
        return result;
    }

    public static final String THIN_LINE_ACTION = "thinLine";
    public static final String THICK_LINE_ACTION = "thickLine";

    @NonNull
    public static JCommand<HasChart> applyLineThickNess(HasChart.@NonNull LinesThickness thickness) {
        return HasChart.LinesThickness.Thick.equals(thickness) ? ApplyLineThickNessCommand.THICK : ApplyLineThickNessCommand.THIN;
    }

    public static JMenu newLinesThicknessMenu(ActionMap am) {
        JMenu result = new JMenu("Lines thickness");
        result.add(new JCheckBoxMenuItem(am.get(THIN_LINE_ACTION))).setText("Thin");
        result.add(new JCheckBoxMenuItem(am.get(THICK_LINE_ACTION))).setText("Thick");
        return result;
    }

    @NonNull
    public static JCommand<HasTsCollection> splitIntoYearlyComponents() {
        return SplitIntoYearlyComponentsCommand.INSTANCE;
    }

    public static void registerActions(HasChart chart, ActionMap am) {
        am.put(TITLE_VISIBLE_ACTION, toggleTitleVisibility().toAction(chart));
        am.put(LEGEND_VISIBLE_ACTION, toggleLegendVisibility().toAction(chart));
        am.put(THIN_LINE_ACTION, applyLineThickNess(HasChart.LinesThickness.Thin).toAction(chart));
        am.put(THICK_LINE_ACTION, applyLineThickNess(HasChart.LinesThickness.Thick).toAction(chart));
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final class ToggleTitleVisibilityCommand extends ComponentCommand<HasChart> {

        public static final ToggleTitleVisibilityCommand INSTANCE = new ToggleTitleVisibilityCommand();

        public ToggleTitleVisibilityCommand() {
            super(TITLE_VISIBLE_PROPERTY);
        }

        @Override
        public boolean isSelected(HasChart component) {
            return component.isTitleVisible();
        }

        @Override
        public void execute(HasChart component) throws Exception {
            component.setTitleVisible(!component.isTitleVisible());
        }
    }

    private static final class ToggleLegendVisibilityCommand extends ComponentCommand<HasChart> {

        public static final ToggleLegendVisibilityCommand INSTANCE = new ToggleLegendVisibilityCommand();

        public ToggleLegendVisibilityCommand() {
            super(LEGEND_VISIBLE_PROPERTY);
        }

        @Override
        public boolean isSelected(HasChart component) {
            return component.isLegendVisible();
        }

        @Override
        public void execute(HasChart component) throws Exception {
            component.setLegendVisible(!component.isLegendVisible());
        }
    }

    private static final class ApplyLineThickNessCommand extends ComponentCommand<HasChart> {

        public static final ApplyLineThickNessCommand THICK = new ApplyLineThickNessCommand(HasChart.LinesThickness.Thick);
        public static final ApplyLineThickNessCommand THIN = new ApplyLineThickNessCommand(HasChart.LinesThickness.Thin);
        //
        private final HasChart.LinesThickness value;

        public ApplyLineThickNessCommand(HasChart.LinesThickness value) {
            super(LINES_THICKNESS_PROPERTY);
            this.value = value;
        }

        @Override
        public boolean isSelected(HasChart component) {
            return component.getLinesThickness() == value;
        }

        @Override
        public void execute(HasChart component) throws Exception {
            component.setLinesThickness(value);
        }
    }

    private static final class SplitIntoYearlyComponentsCommand extends ComponentCommand<HasTsCollection> {

        public static final SplitIntoYearlyComponentsCommand INSTANCE = new SplitIntoYearlyComponentsCommand();

        public SplitIntoYearlyComponentsCommand() {
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
            c.getChart().setTsUpdateMode(TsUpdateMode.None);
            c.getChart().setTsCollection(split(ts));
            Icon icon = TsMonikerUI.getDefault().getIcon(TsConverter.toTsMoniker(ts.getMoniker()));
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
    //</editor-fold>
}
