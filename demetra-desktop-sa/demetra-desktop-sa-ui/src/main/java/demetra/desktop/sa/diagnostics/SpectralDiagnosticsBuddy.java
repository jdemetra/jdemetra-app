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
package demetra.desktop.sa.diagnostics;

import demetra.desktop.Config;
import demetra.desktop.ConfigEditor;
import demetra.desktop.properties.NodePropertySetBuilder;
import org.openide.nodes.Sheet;
import nbbrd.io.text.BooleanProperty;
import demetra.desktop.Converter;
import demetra.desktop.Persistable;
import demetra.desktop.actions.Configurable;
import demetra.desktop.actions.Resetable;
import demetra.desktop.beans.BeanConfigurator;
import demetra.desktop.beans.BeanEditor;
import demetra.desktop.beans.BeanHandler;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.desktop.sa.output.OutputFactoryBuddy;
import demetra.sa.SaDiagnosticsFactory;
import java.beans.IntrospectionException;
import jdplus.sa.diagnostics.SpectralDiagnosticsFactory;
import jdplus.sa.diagnostics.SpectralDiagnosticsConfiguration;
import nbbrd.io.text.DoubleProperty;
import nbbrd.io.text.IntProperty;

/**
 *
 * @author Mats Maggi
 */
public abstract class SpectralDiagnosticsBuddy implements SaDiagnosticsFactoryBuddy, Configurable, Persistable, ConfigEditor, Resetable {

    private static final BeanConfigurator<SpectralDiagnosticsConfiguration, SpectralDiagnosticsBuddy> configurator = createConfigurator();

    protected SpectralDiagnosticsConfiguration config = SpectralDiagnosticsConfiguration.getDefault();

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new SpectralDiagnosticsBuddy.SpectralDiagnosticsNode<>(config);
    }

    @Override
    public AbstractSaDiagnosticsNode createNodeFor(SaDiagnosticsFactory fac) {
        if (fac instanceof SpectralDiagnosticsFactory ofac) {
            return new SpectralDiagnosticsBuddy.SpectralDiagnosticsNode(ofac.getConfiguration());
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return SpectralDiagnosticsFactory.NAME;
    }

    @Override
    public void configure() {
        Configurable.configure(this, this);
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
        config = SpectralDiagnosticsConfiguration.getDefault();
    }

    static final class SaSpectralDiagnosticsConverter implements Converter<SpectralDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", SpectralDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty sensibilityParam = DoubleProperty.of("sensibility", SpectralDiagnosticsConfiguration.SENSIBILITY);
        private final IntProperty lengthParam = IntProperty.of("length", SpectralDiagnosticsConfiguration.LENGTH);
        private final BooleanProperty strictParam = BooleanProperty.of("strict", SpectralDiagnosticsConfiguration.STRICT);

        @Override
        public Config doForward(SpectralDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(OutputFactoryBuddy.class.getName(), "Csv_Matrix", "");
            activeParam.set(result::parameter, a.isActive());
            sensibilityParam.set(result::parameter, a.getSensibility());
            lengthParam.set(result::parameter, a.getLength());
            strictParam.set(result::parameter, a.isStrict());
            return result.build();
        }

        @Override
        public SpectralDiagnosticsConfiguration doBackward(Config b) {
            return SpectralDiagnosticsConfiguration.builder()
                    .active(activeParam.get(b::getParameter))
                    .sensibility(sensibilityParam.get(b::getParameter))
                    .length(lengthParam.get(b::getParameter))
                    .strict(strictParam.get(b::getParameter))
                    .build();
        }
    }

    static class SpectralDiagnosticsNode<R> extends AbstractSaDiagnosticsNode<SpectralDiagnosticsConfiguration, R> {

        public SpectralDiagnosticsNode(SpectralDiagnosticsConfiguration config) {
            super(config);
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();

            NodePropertySetBuilder builder = new NodePropertySetBuilder();
            builder.reset("Behaviour");
            builder.withBoolean()
                    .select("active", config::isActive, active -> activate(active))
                    .display("Enabled")
                    .add();
            builder.withBoolean()
                    .select("strict", config::isStrict, strict -> {
                        config = config.toBuilder().strict(strict).build();
                    })
                    .display("Strict")
                    .add();
            sheet.put(builder.build());

            builder.reset("Properties");
            builder.withDouble()
                    .select("sensibility", config::getSensibility, d -> {
                        config = config.toBuilder().sensibility(d).build();
                    })
                    .display("Sensibility")
                    .add();
            builder.withInt()
                    .select("length", config::getLength, l -> {
                        config = config.toBuilder().length(l).build();
                    })
                    .display("Length")
                    .add();
            sheet.put(builder.build());

            return sheet;
        }
    }

    static final class Handler implements BeanHandler<SpectralDiagnosticsConfiguration, SpectralDiagnosticsBuddy> {

        @Override
        public SpectralDiagnosticsConfiguration load(SpectralDiagnosticsBuddy resource) {
            return resource.config;
        }

        @Override
        public void store(SpectralDiagnosticsBuddy resource, SpectralDiagnosticsConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class Editor implements BeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit spectral diagnostics")
                    .editNode(new SpectralDiagnosticsBuddy.SpectralDiagnosticsNode<>((SpectralDiagnosticsConfiguration) bean));
        }
    }

    private static BeanConfigurator<SpectralDiagnosticsConfiguration, SpectralDiagnosticsBuddy> createConfigurator() {
        return new BeanConfigurator<>(new Handler(),
                new SpectralDiagnosticsBuddy.SaSpectralDiagnosticsConverter(),
                new Editor()
        );
    }
}
