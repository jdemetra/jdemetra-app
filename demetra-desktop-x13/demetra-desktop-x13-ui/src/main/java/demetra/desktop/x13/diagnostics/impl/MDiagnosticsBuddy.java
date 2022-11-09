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
package demetra.desktop.x13.diagnostics.impl;

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
import demetra.desktop.sa.diagnostics.AbstractSaDiagnosticsNode;
import demetra.desktop.sa.output.OutputFactoryBuddy;
import demetra.desktop.x13.diagnostics.X13DiagnosticsFactoryBuddy;
import demetra.sa.SaDiagnosticsFactory;
import java.beans.IntrospectionException;
import jdplus.x13.diagnostics.MDiagnosticsConfiguration;
import jdplus.x13.diagnostics.MDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mats Maggi
 */
@ServiceProvider(service = X13DiagnosticsFactoryBuddy.class, position = 1400)
public class MDiagnosticsBuddy implements X13DiagnosticsFactoryBuddy, Configurable, Persistable, ConfigEditor, Resetable {

    private static final BeanConfigurator<MDiagnosticsConfiguration, MDiagnosticsBuddy> configurator = createConfigurator();

    protected MDiagnosticsConfiguration config = MDiagnosticsConfiguration.getDefault();

    @Override
    public SaDiagnosticsFactory createFactory() {
        return new MDiagnosticsFactory(config);
    }
    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new MDiagnosticsBuddy.MDiagnosticsNode<>(config);
    }

    @Override
    public AbstractSaDiagnosticsNode createNodeFor(SaDiagnosticsFactory fac) {
        if (fac instanceof MDiagnosticsFactory ofac) {
            return new MDiagnosticsBuddy.MDiagnosticsNode(ofac.getConfiguration());
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return MDiagnosticsFactory.NAME;
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
        config = MDiagnosticsConfiguration.getDefault();
    }

    static final class MDiagnosticsConverter implements Converter<MDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", MDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty severeParam = DoubleProperty.of("severeThreshold", MDiagnosticsConfiguration.SEVERE);
        private final DoubleProperty badParam = DoubleProperty.of("badThreshold", MDiagnosticsConfiguration.BAD);
        private final BooleanProperty allParam = BooleanProperty.of("all", MDiagnosticsConfiguration.ALL);
 
        @Override
        public Config doForward(MDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(OutputFactoryBuddy.class.getName(), "Csv_Matrix", "");
            activeParam.set(result::parameter, a.isActive());
            severeParam.set(result::parameter, a.getSevereThreshold());
            badParam.set(result::parameter, a.getBadThreshold());
            allParam.set(result::parameter, a.isAll());
            return result.build();
        }

        @Override
        public MDiagnosticsConfiguration doBackward(Config b) {
            return MDiagnosticsConfiguration.builder()
                    .active(activeParam.get(b::getParameter))
                    .severeThreshold(severeParam.get(b::getParameter))
                    .badThreshold(badParam.get(b::getParameter))
                    .all(allParam.get(b::getParameter))
                    .build();
        }
    }

    static class MDiagnosticsNode<R> extends AbstractSaDiagnosticsNode<MDiagnosticsConfiguration, R> {

        public MDiagnosticsNode(MDiagnosticsConfiguration config) {
            super(config);
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();

            NodePropertySetBuilder builder = new NodePropertySetBuilder();
            builder.reset("Behaviour");
            builder.withBoolean().select("active", config::isActive, active -> activate(active)).display("Enabled").add();
            sheet.put(builder.build());
            builder.withBoolean().select("all", config::isAll, b -> {
                config = config.toBuilder().all(b).build();
            }).display("All").add();
            builder.reset("Thresholds");
            builder.withDouble().select("severe", config::getSevereThreshold, d -> {
                config = config.toBuilder().severeThreshold(d).build();
            }).display("Severe").add();
            builder.withDouble().select("bad", config::getBadThreshold, d -> {
                config = config.toBuilder().badThreshold(d).build();
            }).display("Bad").add();
            sheet.put(builder.build());
            return sheet;
        }
    }

    static final class Handler implements BeanHandler<MDiagnosticsConfiguration, MDiagnosticsBuddy> {

        @Override
        public MDiagnosticsConfiguration load(MDiagnosticsBuddy resource) {
            return resource.config;
        }

        @Override
        public void store(MDiagnosticsBuddy resource, MDiagnosticsConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class Editor implements BeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit M-diagnostics")
                    .editNode(new MDiagnosticsBuddy.MDiagnosticsNode<>((MDiagnosticsConfiguration) bean));
        }
    }

    private static BeanConfigurator<MDiagnosticsConfiguration, MDiagnosticsBuddy> createConfigurator() {
        return new BeanConfigurator<>(new Handler(),
                new MDiagnosticsBuddy.MDiagnosticsConverter(),
                new Editor()
        );
    }
}
