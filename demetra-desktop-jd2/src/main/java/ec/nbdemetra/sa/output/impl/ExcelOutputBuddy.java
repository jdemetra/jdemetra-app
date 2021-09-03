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

import ec.nbdemetra.sa.output.AbstractOutputNode;
import ec.nbdemetra.sa.output.INbOutputFactory;
import demetra.desktop.Config;
import demetra.desktop.ConfigEditor;
import ec.util.various.swing.FontAwesome;
import java.awt.Color;
import java.awt.Image;

import org.openide.util.lookup.ServiceProvider;
import demetra.desktop.actions.Resetable;
import demetra.desktop.Persistable;
import demetra.desktop.actions.Configurable;

/**
 *
 * @author Mats Maggi
 */
@ServiceProvider(service = INbOutputFactory.class, position = 1200)
public class ExcelOutputBuddy implements INbOutputFactory, Configurable, Persistable, ConfigEditor, Resetable {

//    private final BeanConfigurator<SpreadsheetOutputConfiguration, ExcelOutputBuddy> configurator = createConfigurator();
//    private SpreadsheetOutputConfiguration config = new SpreadsheetOutputConfiguration();
    @Override
    public String getName() {
//        return SpreadsheetOutputFactory.NAME;
        return "ExcelOutputBuddy";
    }

    @Override
    public AbstractOutputNode createNode() {
        return null;
//        return new ExcelNode(config);
    }

    @Override
    public AbstractOutputNode createNodeFor(Object properties) {
//        return properties instanceof SpreadsheetOutputConfiguration ? new ExcelNode((SpreadsheetOutputConfiguration) properties) : null;
        return null;
    }

    @Override
    public Config getConfig() {
//        return configurator.getConfig(this);
        return Config.builder("", "", "").build();
    }

    @Override
    public void setConfig(Config config) throws IllegalArgumentException {
//        configurator.setConfig(this, config);
    }

    @Override
    public Config editConfig(Config config) throws IllegalArgumentException {
//        return configurator.editConfig(config);
        return config;
    }

    @Override
    public void configure() {
        Configurable.configure(this, this);
    }

    @Override
    public void reset() {
//        config = new SpreadsheetOutputConfiguration();
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return FontAwesome.FA_FILE_EXCEL_O.getImage(Color.GREEN.darker(), 16);
    }

//    private static BeanConfigurator<SpreadsheetOutputConfiguration, ExcelOutputBuddy> createConfigurator() {
//        return new BeanConfigurator<>(new ExcelOutputBeanHandler(), new ExcelOutputConverter(), new ExcelOutputBeanEditor());
//    }
//
//    private static final class ExcelOutputBeanHandler implements BeanHandler<SpreadsheetOutputConfiguration, ExcelOutputBuddy> {
//
//        @Override
//        public SpreadsheetOutputConfiguration loadBean(ExcelOutputBuddy resource) {
//            return resource.config.clone();
//        }
//
//        @Override
//        public void storeBean(ExcelOutputBuddy resource, SpreadsheetOutputConfiguration bean) {
//            resource.config = bean;
//        }
//    }
//
//    private static final class ExcelOutputBeanEditor implements BeanEditor {
//
//        @Override
//        public boolean editBean(Object bean) throws IntrospectionException {
//            return new PropertySheetDialogBuilder()
//                    .title("Edit Excel output config")
//                    .editNode(new ExcelNode((SpreadsheetOutputConfiguration) bean));
//        }
//    }
//
//    private static final class ExcelOutputConverter implements Converter<SpreadsheetOutputConfiguration, Config> {
//
//        private final BooleanProperty saveModelParam = BooleanProperty.of("saveModel", false);
//        private final BooleanProperty verticalOrientationParam = BooleanProperty.of("verticalOrientation", true);
//        private final Property<SpreadsheetLayout> layoutParam = Property.of("layout", SpreadsheetLayout.BySeries, Parser.onEnum(SpreadsheetLayout.class), Formatter.onEnum());
//        private final Property<File> folderParam = Property.of("folder", new File(""), Parser.onFile(), Formatter.onFile());
//        private final Property<String> fileNameParam = Property.of("fileName", "series", Parser.onString(), Formatter.onString());
//        private final Property<String> seriesParam = Property.of("series", "y,t,sa,s,i,ycal", Parser.onString(), Formatter.onString());
//        private final BooleanProperty fullNameParam = BooleanProperty.of("fullName", true);
//
//        @Override
//        public Config doForward(SpreadsheetOutputConfiguration a) {
//            Config.Builder result = Config.builder(INbOutputFactory.class.getName(), "Excel", "");
//            saveModelParam.set(result::parameter, a.isSaveModel());
//            verticalOrientationParam.set(result::parameter, a.isVerticalOrientation());
//            layoutParam.set(result::parameter, a.getLayout());
//            folderParam.set(result::parameter, a.getFolder());
//            fileNameParam.set(result::parameter, a.getFileName());
//            seriesParam.set(result::parameter, a.getSeries().stream().collect(Collectors.joining(",")));
//            fullNameParam.set(result::parameter, a.isFullName());
//            return result.build();
//        }
//
//        @Override
//        public SpreadsheetOutputConfiguration doBackward(Config b) {
//            SpreadsheetOutputConfiguration result = new SpreadsheetOutputConfiguration();
//            result.setSaveModel(saveModelParam.get(b::getParameter));
//            result.setVerticalOrientation(verticalOrientationParam.get(b::getParameter));
//            result.setLayout(layoutParam.get(b::getParameter));
//            result.setFolder(folderParam.get(b::getParameter));
//            result.setFileName(fileNameParam.get(b::getParameter));
//            result.setSeries(Splitter.on(",").trimResults().splitToList(seriesParam.get(b::getParameter)));
//            result.setFullName(fullNameParam.get(b::getParameter));
//            return result;
//        }
//    }
//
//    public static final class ExcelNode extends AbstractOutputNode<SpreadsheetOutputConfiguration> {
//
//        public ExcelNode() {
//            super(new SpreadsheetOutputConfiguration());
//            setDisplayName(SpreadsheetOutputFactory.NAME);
//        }
//
//        public ExcelNode(SpreadsheetOutputConfiguration config) {
//            super(config);
//            setDisplayName(SpreadsheetOutputFactory.NAME);
//        }
//
//        @Override
//        protected Sheet createSheet() {
//            SpreadsheetOutputConfiguration config = getLookup().lookup(SpreadsheetOutputConfiguration.class);
//
//            Sheet sheet = super.createSheet();
//
//            NodePropertySetBuilder builder = new NodePropertySetBuilder();
//            builder.reset("Location");
//            builder.withFile().select(config, "Folder").directories(true).description("Base output folder. Will be extended by the workspace and processing names").add();
//            builder.with(String.class).select(config, "fileName").display("File Name").add();
//            sheet.put(builder.build());
//
//            builder.reset("Layout");
//            builder.withEnum(SpreadsheetLayout.class).select(config, "Layout").add();
//            builder.withBoolean().select(config, "VerticalOrientation").add();
//            builder.withBoolean().select(config, "FullName").display("Full series name")
//                    .description("If true, the fully qualified name of the series will be used (workbook + sheet + name). "
//                            + "If false, only the name of the series will be displayed.").add();
//            sheet.put(builder.build());
//
//            builder.reset("Content");
//            builder.with(List.class).select(config, "Series").editor(Series.class).add();
//            sheet.put(builder.build());
//            return sheet;
//        }
//
//        @Override
//        public ISaOutputFactory getFactory() {
//            return new SpreadsheetOutputFactory(getLookup().lookup(SpreadsheetOutputConfiguration.class));
//        }
//    }
}
