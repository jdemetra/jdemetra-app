/*
 * Copyright 2018 National Bank of Belgium
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

import demetra.ui.beans.PropertyChangeSource;
import demetra.ui.components.HasChart;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class HasChartImpl implements HasChart {

    public static final boolean DEFAULT_LEGENDVISIBLE = true;
    public static final boolean DEFAULT_TITLEVISIBLE = true;
    public static final boolean DEFAULT_AXISVISIBLE = true;
    public static final String DEFAULT_TITLE = "";
    public static final LinesThickness DEFAULT_LINES_THICKNESS = LinesThickness.Thin;

    @lombok.NonNull
    private final PropertyChangeSource.Broadcaster broadcaster;

    private boolean legendVisible = DEFAULT_LEGENDVISIBLE;
    private boolean titleVisible = DEFAULT_TITLEVISIBLE;
    private boolean axisVisible = DEFAULT_AXISVISIBLE;
    private String title = DEFAULT_TITLE;
    private LinesThickness linesThickness = DEFAULT_LINES_THICKNESS;

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
    public LinesThickness getLinesThickness() {
        return linesThickness;
    }

    @Override
    public void setLinesThickness(LinesThickness linesThickness) {
        LinesThickness old = this.linesThickness;
        this.linesThickness = linesThickness != null ? linesThickness : DEFAULT_LINES_THICKNESS;
        broadcaster.firePropertyChange(LINES_THICKNESS_PROPERTY, old, this.linesThickness);
    }
}
