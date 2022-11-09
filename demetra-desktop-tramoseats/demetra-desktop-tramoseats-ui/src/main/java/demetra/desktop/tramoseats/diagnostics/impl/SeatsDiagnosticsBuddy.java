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
package demetra.desktop.tramoseats.diagnostics.impl;

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
import demetra.desktop.tramoseats.diagnostics.TramoSeatsDiagnosticsFactoryBuddy;
import demetra.sa.SaDiagnosticsFactory;
import java.beans.IntrospectionException;
import jdplus.seats.diagnostics.SeatsDiagnosticsConfiguration;
import jdplus.seats.diagnostics.SeatsDiagnosticsFactory;
import jdplus.tramoseats.TramoSeatsResults;
import nbbrd.io.text.DoubleProperty;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mats Maggi
 */
@ServiceProvider(service = TramoSeatsDiagnosticsFactoryBuddy.class, position = 1400)
public class SeatsDiagnosticsBuddy implements TramoSeatsDiagnosticsFactoryBuddy, Configurable, Persistable, ConfigEditor, Resetable {

    private static final BeanConfigurator<SeatsDiagnosticsConfiguration, SeatsDiagnosticsBuddy> configurator = createConfigurator();

    protected SeatsDiagnosticsConfiguration config = SeatsDiagnosticsConfiguration.getDefault();

    @Override
    public SaDiagnosticsFactory createFactory() {
        return new SeatsDiagnosticsFactory<>(config, (TramoSeatsResults r) -> r.getDiagnostics().getSpecificDiagnostics());
    }
    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new SeatsDiagnosticsBuddy.DiagnosticsNode<>(config);
    }

    @Override
    public AbstractSaDiagnosticsNode createNodeFor(SaDiagnosticsFactory fac) {
        if (fac instanceof SeatsDiagnosticsFactory ofac) {
            return new SeatsDiagnosticsBuddy.DiagnosticsNode(ofac.getConfiguration());
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return SeatsDiagnosticsFactory.NAME;
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
        config = SeatsDiagnosticsConfiguration.getDefault();
    }

    static final class DiagnosticsConverter implements Converter<SeatsDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", SeatsDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty uncertainParam = DoubleProperty.of("severeThreshold", SeatsDiagnosticsConfiguration.UNC);
        private final DoubleProperty badParam = DoubleProperty.of("badThreshold", SeatsDiagnosticsConfiguration.BAD);
 
        @Override
        public Config doForward(SeatsDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(OutputFactoryBuddy.class.getName(), "Csv_Matrix", "");
            activeParam.set(result::parameter, a.isActive());
            uncertainParam.set(result::parameter, a.getUncertainThreshold());
            badParam.set(result::parameter, a.getBadThreshold());
            return result.build();
        }

        @Override
        public SeatsDiagnosticsConfiguration doBackward(Config b) {
            return SeatsDiagnosticsConfiguration.builder()
                    .active(activeParam.get(b::getParameter))
                    .uncertainThreshold(uncertainParam.get(b::getParameter))
                    .badThreshold(badParam.get(b::getParameter))
                    .build();
        }
    }

    static class DiagnosticsNode<R> extends AbstractSaDiagnosticsNode<SeatsDiagnosticsConfiguration, R> {

        public DiagnosticsNode(SeatsDiagnosticsConfiguration config) {
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

    static final class Handler implements BeanHandler<SeatsDiagnosticsConfiguration, SeatsDiagnosticsBuddy> {

        @Override
        public SeatsDiagnosticsConfiguration load(SeatsDiagnosticsBuddy resource) {
            return resource.config;
        }

        @Override
        public void store(SeatsDiagnosticsBuddy resource, SeatsDiagnosticsConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class Editor implements BeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit M-diagnostics")
                    .editNode(new SeatsDiagnosticsBuddy.DiagnosticsNode<>((SeatsDiagnosticsConfiguration) bean));
        }
    }

    private static BeanConfigurator<SeatsDiagnosticsConfiguration, SeatsDiagnosticsBuddy> createConfigurator() {
        return new BeanConfigurator<>(new Handler(),
                new SeatsDiagnosticsBuddy.DiagnosticsConverter(),
                new Editor()
        );
    }
}
