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
package ec.nbdemetra.sa.output.impl;

import com.google.common.base.Converter;
import com.google.common.base.Splitter;
import ec.nbdemetra.sa.output.AbstractOutputNode;
import ec.nbdemetra.sa.output.INbOutputFactory;
import ec.nbdemetra.sa.output.Series;
import ec.nbdemetra.ui.BeanHandler;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.IResetable;
import ec.nbdemetra.ui.properties.PropertySheetDialogBuilder;
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tss.sa.ISaOutputFactory;
import ec.tss.sa.output.TxtOutputConfiguration;
import ec.tss.sa.output.TxtOutputFactory;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mats Maggi
 */
@ServiceProvider(service = INbOutputFactory.class, position = 1500)
public class TxtOutputBuddy implements INbOutputFactory, IConfigurable, IResetable {

    private final Configurator<TxtOutputBuddy> configurator = createConfigurator();
    private TxtOutputConfiguration config = new TxtOutputConfiguration();

    @Override
    public String getName() {
        return TxtOutputFactory.NAME;
    }

    @Override
    public AbstractOutputNode createNode() {
        return new TxtNode(config);
    }

    @Override
    public AbstractOutputNode createNodeFor(Object properties) {
        return properties instanceof TxtOutputConfiguration ? new TxtNode((TxtOutputConfiguration) properties) : null;
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
        config = new TxtOutputConfiguration();
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraUiIcon.DOCUMENT_16);
    }

    private static Configurator<TxtOutputBuddy> createConfigurator() {
        return new TxtOutputBeanHandler().toConfigurator(new TxtOutputConverter(), new TxtOutputBeanEditor());
    }

    private static final class TxtOutputBeanHandler extends BeanHandler<TxtOutputConfiguration, TxtOutputBuddy> {

        @Override
        public TxtOutputConfiguration loadBean(TxtOutputBuddy resource) {
            return resource.config.clone();
        }

        @Override
        public void storeBean(TxtOutputBuddy resource, TxtOutputConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class TxtOutputBeanEditor implements IBeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit Text output config")
                    .editNode(new TxtNode((TxtOutputConfiguration) bean));
        }
    }

    private static final class TxtOutputConverter extends Converter<TxtOutputConfiguration, Config> {

        private final IParam<Config, File> folderParam = Params.onFile(Paths.get("").toFile(), "folder");
        private final IParam<Config, String> seriesParam = Params.onString("y,t,sa,s,i,ycal", "series");
        private final IParam<Config, Boolean> fullNameParam = Params.onBoolean(true, "fullName");

        @Override
        protected Config doForward(TxtOutputConfiguration a) {
            Config.Builder result = Config.builder(INbOutputFactory.class.getName(), "Txt", "");
            folderParam.set(result, a.getFolder());
            seriesParam.set(result, a.getSeries().stream().collect(Collectors.joining(",")));
            fullNameParam.set(result, a.isFullName());
            return result.build();
        }

        @Override
        protected TxtOutputConfiguration doBackward(Config b) {
            TxtOutputConfiguration result = new TxtOutputConfiguration();
            result.setFolder(folderParam.get(b));
            result.setSeries(Splitter.on(",").trimResults().splitToList(seriesParam.get(b)));
            result.setFullName(fullNameParam.get(b));
            return result;
        }
    }

    public static final class TxtNode extends AbstractOutputNode<TxtOutputConfiguration> {

        public TxtNode() {
            super(new TxtOutputConfiguration());
            setDisplayName(TxtOutputFactory.NAME);
        }

        public TxtNode(TxtOutputConfiguration config) {
            super(config);
            setDisplayName(TxtOutputFactory.NAME);
        }

        @Override
        protected Sheet createSheet() {
            TxtOutputConfiguration config = getLookup().lookup(TxtOutputConfiguration.class);

            Sheet sheet = super.createSheet();
            NodePropertySetBuilder builder = new NodePropertySetBuilder();
            builder.reset("Location");
            builder.withFile().select(config, "Folder").directories(true).description("Base output folder. Will be extended by the workspace and processing names").add();
            sheet.put(builder.build());

            builder.reset("Content");
            builder.with(List.class).select(config, "Series").editor(Series.class).add();
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
            return new TxtOutputFactory(getLookup().lookup(TxtOutputConfiguration.class));
        }
    }
}
