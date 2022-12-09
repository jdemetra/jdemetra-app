/*
* Copyright 2016 National Bank of Belgium
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
import demetra.desktop.sa.output.OutputFactoryBuddy;
import demetra.desktop.sa.output.OutputSelection;
import demetra.desktop.sa.output.Series;
import demetra.sa.SaManager;
import demetra.sa.SaOutputFactory;
import demetra.sa.csv.CsvLayout;
import demetra.sa.csv.CsvOutputConfiguration;
import demetra.sa.csv.CsvOutputFactory;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = OutputFactoryBuddy.class, position = 1000)
public final class CsvOutputBuddy implements OutputFactoryBuddy, Configurable, ConfigEditor, Resetable {

    private final BeanConfigurator<CsvOutputConfiguration, CsvOutputBuddy> configurator = createConfigurator();
    private CsvOutputConfiguration config = new CsvOutputConfiguration();
    
    public CsvOutputBuddy(){
    }

    @Override
    public AbstractOutputNode createNode() {
        return new CsvNode(config);
    }

    @Override
    public String getName() {
        return CsvOutputFactory.NAME;
    }

    @Override
    public AbstractOutputNode createNodeFor(Object properties) {
        return properties instanceof CsvOutputConfiguration ? new CsvNode((CsvOutputConfiguration) properties) : null;
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
        config = new CsvOutputConfiguration();
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static BeanConfigurator<CsvOutputConfiguration, CsvOutputBuddy> createConfigurator() {
        return new BeanConfigurator<>(new CsvOutputBeanHandler(), new CsvOutputConverter(), new CsvOutputBeanEditor());
    }

    private static final class CsvOutputBeanHandler implements BeanHandler<CsvOutputConfiguration, CsvOutputBuddy> {

        @Override
        public CsvOutputConfiguration load(CsvOutputBuddy resource) {
            return resource.config.clone();
        }

        @Override
        public void store(CsvOutputBuddy resource, CsvOutputConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class CsvOutputBeanEditor implements BeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit csv output config")
                    .editNode(new CsvNode((CsvOutputConfiguration) bean));
        }
    }

    private static final class CsvOutputConverter implements Converter<CsvOutputConfiguration, Config> {

        private final Property<CsvLayout> presentationParam = Property.of("presentation", CsvLayout.List, Parser.onEnum(CsvLayout.class), Formatter.onEnum());
        private final Property<File> folderParam = Property.of("folder", new File(""), Parser.onFile(), Formatter.onFile());
        private final Property<String> filePrefixParam = Property.of("filePrefix", "series", Parser.onString(), Formatter.onString());
        private final Property<String> seriesParam = Property.of("series", "y,t,sa,s,i,ycal", Parser.onString(), Formatter.onString());
        private final BooleanProperty fullNameParam = BooleanProperty.of("fullName", true);

        @Override
        public Config doForward(CsvOutputConfiguration a) {
            Config.Builder result = Config.builder("outputs", "csv", "3.0");
            presentationParam.set(result::parameter, a.getPresentation());
            folderParam.set(result::parameter, a.getFolder());
            filePrefixParam.set(result::parameter, a.getFilePrefix());
            seriesParam.set(result::parameter, a.getSeries().stream().collect(Collectors.joining(",")));
            fullNameParam.set(result::parameter, a.isFullName());
            return result.build();
        }

        @Override
        public CsvOutputConfiguration doBackward(Config b) {
            CsvOutputConfiguration result = new CsvOutputConfiguration();
            result.setPresentation(presentationParam.get(b::getParameter));
            result.setFolder(folderParam.get(b::getParameter));
            result.setFilePrefix(filePrefixParam.get(b::getParameter));
            result.setSeries(Splitter.on(",").trimResults().splitToList(seriesParam.get(b::getParameter)));
            result.setFullName(fullNameParam.get(b::getParameter));
            return result;
        }
    }

    private final static class CsvNode extends AbstractOutputNode<CsvOutputConfiguration> {

        private static CsvOutputConfiguration newConfiguration(){
            CsvOutputConfiguration config = new CsvOutputConfiguration();
            config.setSeries(OutputSelection.seriesItems(SaManager.processors()));
            return config;
        }

        public CsvNode() {
            super(newConfiguration());
            setDisplayName(CsvOutputFactory.NAME);
        }

        public CsvNode(CsvOutputConfiguration config) {
            super(config);
            setDisplayName(CsvOutputFactory.NAME);
        }

        @Override
        protected Sheet createSheet() {
            CsvOutputConfiguration config = getLookup().lookup(CsvOutputConfiguration.class);

            Sheet sheet = super.createSheet();
            NodePropertySetBuilder builder = new NodePropertySetBuilder();

            builder.reset("Location");
            builder.withFile().select(config, "Folder").directories(true).description("Base output folder. Will be extended by the workspace and processing names").add();
            builder.with(String.class).select(config, "filePrefix").display("File Prefix").add();
            sheet.put(builder.build());

            builder.reset("Layout");
            builder.withEnum(CsvLayout.class).select(config, "Presentation").add();
            builder.withBoolean().select(config, "FullName").display("Full series name")
                    .description("If true, the fully qualified name of the series will be used. "
                            + "If false, only the name of the series will be displayed.").add();
            sheet.put(builder.build());

            builder.reset("Content");
            builder.with(List.class).select(config, "Series").editor(Series.class).add();
            sheet.put(builder.build());
            return sheet;
        }

        @Override
        public SaOutputFactory getFactory() {
            return new CsvOutputFactory(getLookup().lookup(CsvOutputConfiguration.class));
        }
    }
    //</editor-fold>
}
