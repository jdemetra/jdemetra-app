/*
 * Copyright 2016 National Bank of Belgium
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
package ec.nbdemetra.sa.output.impl;

import com.google.common.base.Converter;
import com.google.common.base.Splitter;
import ec.nbdemetra.sa.output.AbstractOutputNode;
import ec.nbdemetra.sa.output.INbOutputFactory;
import ec.nbdemetra.sa.output.Series;
import ec.nbdemetra.ui.BeanHandler;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.IConfigurable;
import demetra.ui.properties.PropertySheetDialogBuilder;
import demetra.ui.properties.IBeanEditor;
import demetra.ui.properties.NodePropertySetBuilder;
import ec.tss.sa.ISaOutputFactory;
import ec.tss.sa.output.SpreadsheetOutputConfiguration;
import ec.tss.sa.output.SpreadsheetOutputConfiguration.SpreadsheetLayout;
import ec.tss.sa.output.SpreadsheetOutputFactory;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import ec.util.various.swing.FontAwesome;
import java.awt.Color;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.ServiceProvider;
import demetra.ui.actions.Resetable;

/**
 *
 * @author Mats Maggi
 */
@ServiceProvider(service = INbOutputFactory.class, position = 1200)
public class ExcelOutputBuddy implements INbOutputFactory, IConfigurable, Resetable {

    private final Configurator<ExcelOutputBuddy> configurator = createConfigurator();
    private SpreadsheetOutputConfiguration config = new SpreadsheetOutputConfiguration();

    @Override
    public String getName() {
        return SpreadsheetOutputFactory.NAME;
    }

    @Override
    public AbstractOutputNode createNode() {
        return new ExcelNode(config);
    }

    @Override
    public AbstractOutputNode createNodeFor(Object properties) {
        return properties instanceof SpreadsheetOutputConfiguration ? new ExcelNode((SpreadsheetOutputConfiguration) properties) : null;
    }

    @Override
    public Config getConfig() {
        return configurator.getConfig(this);
    }

    @Override
    public void setConfig(Config config) throws IllegalArgumentException {
        configurator.setConfig(this, config);
    }

    @Override
    public Config editConfig(Config config) throws IllegalArgumentException {
        return configurator.editConfig(config);
    }

    @Override
    public void reset() {
        config = new SpreadsheetOutputConfiguration();
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return FontAwesome.FA_FILE_EXCEL_O.getImage(Color.GREEN.darker(), 16);
    }

    private static Configurator<ExcelOutputBuddy> createConfigurator() {
        return new ExcelOutputBeanHandler().toConfigurator(new ExcelOutputConverter(), new ExcelOutputBeanEditor());
    }

    private static final class ExcelOutputBeanHandler extends BeanHandler<SpreadsheetOutputConfiguration, ExcelOutputBuddy> {

        @Override
        public SpreadsheetOutputConfiguration loadBean(ExcelOutputBuddy resource) {
            return resource.config.clone();
        }

        @Override
        public void storeBean(ExcelOutputBuddy resource, SpreadsheetOutputConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class ExcelOutputBeanEditor implements IBeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit Excel output config")
                    .editNode(new ExcelNode((SpreadsheetOutputConfiguration) bean));
        }
    }

    private static final class ExcelOutputConverter extends Converter<SpreadsheetOutputConfiguration, Config> {

        private final IParam<Config, Boolean> saveModelParam = Params.onBoolean(false, "saveModel");
        private final IParam<Config, Boolean> verticalOrientationParam = Params.onBoolean(true, "verticalOrientation");
        private final IParam<Config, SpreadsheetLayout> layoutParam = Params.onEnum(SpreadsheetLayout.BySeries, "layout");
        private final IParam<Config, File> folderParam = Params.onFile(new File(""), "folder");
        private final IParam<Config, String> fileNameParam = Params.onString("series", "fileName");
        private final IParam<Config, String> seriesParam = Params.onString("y,t,sa,s,i,ycal", "series");
        private final IParam<Config, Boolean> fullNameParam = Params.onBoolean(true, "fullName");

        @Override
        protected Config doForward(SpreadsheetOutputConfiguration a) {
            Config.Builder result = Config.builder(INbOutputFactory.class.getName(), "Excel", "");
            saveModelParam.set(result, a.isSaveModel());
            verticalOrientationParam.set(result, a.isVerticalOrientation());
            layoutParam.set(result, a.getLayout());
            folderParam.set(result, a.getFolder());
            fileNameParam.set(result, a.getFileName());
            seriesParam.set(result, a.getSeries().stream().collect(Collectors.joining(",")));
            fullNameParam.set(result, a.isFullName());
            return result.build();
        }

        @Override
        protected SpreadsheetOutputConfiguration doBackward(Config b) {
            SpreadsheetOutputConfiguration result = new SpreadsheetOutputConfiguration();
            result.setSaveModel(saveModelParam.get(b));
            result.setVerticalOrientation(verticalOrientationParam.get(b));
            result.setLayout(layoutParam.get(b));
            result.setFolder(folderParam.get(b));
            result.setFileName(fileNameParam.get(b));
            result.setSeries(Splitter.on(",").trimResults().splitToList(seriesParam.get(b)));
            result.setFullName(fullNameParam.get(b));
            return result;
        }
    }

    public static final class ExcelNode extends AbstractOutputNode<SpreadsheetOutputConfiguration> {

        public ExcelNode() {
            super(new SpreadsheetOutputConfiguration());
            setDisplayName(SpreadsheetOutputFactory.NAME);
        }

        public ExcelNode(SpreadsheetOutputConfiguration config) {
            super(config);
            setDisplayName(SpreadsheetOutputFactory.NAME);
        }

        @Override
        protected Sheet createSheet() {
            SpreadsheetOutputConfiguration config = getLookup().lookup(SpreadsheetOutputConfiguration.class);

            Sheet sheet = super.createSheet();

            NodePropertySetBuilder builder = new NodePropertySetBuilder();
            builder.reset("Location");
            builder.withFile().select(config, "Folder").directories(true).description("Base output folder. Will be extended by the workspace and processing names").add();
            builder.with(String.class).select(config, "fileName").display("File Name").add();
            sheet.put(builder.build());

            builder.reset("Layout");
            builder.withEnum(SpreadsheetLayout.class).select(config, "Layout").add();
            builder.withBoolean().select(config, "VerticalOrientation").add();
            builder.withBoolean().select(config, "FullName").display("Full series name")
                    .description("If true, the fully qualified name of the series will be used (workbook + sheet + name). "
                            + "If false, only the name of the series will be displayed.").add();
            sheet.put(builder.build());

            builder.reset("Content");
            builder.with(List.class).select(config, "Series").editor(Series.class).add();
            sheet.put(builder.build());
            return sheet;
        }

        @Override
        public ISaOutputFactory getFactory() {
            return new SpreadsheetOutputFactory(getLookup().lookup(SpreadsheetOutputConfiguration.class));
        }
    }
}
