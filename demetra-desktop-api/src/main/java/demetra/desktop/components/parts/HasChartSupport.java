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
package demetra.desktop.components.parts;

import demetra.desktop.DemetraIcons;
import demetra.desktop.beans.PropertyChangeBroadcaster;
import demetra.desktop.components.ComponentCommand;
import ec.util.various.swing.FontAwesome;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;

/**
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class HasChartSupport {

    @NonNull
    public static HasChart of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasChartImpl(broadcaster);
    }

    public static void registerActions(HasChart chart, ActionMap am) {
        am.put(HasChart.TOGGLE_TITLE_VISIBILITY_ACTION, ToggleTitleVisibilityCommand.INSTANCE.toAction(chart));
        am.put(HasChart.TOGGLE_LEGEND_VISIBILITY_ACTION, ToggleLegendVisibilityCommand.INSTANCE.toAction(chart));
        am.put(HasChart.APPLY_THIN_LINE_ACTION, ApplyLineThickNessCommand.THIN.toAction(chart));
        am.put(HasChart.APPLY_THICK_LINE_ACTION, ApplyLineThickNessCommand.THICK.toAction(chart));
    }

    public static <C extends JComponent & HasChart> JMenuItem newToggleTitleVisibilityMenu(C component) {
        JMenuItem result = new JCheckBoxMenuItem(component.getActionMap().get(HasChart.TOGGLE_TITLE_VISIBILITY_ACTION));
        result.setText("Show title");
        result.setIcon(DemetraIcons.getPopupMenuIcon(FontAwesome.FA_FONT));
        return result;
    }

    public static <C extends JComponent & HasChart> JMenuItem newToggleLegendVisibilityMenu(C component) {
        JMenuItem result = new JCheckBoxMenuItem(component.getActionMap().get(HasChart.TOGGLE_LEGEND_VISIBILITY_ACTION));
        result.setText("Show legend");
        return result;
    }

    public static <C extends JComponent & HasChart> JMenu newLinesThicknessMenu(C component) {
        JMenu result = new JMenu("Lines thickness");
        result.add(new JCheckBoxMenuItem(component.getActionMap().get(HasChart.APPLY_THIN_LINE_ACTION))).setText("Thin");
        result.add(new JCheckBoxMenuItem(component.getActionMap().get(HasChart.APPLY_THICK_LINE_ACTION))).setText("Thick");
        return result;
    }

    private static final class ToggleTitleVisibilityCommand extends ComponentCommand<HasChart> {

        public static final ToggleTitleVisibilityCommand INSTANCE = new ToggleTitleVisibilityCommand();

        public ToggleTitleVisibilityCommand() {
            super(HasChart.TITLE_VISIBLE_PROPERTY);
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
            super(HasChart.LEGEND_VISIBLE_PROPERTY);
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

        private final HasChart.LinesThickness value;

        public ApplyLineThickNessCommand(HasChart.LinesThickness value) {
            super(HasChart.LINES_THICKNESS_PROPERTY);
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

    @lombok.RequiredArgsConstructor
    private static final class HasChartImpl implements HasChart {

        @lombok.NonNull
        private final PropertyChangeBroadcaster broadcaster;

        private static final boolean DEFAULT_LEGEND_VISIBLE = true;
        private boolean legendVisible = DEFAULT_LEGEND_VISIBLE;

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

        private static final boolean DEFAULT_TITLE_VISIBLE = true;
        private boolean titleVisible = DEFAULT_TITLE_VISIBLE;

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

        private static final boolean DEFAULT_AXIS_VISIBLE = true;
        private boolean axisVisible = DEFAULT_AXIS_VISIBLE;

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

        private static final String DEFAULT_TITLE = "";
        private String title = DEFAULT_TITLE;

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

        private static final HasChart.LinesThickness DEFAULT_LINES_THICKNESS = HasChart.LinesThickness.Thin;
        private HasChart.LinesThickness linesThickness = DEFAULT_LINES_THICKNESS;

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
}
