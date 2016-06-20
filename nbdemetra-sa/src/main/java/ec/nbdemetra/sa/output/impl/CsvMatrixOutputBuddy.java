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
import ec.nbdemetra.sa.output.Matrix;
import ec.nbdemetra.ui.BeanHandler;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.IResetable;
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.tss.sa.ISaOutputFactory;
import ec.tss.sa.output.CsvMatrixOutputConfiguration;
import ec.tss.sa.output.CsvMatrixOutputFactory;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import java.beans.IntrospectionException;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mats Maggi
 */
@ServiceProvider(service = INbOutputFactory.class, position = 1100)
public class CsvMatrixOutputBuddy implements INbOutputFactory, IConfigurable, IResetable {

    private final Configurator<CsvMatrixOutputBuddy> configurator = createConfigurator();
    private CsvMatrixOutputConfiguration config = new CsvMatrixOutputConfiguration();

    @Override
    public String getName() {
        return CsvMatrixOutputFactory.NAME;
    }

    @Override
    public AbstractOutputNode createNode() {
        return new CsvMatrixNode(config);
    }

    @Override
    public AbstractOutputNode createNodeFor(Object properties) {
        return properties instanceof CsvMatrixOutputConfiguration ? new CsvMatrixNode((CsvMatrixOutputConfiguration) properties) : null;
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
        config = new CsvMatrixOutputConfiguration();
    }

    private static Configurator<CsvMatrixOutputBuddy> createConfigurator() {
        return new CsvMatrixOutputBeanHandler().toConfigurator(new CsvMatrixOutputConverter(), new CsvMatrixOutputBeanEditor());
    }

    private static final class CsvMatrixOutputBeanHandler extends BeanHandler<CsvMatrixOutputConfiguration, CsvMatrixOutputBuddy> {

        @Override
        public CsvMatrixOutputConfiguration loadBean(CsvMatrixOutputBuddy resource) {
            return resource.config.clone();
        }

        @Override
        public void storeBean(CsvMatrixOutputBuddy resource, CsvMatrixOutputConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class CsvMatrixOutputBeanEditor implements IBeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return OpenIdePropertySheetBeanEditor.editNode(new CsvMatrixNode((CsvMatrixOutputConfiguration) bean), "Edit Csv Matrix output config", null);
        }
    }

    private static final class CsvMatrixOutputConverter extends Converter<CsvMatrixOutputConfiguration, Config> {

        private final IParam<Config, File> folderParam = Params.onFile(new File(""), "folder");
        private final IParam<Config, String> fileNameParam = Params.onString("series", "fileName");
        private final IParam<Config, String> seriesParam = Params.onString("y,t,sa,s,i,ycal", "items");
        private final IParam<Config, Boolean> fullNameParam = Params.onBoolean(true, "fullName");

        @Override
        protected Config doForward(CsvMatrixOutputConfiguration a) {
            Config.Builder result = Config.builder(INbOutputFactory.class.getName(), "Csv_Matrix", "");
            folderParam.set(result, a.getFolder());
            fileNameParam.set(result, a.getFileName());
            seriesParam.set(result, a.getItems().stream().collect(Collectors.joining(",")));
            fullNameParam.set(result, a.isFullName());
            return result.build();
        }

        @Override
        protected CsvMatrixOutputConfiguration doBackward(Config b) {
            CsvMatrixOutputConfiguration result = new CsvMatrixOutputConfiguration();
            result.setFolder(folderParam.get(b));
            result.setFileName(fileNameParam.get(b));
            result.setItems(Splitter.on(",").trimResults().splitToList(seriesParam.get(b)));
            result.setFullName(fullNameParam.get(b));
            return result;
        }
    }

    public final static class CsvMatrixNode extends AbstractOutputNode<CsvMatrixOutputConfiguration> {

        public CsvMatrixNode() {
            super(new CsvMatrixOutputConfiguration());
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
        public ISaOutputFactory getFactory() {
            return new CsvMatrixOutputFactory(getLookup().lookup(CsvMatrixOutputConfiguration.class));
        }
    }
}
