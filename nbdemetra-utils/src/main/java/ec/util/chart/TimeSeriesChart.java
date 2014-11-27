/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package ec.util.chart;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Defines the features of a time series chart.
 *
 * @author Philippe Charles
 * @param <DS> the type of the data source
 * @param <COLORS> the type of the color scheme support
 */
public interface TimeSeriesChart<DS, COLORS extends ColorSchemeSupport> {

    @Nonnull
    DS getDataset();

    @Nonnull
    COLORS getColorSchemeSupport();

    @Nonnull
    String getTitle();

    @Nonnull
    String getNoDataMessage();

    boolean isElementVisible(@Nonnull Element element);

    @Nonnull
    int[] getPlotWeights();

    @Nonnull
    SeriesFunction<Integer> getPlotDispatcher();

    float getLineThickness();

    @Nonnull
    DateFormat getPeriodFormat();

    @Nonnull
    NumberFormat getValueFormat();

    @Nonnull
    SeriesFunction<RendererType> getSeriesRenderer();

    @Nonnull
    SeriesFunction<String> getSeriesFormatter();

    @Nonnull
    ObsFunction<String> getObsFormatter();

    @Nonnull
    ObsPredicate getDashPredicate();

    @Nonnull
    SeriesPredicate getLegendVisibilityPredicate();

    @Nonnull
    CrosshairType getCrosshairType();

    @Nonnull
    ObsIndex getActiveObs();

    @Nonnull
    ObsPredicate getObsHighlighter();

    void setDataset(@Nullable DS dataset);

    void setColorSchemeSupport(@Nullable COLORS colorSchemeSupport);

    void setTitle(@Nullable String title);

    void setNoDataMessage(@Nullable String noDataMessage);

    void setElementVisible(@Nonnull Element element, boolean visible);

    void setPlotWeights(@Nullable int[] weights);

    void setPlotDispatcher(@Nullable SeriesFunction<Integer> plotDispatcher);

    void setLineThickness(float lineThickness);

    void setPeriodFormat(@Nullable DateFormat periodFormat);

    void setValueFormat(@Nullable NumberFormat valueFormat);

    void setSeriesRenderer(@Nullable SeriesFunction<RendererType> renderer);

    void setSeriesFormatter(@Nullable SeriesFunction<String> formatter);

    void setObsFormatter(@Nullable ObsFunction<String> formatter);

    void setDashPredicate(@Nullable ObsPredicate predicate);

    void setLegendVisibilityPredicate(@Nullable SeriesPredicate predicate);

    void setCrosshairType(@Nullable CrosshairType crosshairType);

    void setActiveObs(@Nullable ObsIndex activeObs);

    void setObsHighlighter(@Nullable ObsPredicate obsHighlighter);

    void copyImage() throws IOException;

    void saveImage() throws IOException;

    void printImage() throws IOException;

    @Nonnull
    EnumSet<RendererType> getSupportedRendererTypes();

    enum Element {

        TITLE, LEGEND, AXIS, TOOLTIP
    }

    enum RendererType {

        LINE, STACKED_LINE,
        SPLINE, STACKED_SPLINE,
        COLUMN, STACKED_COLUMN,
        AREA, STACKED_AREA,
        MARKER
    }

    enum CrosshairType {

        ALL, RANGE, DOMAIN, NONE;
    }
}
