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
import jdplus.sa.diagnostics.ResidualTradingDaysDiagnosticsConfiguration;
import jdplus.sa.diagnostics.ResidualTradingDaysDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;

/**
 *
 * @author Mats Maggi
 */
public abstract class ResidualTradingDaysDiagnosticsBuddy implements SaDiagnosticsFactoryBuddy, Configurable, Persistable, ConfigEditor, Resetable {

    private static final BeanConfigurator<ResidualTradingDaysDiagnosticsConfiguration, ResidualTradingDaysDiagnosticsBuddy> configurator = createConfigurator();

    protected ResidualTradingDaysDiagnosticsConfiguration config = ResidualTradingDaysDiagnosticsConfiguration.getDefault();

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new ResidualTradingDaysDiagnosticsBuddy.ResidualTradingDaysDiagnosticsNode<>(config);
    }

    @Override
    public AbstractSaDiagnosticsNode createNodeFor(SaDiagnosticsFactory fac) {
        if (fac instanceof ResidualTradingDaysDiagnosticsFactory ofac) {
            return new ResidualTradingDaysDiagnosticsBuddy.ResidualTradingDaysDiagnosticsNode(ofac.getConfiguration());
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return ResidualTradingDaysDiagnosticsFactory.NAME;
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
        config = ResidualTradingDaysDiagnosticsConfiguration.getDefault();
    }

    static final class SaResidualTradingDaysDiagnosticsConverter implements Converter<ResidualTradingDaysDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", ResidualTradingDaysDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty severeParam = DoubleProperty.of("severeThreshold", ResidualTradingDaysDiagnosticsConfiguration.SEV);
        private final DoubleProperty badParam = DoubleProperty.of("badThreshold", ResidualTradingDaysDiagnosticsConfiguration.BAD);
        private final DoubleProperty uncertainParam = DoubleProperty.of("uncertainThreshold", ResidualTradingDaysDiagnosticsConfiguration.UNC);
 
        @Override
        public Config doForward(ResidualTradingDaysDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(OutputFactoryBuddy.class.getName(), "Csv_Matrix", "");
            activeParam.set(result::parameter, a.isActive());
            severeParam.set(result::parameter, a.getSevereThreshold());
            badParam.set(result::parameter, a.getBadThreshold());
            uncertainParam.set(result::parameter, a.getUncertainThreshold());
            return result.build();
        }

        @Override
        public ResidualTradingDaysDiagnosticsConfiguration doBackward(Config b) {
            return ResidualTradingDaysDiagnosticsConfiguration.builder()
                    .active(activeParam.get(b::getParameter))
                    .severeThreshold(severeParam.get(b::getParameter))
                    .badThreshold(badParam.get(b::getParameter))
                    .uncertainThreshold(uncertainParam.get(b::getParameter))
                    .build();
        }
    }

    static class ResidualTradingDaysDiagnosticsNode<R> extends AbstractSaDiagnosticsNode<ResidualTradingDaysDiagnosticsConfiguration, R> {

        public ResidualTradingDaysDiagnosticsNode(ResidualTradingDaysDiagnosticsConfiguration config) {
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
            builder.withDouble().select("severe", config::getSevereThreshold, d -> {
                config = config.toBuilder().severeThreshold(d).build();
            }).display("Severe").add();
            builder.withDouble().select("bad", config::getBadThreshold, d -> {
                config = config.toBuilder().badThreshold(d).build();
            }).display("Bad").add();
            builder.withDouble().select("uncertain", config::getUncertainThreshold, d -> {
                config = config.toBuilder().uncertainThreshold(d).build();
            }).display("Uncertain").add();
            sheet.put(builder.build());
            return sheet;
        }
    }

    static final class Handler implements BeanHandler<ResidualTradingDaysDiagnosticsConfiguration, ResidualTradingDaysDiagnosticsBuddy> {

        @Override
        public ResidualTradingDaysDiagnosticsConfiguration load(ResidualTradingDaysDiagnosticsBuddy resource) {
            return resource.config;
        }

        @Override
        public void store(ResidualTradingDaysDiagnosticsBuddy resource, ResidualTradingDaysDiagnosticsConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class Editor implements BeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit residual trading days diagnostics")
                    .editNode(new ResidualTradingDaysDiagnosticsBuddy.ResidualTradingDaysDiagnosticsNode<>((ResidualTradingDaysDiagnosticsConfiguration) bean));
        }
    }

    private static BeanConfigurator<ResidualTradingDaysDiagnosticsConfiguration, ResidualTradingDaysDiagnosticsBuddy> createConfigurator() {
        return new BeanConfigurator<>(new Handler(),
                new ResidualTradingDaysDiagnosticsBuddy.SaResidualTradingDaysDiagnosticsConverter(),
                new Editor()
        );
    }
}
