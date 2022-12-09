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
import demetra.desktop.properties.NodePropertySetBuilder;
import org.openide.nodes.Sheet;
import nbbrd.io.text.BooleanProperty;
import demetra.desktop.Converter;
import jdplus.sa.diagnostics.ResidualTradingDaysDiagnosticsConfiguration;
import jdplus.sa.diagnostics.ResidualTradingDaysDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;

/**
 *
 * @author Mats Maggi
 */
public class ResidualTradingDaysDiagnosticsBuddy extends AbstractSaDiagnosticsFactoryBuddy<ResidualTradingDaysDiagnosticsConfiguration, ResidualTradingDaysDiagnosticsBuddy.Bean> {

    @lombok.Data
    public static class Bean {

        private boolean active;
        private boolean monthlyOnly;
        private double severeThreshold;
        private double badThreshold;
        private double uncertainThreshold;

        public static Bean of(ResidualTradingDaysDiagnosticsConfiguration config) {
            Bean bean = new Bean();
            bean.active = config.isActive();
            bean.monthlyOnly = config.isMonthlyOnly();
            bean.severeThreshold = config.getSevereThreshold();
            bean.badThreshold = config.getBadThreshold();
            bean.uncertainThreshold = config.getUncertainThreshold();
            return bean;
        }

        public ResidualTradingDaysDiagnosticsConfiguration asCore() {
            return ResidualTradingDaysDiagnosticsConfiguration.builder()
                    .active(active)
                    .monthlyOnly(monthlyOnly)
                    .severeThreshold(severeThreshold)
                    .badThreshold(badThreshold)
                    .uncertainThreshold(uncertainThreshold)
                    .build();
        }
    }

    private static final Converter<Bean, ResidualTradingDaysDiagnosticsConfiguration> BEANCONVERTER = new BeanConverter();

    protected ResidualTradingDaysDiagnosticsBuddy() {
        super(new ResidualTradingDaysDiagnosticsBuddy.CoreConverter(), BEANCONVERTER);
    }

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new DiagnosticsNode(bean());
    }

    @Override
    public String getName() {
        return ResidualTradingDaysDiagnosticsFactory.NAME;
    }

    @Override
    public void reset() {
        setCore(ResidualTradingDaysDiagnosticsConfiguration.getDefault());
    }

    static final class BeanConverter implements Converter<Bean, ResidualTradingDaysDiagnosticsConfiguration> {

        @Override
        public ResidualTradingDaysDiagnosticsConfiguration doForward(Bean a) {
            return a.asCore();
        }

        @Override
        public Bean doBackward(ResidualTradingDaysDiagnosticsConfiguration b) {
            return Bean.of(b);
        }
    }

    static final class CoreConverter implements Converter<ResidualTradingDaysDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", ResidualTradingDaysDiagnosticsConfiguration.ACTIVE);
        private final BooleanProperty monthlyOnlyParam = BooleanProperty.of("monthlyOnly", ResidualTradingDaysDiagnosticsConfiguration.MONTHLY);
        private final DoubleProperty severeParam = DoubleProperty.of("severeThreshold", ResidualTradingDaysDiagnosticsConfiguration.SEV);
        private final DoubleProperty badParam = DoubleProperty.of("badThreshold", ResidualTradingDaysDiagnosticsConfiguration.BAD);
        private final DoubleProperty uncertainParam = DoubleProperty.of("uncertainThreshold", ResidualTradingDaysDiagnosticsConfiguration.UNC);

        @Override
        public Config doForward(ResidualTradingDaysDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder("diagnostics", "residual_td", "3.0");
            activeParam.set(result::parameter, a.isActive());
            monthlyOnlyParam.set(result::parameter, a.isMonthlyOnly());
            severeParam.set(result::parameter, a.getSevereThreshold());
            badParam.set(result::parameter, a.getBadThreshold());
            uncertainParam.set(result::parameter, a.getUncertainThreshold());
            return result.build();
        }

        @Override
        public ResidualTradingDaysDiagnosticsConfiguration doBackward(Config b) {
            return ResidualTradingDaysDiagnosticsConfiguration.builder()
                    .active(activeParam.get(b::getParameter))
                    .monthlyOnly(monthlyOnlyParam.get(b::getParameter))
                    .severeThreshold(severeParam.get(b::getParameter))
                    .badThreshold(badParam.get(b::getParameter))
                    .uncertainThreshold(uncertainParam.get(b::getParameter))
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
            builder.withBoolean().select(bean, "monthlyOnly").display("Monthly Only").description("Apply the tests only on monthly series").add();
            sheet.put(builder.build());
            builder.reset("Thresholds");
            builder.withDouble().select(bean, "severeThreshold").display("Severe").add();
            builder.withDouble().select(bean, "badThreshold").display("Bad").add();
            builder.withDouble().select(bean, "uncertainThreshold").display("Uncertain").add();
            sheet.put(builder.build());
            return sheet;
        }
    }
}
