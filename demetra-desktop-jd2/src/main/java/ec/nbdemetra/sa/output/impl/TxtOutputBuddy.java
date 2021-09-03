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

import com.google.common.base.Splitter;
import ec.nbdemetra.sa.output.AbstractOutputNode;
import ec.nbdemetra.sa.output.INbOutputFactory;
import ec.nbdemetra.sa.output.Series;
import demetra.desktop.beans.BeanHandler;
import demetra.desktop.Config;
import demetra.desktop.ConfigEditor;
import ec.nbdemetra.ui.DemetraUiIcon;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.desktop.properties.NodePropertySetBuilder;
import ec.tss.sa.ISaOutputFactory;
import ec.tss.sa.output.TxtOutputConfiguration;
import ec.tss.sa.output.TxtOutputFactory;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;
import demetra.desktop.actions.Resetable;
import nbbrd.io.text.BooleanProperty;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import nbbrd.io.text.Property;
import demetra.desktop.properties.BeanEditor;
import demetra.desktop.Converter;
import demetra.desktop.Persistable;
import demetra.desktop.actions.Configurable;
import demetra.desktop.beans.BeanConfigurator;

/**
 *
 * @author Mats Maggi
 */
@ServiceProvider(service = INbOutputFactory.class, position = 1500)
public class TxtOutputBuddy implements INbOutputFactory, Configurable, Persistable, ConfigEditor, Resetable {

    private final BeanConfigurator<TxtOutputConfiguration, TxtOutputBuddy> configurator = createConfigurator();
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
    public void configure() {
        Configurable.configure(this, this);
    }

    @Override
    public void reset() {
        config = new TxtOutputConfiguration();
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraUiIcon.DOCUMENT_16);
    }

    private static BeanConfigurator<TxtOutputConfiguration, TxtOutputBuddy> createConfigurator() {
        return new BeanConfigurator<>(new TxtOutputBeanHandler(), new TxtOutputConverter(), new TxtOutputBeanEditor());
    }

    private static final class TxtOutputBeanHandler implements BeanHandler<TxtOutputConfiguration, TxtOutputBuddy> {

        @Override
        public TxtOutputConfiguration loadBean(TxtOutputBuddy resource) {
            return resource.config.clone();
        }

        @Override
        public void storeBean(TxtOutputBuddy resource, TxtOutputConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class TxtOutputBeanEditor implements BeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit Text output config")
                    .editNode(new TxtNode((TxtOutputConfiguration) bean));
        }
    }

    private static final class TxtOutputConverter implements Converter<TxtOutputConfiguration, Config> {

        private final Property<File> folderParam = Property.of("folder", new File(""), Parser.onFile(), Formatter.onFile());
        private final Property<String> seriesParam = Property.of("series", "y,t,sa,s,i,ycal", Parser.onString(), Formatter.onString());
        private final BooleanProperty fullNameParam = BooleanProperty.of("fullName", true);

        @Override
        public Config doForward(TxtOutputConfiguration a) {
            Config.Builder result = Config.builder(INbOutputFactory.class.getName(), "Txt", "");
            folderParam.set(result::parameter, a.getFolder());
            seriesParam.set(result::parameter, a.getSeries().stream().collect(Collectors.joining(",")));
            fullNameParam.set(result::parameter, a.isFullName());
            return result.build();
        }

        @Override
        public TxtOutputConfiguration doBackward(Config b) {
            TxtOutputConfiguration result = new TxtOutputConfiguration();
            result.setFolder(folderParam.get(b::getParameter));
            result.setSeries(Splitter.on(",").trimResults().splitToList(seriesParam.get(b::getParameter)));
            result.setFullName(fullNameParam.get(b::getParameter));
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
