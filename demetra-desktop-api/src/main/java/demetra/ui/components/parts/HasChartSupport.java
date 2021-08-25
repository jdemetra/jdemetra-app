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
package demetra.ui.components.parts;

import demetra.ui.DemetraOptions;
import demetra.ui.beans.PropertyChangeBroadcaster;
import ec.util.various.swing.JCommand;
import org.checkerframework.checker.nullness.qual.NonNull;
import demetra.ui.components.ComponentCommand;
import static demetra.ui.components.parts.HasChart.AXIS_VISIBLE_PROPERTY;
import static demetra.ui.components.parts.HasChart.LEGEND_VISIBLE_PROPERTY;
import static demetra.ui.components.parts.HasChart.LINES_THICKNESS_PROPERTY;
import static demetra.ui.components.parts.HasChart.TITLE_PROPERTY;
import static demetra.ui.components.parts.HasChart.TITLE_VISIBLE_PROPERTY;
import ec.util.various.swing.FontAwesome;
import javax.swing.ActionMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class HasChartSupport {

    @NonNull
    public static HasChart of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasChartImpl(broadcaster);
    }

    @lombok.RequiredArgsConstructor
    private static final class HasChartImpl implements HasChart {

        public static final boolean DEFAULT_LEGENDVISIBLE = true;
        public static final boolean DEFAULT_TITLEVISIBLE = true;
        public static final boolean DEFAULT_AXISVISIBLE = true;
        public static final String DEFAULT_TITLE = "";
        public static final HasChart.LinesThickness DEFAULT_LINES_THICKNESS = HasChart.LinesThickness.Thin;

        @lombok.NonNull
        private final PropertyChangeBroadcaster broadcaster;

        private boolean legendVisible = DEFAULT_LEGENDVISIBLE;
        private boolean titleVisible = DEFAULT_TITLEVISIBLE;
        private boolean axisVisible = DEFAULT_AXISVISIBLE;
        private String title = DEFAULT_TITLE;
        private HasChart.LinesThickness linesThickness = DEFAULT_LINES_THICKNESS;

        @Override
        public boolean isLegendVisible() {
            return legendVisible;
        }

        @Override
        public void setLegendVisible(boolean show) {
            boolean old = this.legendVisible;
            this.legendVisible = show;
            broadcaster.firePropertyChange(LEGEND_VISIBLE_PROPERTY, old, this.legendVisible);
        }

        @Override
        public boolean isTitleVisible() {
            return titleVisible;
        }

        @Override
        public void setTitleVisible(boolean show) {
            boolean old = this.titleVisible;
            this.titleVisible = show;
            broadcaster.firePropertyChange(TITLE_VISIBLE_PROPERTY, old, this.titleVisible);
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public void setTitle(String title) {
            String old = this.title;
            this.title = title;
            broadcaster.firePropertyChange(TITLE_PROPERTY, old, this.title);
        }

        @Override
        public boolean isAxisVisible() {
            return axisVisible;
        }

        @Override
        public void setAxisVisible(boolean showingAxis) {
            boolean old = this.axisVisible;
            this.axisVisible = showingAxis;
            broadcaster.firePropertyChange(AXIS_VISIBLE_PROPERTY, old, this.axisVisible);
        }

        @Override
        public HasChart.LinesThickness getLinesThickness() {
            return linesThickness;
        }

        @Override
        public void setLinesThickness(HasChart.LinesThickness linesThickness) {
            HasChart.LinesThickness old = this.linesThickness;
            this.linesThickness = linesThickness != null ? linesThickness : DEFAULT_LINES_THICKNESS;
            broadcaster.firePropertyChange(LINES_THICKNESS_PROPERTY, old, this.linesThickness);
        }
    }

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

    public static void registerActions(HasChart chart, ActionMap am) {
        am.put(TITLE_VISIBLE_ACTION, toggleTitleVisibility().toAction(chart));
        am.put(LEGEND_VISIBLE_ACTION, toggleLegendVisibility().toAction(chart));
        am.put(THIN_LINE_ACTION, applyLineThickNess(HasChart.LinesThickness.Thin).toAction(chart));
        am.put(THICK_LINE_ACTION, applyLineThickNess(HasChart.LinesThickness.Thick).toAction(chart));
    }

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
}
