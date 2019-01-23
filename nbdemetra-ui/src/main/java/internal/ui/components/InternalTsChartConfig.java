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

import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import demetra.ui.components.HasChart;
import ec.nbdemetra.ui.BeanHandler;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.DemetraUI;
import demetra.ui.completion.JAutoCompletionService;
import demetra.ui.properties.IBeanEditor;
import demetra.ui.properties.NodePropertySetBuilder;
import demetra.ui.properties.PropertySheetDialogBuilder;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import demetra.ui.components.JTsChart;
import ec.util.chart.ColorScheme;
import java.beans.IntrospectionException;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
public final class InternalTsChartConfig {

    public boolean legendVisible = HasChartImpl.DEFAULT_LEGENDVISIBLE;
    public boolean titleVisible = HasChartImpl.DEFAULT_TITLEVISIBLE;
    public boolean axisVisible = HasChartImpl.DEFAULT_AXISVISIBLE;
    public String title = HasChartImpl.DEFAULT_TITLE;
    public HasChart.LinesThickness linesThickness = HasChartImpl.DEFAULT_LINES_THICKNESS;
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

    public static final Configurator<JTsChart> CONFIGURATOR = new TsChartConfigHandler()
            .toConfigurator(new TsChartConfigConverter(), new TsChartConfigEditor());

    private static final class TsChartConfigHandler extends BeanHandler<InternalTsChartConfig, JTsChart> {

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

    private static final class TsChartConfigEditor implements IBeanEditor {

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

    private static final class TsChartConfigConverter extends Converter<InternalTsChartConfig, Config> {

        private static final String DOMAIN = "ec.ui.chart.JTsChart", NAME = "", VERSION = "";
        private static final IParam<Config, Boolean> LEGEND_VISIBLE = Params.onBoolean(HasChartImpl.DEFAULT_LEGENDVISIBLE, "legendVisible");
        private static final IParam<Config, Boolean> TITLE_VISIBLE = Params.onBoolean(HasChartImpl.DEFAULT_TITLEVISIBLE, "titleVisible");
        private static final IParam<Config, Boolean> AXIS_VISIBLE = Params.onBoolean(HasChartImpl.DEFAULT_AXISVISIBLE, "axisVisible");
        private static final IParam<Config, String> TITLE = Params.onString(HasChartImpl.DEFAULT_TITLE, "title");
        private static final IParam<Config, HasChart.LinesThickness> LINES_THICKNESS = Params.onEnum(HasChartImpl.DEFAULT_LINES_THICKNESS, "linesThickness");
        private static final IParam<Config, String> COLOR_SCHEME_NAME = Params.onString("", "colorSchemeName");
        private static final IParam<Config, double[]> ZOOM = Params.onDoubleArray("zoom", new double[0]);

        @Override
        protected Config doForward(InternalTsChartConfig a) {
            Config.Builder b = Config.builder(DOMAIN, NAME, VERSION);
            LEGEND_VISIBLE.set(b, a.legendVisible);
            TITLE_VISIBLE.set(b, a.titleVisible);
            AXIS_VISIBLE.set(b, a.axisVisible);
            TITLE.set(b, a.title);
            LINES_THICKNESS.set(b, a.linesThickness);
            COLOR_SCHEME_NAME.set(b, a.colorSchemeName);
            ZOOM.set(b, a.zoom);
            return b.build();
        }

        @Override
        protected InternalTsChartConfig doBackward(Config config) {
            Preconditions.checkArgument(DOMAIN.equals(config.getDomain()), "Not produced here");
            InternalTsChartConfig result = new InternalTsChartConfig();
            result.legendVisible = LEGEND_VISIBLE.get(config);
            result.titleVisible = TITLE_VISIBLE.get(config);
            result.axisVisible = AXIS_VISIBLE.get(config);
            result.title = TITLE.get(config);
            result.linesThickness = LINES_THICKNESS.get(config);
            result.colorSchemeName = COLOR_SCHEME_NAME.get(config);
            result.zoom = ZOOM.get(config);
            return result;
        }
    }
}
