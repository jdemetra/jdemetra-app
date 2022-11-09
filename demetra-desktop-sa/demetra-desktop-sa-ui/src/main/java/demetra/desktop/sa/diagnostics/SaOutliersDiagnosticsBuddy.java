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
import jdplus.regarima.diagnostics.OutliersDiagnosticsConfiguration;
import jdplus.sa.diagnostics.SaOutliersDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;

/**
 *
 * @author Mats Maggi
 */
public abstract class SaOutliersDiagnosticsBuddy implements SaDiagnosticsFactoryBuddy, Configurable, Persistable, ConfigEditor, Resetable {

    private static final BeanConfigurator<OutliersDiagnosticsConfiguration, SaOutliersDiagnosticsBuddy> configurator = createConfigurator();

    protected OutliersDiagnosticsConfiguration config = OutliersDiagnosticsConfiguration.getDefault();

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new SaOutliersDiagnosticsBuddy.OutliersDiagnosticsNode<>(config);
    }

    @Override
    public AbstractSaDiagnosticsNode createNodeFor(SaDiagnosticsFactory fac) {
        if (fac instanceof SaOutliersDiagnosticsFactory ofac) {
            return new SaOutliersDiagnosticsBuddy.OutliersDiagnosticsNode(ofac.getConfiguration());
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return SaOutliersDiagnosticsFactory.NAME;
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
        config = OutliersDiagnosticsConfiguration.getDefault();
    }

    static final class SaOutliersDiagnosticsConverter implements Converter<OutliersDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", OutliersDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty severeParam = DoubleProperty.of("severeThreshold", OutliersDiagnosticsConfiguration.SEV);
        private final DoubleProperty badParam = DoubleProperty.of("badThreshold", OutliersDiagnosticsConfiguration.BAD);
        private final DoubleProperty uncertainParam = DoubleProperty.of("uncertainThreshold", OutliersDiagnosticsConfiguration.UNC);

        @Override
        public Config doForward(OutliersDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(OutputFactoryBuddy.class.getName(), "Csv_Matrix", "");
            activeParam.set(result::parameter, a.isActive());
            severeParam.set(result::parameter, a.getSevereThreshold());
            badParam.set(result::parameter, a.getBadThreshold());
            uncertainParam.set(result::parameter, a.getUncertainThreshold());
            return result.build();
        }

        @Override
        public OutliersDiagnosticsConfiguration doBackward(Config b) {
            return OutliersDiagnosticsConfiguration.builder()
                    .active(activeParam.get(b::getParameter))
                    .severeThreshold(severeParam.get(b::getParameter))
                    .badThreshold(badParam.get(b::getParameter))
                    .uncertainThreshold(uncertainParam.get(b::getParameter))
                    .build();
        }
    }

    static class OutliersDiagnosticsNode<R> extends AbstractSaDiagnosticsNode<OutliersDiagnosticsConfiguration, R> {

        public OutliersDiagnosticsNode(OutliersDiagnosticsConfiguration config) {
            super(config);
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();

            NodePropertySetBuilder builder = new NodePropertySetBuilder();
            builder.reset("Behaviour");
            builder.withBoolean().select("active", config::isActive, active -> activate(active)).display("Enabled").add();
            sheet.put(builder.build());

            builder.reset("Thresholds");
            builder.withDouble()
                    .select("severe", config::getSevereThreshold, d -> {
                        config = config.toBuilder().severeThreshold(d).build();
                    })
                    .display("Severe").add();
            builder.withDouble()
                    .select("bad", config::getBadThreshold, d -> {
                        config = config.toBuilder().badThreshold(d).build();
                    })
                    .display("Bad").add();
            builder.withDouble()
                    .select("uncertain", config::getUncertainThreshold, d -> {
                        config = config.toBuilder().uncertainThreshold(d).build();
                    })
                    .display("Uncertain").add();
            sheet.put(builder.build());
            return sheet;
        }
    }

    static final class Handler implements BeanHandler<OutliersDiagnosticsConfiguration, SaOutliersDiagnosticsBuddy> {

        @Override
        public OutliersDiagnosticsConfiguration load(SaOutliersDiagnosticsBuddy resource) {
            return resource.config;
        }

        @Override
        public void store(SaOutliersDiagnosticsBuddy resource, OutliersDiagnosticsConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class Editor implements BeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit outliers diagnostics")
                    .editNode(new SaOutliersDiagnosticsBuddy.OutliersDiagnosticsNode<>((OutliersDiagnosticsConfiguration) bean));
        }
    }

    private static BeanConfigurator<OutliersDiagnosticsConfiguration, SaOutliersDiagnosticsBuddy> createConfigurator() {
        return new BeanConfigurator<>(new Handler(),
                new SaOutliersDiagnosticsBuddy.SaOutliersDiagnosticsConverter(),
                new Editor()
        );
    }
}
