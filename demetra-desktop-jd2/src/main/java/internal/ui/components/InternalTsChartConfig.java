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

import com.google.common.base.Preconditions;
import demetra.ui.components.parts.HasChart;
import demetra.ui.beans.BeanHandler;
import demetra.ui.Config;
import ec.nbdemetra.ui.DemetraUI;
import demetra.ui.completion.JAutoCompletionService;
import demetra.ui.properties.NodePropertySetBuilder;
import demetra.ui.properties.PropertySheetDialogBuilder;
import demetra.ui.components.JTsChart;
import ec.util.chart.ColorScheme;
import java.beans.IntrospectionException;
import nbbrd.io.text.BooleanProperty;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import nbbrd.io.text.Property;
import org.openide.nodes.Sheet;
import demetra.ui.properties.BeanEditor;
import demetra.ui.Converter;
import demetra.ui.beans.BeanConfigurator;

/**
 *
 * @author Philippe Charles
 */
public final class InternalTsChartConfig {

    public boolean legendVisible = true;
    public boolean titleVisible = true;
    public boolean axisVisible = true;
    public String title = "";
    public HasChart.LinesThickness linesThickness = HasChart.LinesThickness.Thin;
    public String colorSchemeName = "";
    public double[] zoom = new double[0];

    ColorScheme getColorScheme() {
        if (colorSchemeName.isEmpty()) {
            return null;
        }
        return DemetraUI.getDefault().getColorSchemes().stream()
                .filter(o -> colorSchemeName.equals(o.getName()))
                .findFirst()
                .orElse(null);
    }

    public static final BeanConfigurator<InternalTsChartConfig, JTsChart> CONFIGURATOR = new BeanConfigurator<>(new TsChartConfigHandler(), new TsChartConfigConverter(), new TsChartConfigEditor());

    private static final class TsChartConfigHandler implements BeanHandler<InternalTsChartConfig, JTsChart> {

        @Override
        public InternalTsChartConfig loadBean(JTsChart r) {
            ColorScheme colorScheme = r.getColorScheme();
            InternalTsChartConfig result = new InternalTsChartConfig();
            result.legendVisible = r.isLegendVisible();
            result.titleVisible = r.isTitleVisible();
            result.axisVisible = r.isAxisVisible();
            result.title = r.getTitle();
            result.linesThickness = r.getLinesThickness();
            result.colorSchemeName = colorScheme != null ? colorScheme.getName() : "";
//            result.zoom = r.chartPanel.getZoom();
            return result;
        }

        @Override
        public void storeBean(JTsChart resource, InternalTsChartConfig bean) {
            resource.setLegendVisible(bean.legendVisible);
            resource.setTitleVisible(bean.titleVisible);
            resource.setAxisVisible(bean.axisVisible);
            resource.setTitle(bean.title);
            resource.setLinesThickness(bean.linesThickness);
            resource.setColorScheme(bean.getColorScheme());
//            resource.chartPanel.setZoom(bean.zoom);
        }
    }

    private static final class TsChartConfigEditor implements BeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            b.reset("Chart display");
            b.withBoolean().selectField(bean, "legendVisible").display("Show legend").description("Whether the legend should be shown or not").add();
            b.withBoolean().selectField(bean, "titleVisible").display("Show title").description("Whether the title should be shown or not").add();
            b.withBoolean().selectField(bean, "axisVisible").display("Show axis").description("Whether the axis should be shown or not").add();
            b.with(String.class).selectField(bean, "title").display("Title").description("Title of the chart").add();
            sheet.put(b.build());
            b.reset("Series display");
            b.withEnum(HasChart.LinesThickness.class).selectField(bean, "linesThickness").display("Line thickness").description("Thickness of the line representing the series").add();
            b.withAutoCompletion().selectField(bean, "colorSchemeName").servicePath(JAutoCompletionService.COLOR_SCHEME_PATH).promptText(DemetraUI.getDefault().getColorSchemeName()).display("Color scheme").add();

            sheet.put(b.build());
            return new PropertySheetDialogBuilder().title("Configure chart").editSheet(sheet);
        }
    }

    private static final class TsChartConfigConverter implements Converter<InternalTsChartConfig, Config> {

        private static final String DOMAIN = "ec.ui.chart.JTsChart", NAME = "", VERSION = "";
        private static final BooleanProperty LEGEND_VISIBLE = BooleanProperty.of("legendVisible", true);
        private static final BooleanProperty TITLE_VISIBLE = BooleanProperty.of("titleVisible", true);
        private static final BooleanProperty AXIS_VISIBLE = BooleanProperty.of("axisVisible", true);
        private static final Property<String> TITLE = Property.of("title", "", Parser.onString(), Formatter.onString());
        private static final Property<HasChart.LinesThickness> LINES_THICKNESS = Property.of("linesThickness", HasChart.LinesThickness.Thin, Parser.onEnum(HasChart.LinesThickness.class), Formatter.onEnum());
        private static final Property<String> COLOR_SCHEME_NAME = Property.of("colorSchemeName", "", Parser.onString(), Formatter.onString());
        private static final Property<double[]> ZOOM = Property.of("zoom", new double[0], Parser.onDoubleArray(), Formatter.onDoubleArray());

        @Override
        public Config doForward(InternalTsChartConfig a) {
            Config.Builder b = Config.builder(DOMAIN, NAME, VERSION);
            LEGEND_VISIBLE.set(b::parameter, a.legendVisible);
            TITLE_VISIBLE.set(b::parameter, a.titleVisible);
            AXIS_VISIBLE.set(b::parameter, a.axisVisible);
            TITLE.set(b::parameter, a.title);
            LINES_THICKNESS.set(b::parameter, a.linesThickness);
            COLOR_SCHEME_NAME.set(b::parameter, a.colorSchemeName);
            ZOOM.set(b::parameter, a.zoom);
            return b.build();
        }

        @Override
        public InternalTsChartConfig doBackward(Config config) {
            Preconditions.checkArgument(DOMAIN.equals(config.getDomain()), "Not produced here");
            InternalTsChartConfig result = new InternalTsChartConfig();
            result.legendVisible = LEGEND_VISIBLE.get(config::getParameter);
            result.titleVisible = TITLE_VISIBLE.get(config::getParameter);
            result.axisVisible = AXIS_VISIBLE.get(config::getParameter);
            result.title = TITLE.get(config::getParameter);
            result.linesThickness = LINES_THICKNESS.get(config::getParameter);
            result.colorSchemeName = COLOR_SCHEME_NAME.get(config::getParameter);
            result.zoom = ZOOM.get(config::getParameter);
            return result;
        }
    }
}
