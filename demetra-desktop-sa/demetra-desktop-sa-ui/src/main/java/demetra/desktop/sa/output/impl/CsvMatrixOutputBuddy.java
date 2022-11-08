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
package demetra.desktop.sa.output.impl;

import com.google.common.base.Splitter;
import demetra.desktop.beans.BeanHandler;
import demetra.desktop.Config;
import demetra.desktop.ConfigEditor;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.desktop.properties.NodePropertySetBuilder;
import java.beans.IntrospectionException;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.ServiceProvider;
import demetra.desktop.actions.Resetable;
import nbbrd.io.text.BooleanProperty;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import nbbrd.io.text.Property;
import demetra.desktop.beans.BeanEditor;
import demetra.desktop.Converter;
import demetra.desktop.Persistable;
import demetra.desktop.actions.Configurable;
import demetra.desktop.beans.BeanConfigurator;
import demetra.desktop.sa.output.AbstractOutputNode;
import demetra.desktop.sa.output.Matrix;
import demetra.sa.csv.CsvMatrixOutputConfiguration;
import demetra.sa.csv.CsvMatrixOutputFactory;
import demetra.desktop.sa.output.OutputFactoryBuddy;
import demetra.desktop.sa.output.OutputSelection;
import demetra.sa.SaManager;
import demetra.sa.SaOutputFactory;

/**
 *
 * @author Mats Maggi
 */
@ServiceProvider(service = OutputFactoryBuddy.class, position = 1100)
public class CsvMatrixOutputBuddy implements OutputFactoryBuddy, Configurable, Persistable, ConfigEditor, Resetable {

    private static final BeanConfigurator<CsvMatrixOutputConfiguration, CsvMatrixOutputBuddy> configurator = createConfigurator();
    private CsvMatrixOutputConfiguration config = new CsvMatrixOutputConfiguration();

    public CsvMatrixOutputBuddy(){
        config.setItems(OutputSelection.matrixItems(SaManager.processors()));
    }
    
    @Override
    public String getName() {
        return CsvMatrixOutputFactory.NAME;
    }

    @Override
    public AbstractOutputNode createNode() {
        return new CsvMatrixNode(config);
    }

    @Override
    public AbstractOutputNode createNodeFor(SaOutputFactory factory) {
        return factory instanceof CsvMatrixOutputFactory ? new CsvMatrixNode(((CsvMatrixOutputFactory) factory).getConfiguration()) : null;
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
    public void configure() {
        Configurable.configure(this, this);
    }

    @Override
    public void reset() {
        config = new CsvMatrixOutputConfiguration();
    }

    private static BeanConfigurator<CsvMatrixOutputConfiguration, CsvMatrixOutputBuddy> createConfigurator() {
        return new BeanConfigurator<>(new CsvMatrixOutputBeanHandler(), new CsvMatrixOutputConverter(), new CsvMatrixOutputBeanEditor());
    }

    private static final class CsvMatrixOutputBeanHandler implements BeanHandler<CsvMatrixOutputConfiguration, CsvMatrixOutputBuddy> {

        @Override
        public CsvMatrixOutputConfiguration load(CsvMatrixOutputBuddy resource) {
            return resource.config.clone();
        }

        @Override
        public void store(CsvMatrixOutputBuddy resource, CsvMatrixOutputConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class CsvMatrixOutputBeanEditor implements BeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit Csv Matrix output config")
                    .editNode(new CsvMatrixNode((CsvMatrixOutputConfiguration) bean));
        }
    }

    private static final class CsvMatrixOutputConverter implements Converter<CsvMatrixOutputConfiguration, Config> {

        private final Property<File> folderParam = Property.of("folder", new File(""), Parser.onFile(), Formatter.onFile());
        private final Property<String> fileNameParam = Property.of("fileName", "series", Parser.onString(), Formatter.onString());
        private final Property<String> seriesParam = Property.of("items", "y,t,sa,s,i,ycal", Parser.onString(), Formatter.onString());
        private final BooleanProperty fullNameParam = BooleanProperty.of("fullName", true);

        @Override
        public Config doForward(CsvMatrixOutputConfiguration a) {
            Config.Builder result = Config.builder(OutputFactoryBuddy.class.getName(), "Csv_Matrix", "");
            folderParam.set(result::parameter, a.getFolder());
            fileNameParam.set(result::parameter, a.getFileName());
            seriesParam.set(result::parameter, a.getItems().stream().collect(Collectors.joining(",")));
            fullNameParam.set(result::parameter, a.isFullName());
            return result.build();
        }

        @Override
        public CsvMatrixOutputConfiguration doBackward(Config b) {
            CsvMatrixOutputConfiguration result = new CsvMatrixOutputConfiguration();
            result.setFolder(folderParam.get(b::getParameter));
            result.setFileName(fileNameParam.get(b::getParameter));
            result.setItems(Splitter.on(",").trimResults().splitToList(seriesParam.get(b::getParameter)));
            result.setFullName(fullNameParam.get(b::getParameter));
            return result;
        }
    }

    public final static class CsvMatrixNode extends AbstractOutputNode<CsvMatrixOutputConfiguration> {
        
        private static CsvMatrixOutputConfiguration newConfiguration(){
            CsvMatrixOutputConfiguration config = new CsvMatrixOutputConfiguration();
            config.setItems(OutputSelection.matrixItems(SaManager.processors()));
            return config;
        }

        public CsvMatrixNode() {
            super(newConfiguration());
            setDisplayName(CsvMatrixOutputFactory.NAME);
        }

        public CsvMatrixNode(CsvMatrixOutputConfiguration config) {
            super(config);
            setDisplayName(CsvMatrixOutputFactory.NAME);
        }

        @Override
        protected Sheet createSheet() {
            CsvMatrixOutputConfiguration config = getLookup().lookup(CsvMatrixOutputConfiguration.class);
            Sheet sheet = super.createSheet();

            NodePropertySetBuilder builder = new NodePropertySetBuilder();
            builder.reset("Location");
            builder.withFile().select(config, "Folder").directories(true).description("Base output folder. Will be extended by the workspace and processing names").add();
            builder.with(String.class).select(config, "fileName").display("File Name").add();
            sheet.put(builder.build());

            builder.reset("Content");
            builder.with(List.class).select(config, "Items").editor(Matrix.class).add();
            sheet.put(builder.build());

            builder.reset("Layout");
            builder.withBoolean().select(config, "FullName").display("Full series name")
                    .description("If true, the fully qualified name of the series will be used. "
                            + "If false, only the name of the series will be displayed.").add();
            sheet.put(builder.build());

            return sheet;
        }

        @Override
        public SaOutputFactory getFactory() {
            return new CsvMatrixOutputFactory(getLookup().lookup(CsvMatrixOutputConfiguration.class));
        }
    }
}
