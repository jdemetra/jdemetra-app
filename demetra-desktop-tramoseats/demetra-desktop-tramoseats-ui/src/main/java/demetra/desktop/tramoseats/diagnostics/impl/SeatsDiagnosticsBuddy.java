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
import demetra.desktop.properties.NodePropertySetBuilder;
import org.openide.nodes.Sheet;
import nbbrd.io.text.BooleanProperty;
import demetra.desktop.Converter;
import demetra.desktop.sa.diagnostics.AbstractSaDiagnosticsFactoryBuddy;
import demetra.desktop.sa.diagnostics.AbstractSaDiagnosticsNode;
import demetra.desktop.tramoseats.diagnostics.TramoSeatsDiagnosticsFactoryBuddy;
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
public final class SeatsDiagnosticsBuddy extends AbstractSaDiagnosticsFactoryBuddy<SeatsDiagnosticsConfiguration, SeatsDiagnosticsBuddy.Bean> 
        implements TramoSeatsDiagnosticsFactoryBuddy<SeatsDiagnosticsConfiguration> {

    @lombok.Data
    public static class Bean {

        private boolean active;
        private double badThreshold;
        private double uncertainThreshold;

        public static Bean of(SeatsDiagnosticsConfiguration config) {
            Bean bean = new Bean();
            bean.active = config.isActive();
            bean.badThreshold = config.getBadThreshold();
            bean.uncertainThreshold = config.getUncertainThreshold();
            return bean;
        }

        public SeatsDiagnosticsConfiguration asCore() {
            return SeatsDiagnosticsConfiguration.builder()
                    .active(active)
                    .badThreshold(badThreshold)
                    .uncertainThreshold(uncertainThreshold)
                    .build();
        }
    }

    private static final Converter<Bean, SeatsDiagnosticsConfiguration> BEANCONVERTER = new BeanConverter();

    public SeatsDiagnosticsBuddy() {
        super(new CoreConverter(), BEANCONVERTER);
         this.setActiveDiagnosticsConfiguration(SeatsDiagnosticsConfiguration.getDefault());
    }

    @Override
    public SeatsDiagnosticsFactory<TramoSeatsResults> createFactory() {
        return new SeatsDiagnosticsFactory<>(core(), (TramoSeatsResults r) -> r.getDiagnostics().getSpecificDiagnostics());
    }

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new DiagnosticsNode(bean());
    }

    @Override
    public String getName() {
        return SeatsDiagnosticsFactory.NAME;
    }

    @Override
    public void reset() {
        setCore(SeatsDiagnosticsConfiguration.getDefault());
    }

    static final class BeanConverter implements Converter<Bean, SeatsDiagnosticsConfiguration> {

        @Override
        public SeatsDiagnosticsConfiguration doForward(Bean a) {
            return a.asCore();
        }

        @Override
        public Bean doBackward(SeatsDiagnosticsConfiguration b) {
            return Bean.of(b);
        }
    }

    static final class CoreConverter implements Converter<SeatsDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", SeatsDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty uncertainParam = DoubleProperty.of("severeThreshold", SeatsDiagnosticsConfiguration.UNC);
        private final DoubleProperty badParam = DoubleProperty.of("badThreshold", SeatsDiagnosticsConfiguration.BAD);

        @Override
        public Config doForward(SeatsDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder("diagnostics", "seats", "3.0");
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

    static class DiagnosticsNode extends AbstractSaDiagnosticsNode<Bean> {

        public DiagnosticsNode(Bean bean) {
            super(bean);
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();

            NodePropertySetBuilder builder = new NodePropertySetBuilder();
            builder.reset("Behaviour");
            builder.withBoolean().select(bean, "active").display("Enabled").add();
            sheet.put(builder.build());
            builder.reset("Thresholds");
            builder.withDouble().select(bean, "badThreshold").display("Bad").add();
            builder.withDouble().select(bean, "uncertainThreshold").display("Uncertain").add();
            sheet.put(builder.build());
            return sheet;
        }
    }
}
