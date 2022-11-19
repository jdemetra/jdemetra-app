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
import jdplus.sa.diagnostics.ResidualSeasonalityDiagnosticsConfiguration;
import jdplus.sa.diagnostics.ResidualSeasonalityDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;

/**
 *
 * @author Mats Maggi
 */
public class ResidualSeasonalityDiagnosticsBuddy extends AbstractSaDiagnosticsFactoryBuddy<ResidualSeasonalityDiagnosticsConfiguration, ResidualSeasonalityDiagnosticsBuddy.Bean> {

    @lombok.Data
    public static class Bean {

        private boolean active;
        private double severeThresholdForSa;
        private double badThresholdForSa;
        private double uncertainThresholdForSa;
        private double severeThresholdForIrregular;
        private double badThresholdForIrregular;
        private double uncertainThresholdForIrregular;
        private double severeThresholdForLastSa;
        private double badThresholdForLastSa;
        private double uncertainThresholdForLastSa;

        public static Bean of(ResidualSeasonalityDiagnosticsConfiguration config) {
            Bean bean = new Bean();
            bean.active = config.isActive();
            bean.severeThresholdForSa = config.getSevereThresholdForSa();
            bean.badThresholdForSa = config.getBadThresholdForSa();
            bean.uncertainThresholdForSa = config.getUncertainThresholdForSa();
            bean.severeThresholdForIrregular = config.getSevereThresholdForIrregular();
            bean.badThresholdForIrregular = config.getBadThresholdForIrregular();
            bean.uncertainThresholdForIrregular = config.getUncertainThresholdForIrregular();
            bean.severeThresholdForLastSa = config.getSevereThresholdForLastSa();
            bean.badThresholdForLastSa = config.getBadThresholdForLastSa();
            bean.uncertainThresholdForLastSa = config.getUncertainThresholdForLastSa();
            return bean;

        }

        public ResidualSeasonalityDiagnosticsConfiguration asCore() {
            return ResidualSeasonalityDiagnosticsConfiguration.builder()
                    .active(active)
                    .severeThresholdForSa(severeThresholdForSa)
                    .badThresholdForSa(badThresholdForSa)
                    .uncertainThresholdForSa(uncertainThresholdForSa)
                    .severeThresholdForIrregular(severeThresholdForIrregular)
                    .badThresholdForIrregular(badThresholdForIrregular)
                    .uncertainThresholdForIrregular(uncertainThresholdForIrregular)
                    .severeThresholdForLastSa(severeThresholdForLastSa)
                    .badThresholdForLastSa(badThresholdForLastSa)
                    .uncertainThresholdForLastSa(uncertainThresholdForLastSa)
                    .build();
        }
    }

    private static final Converter<Bean, ResidualSeasonalityDiagnosticsConfiguration> BEANCONVERTER = new BeanConverter();

    protected ResidualSeasonalityDiagnosticsBuddy() {
        super(new CoreConverter(), BEANCONVERTER);
    }

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new ResidualSeasonalityDiagnosticsBuddy.DiagnosticsNode(bean());
    }

    @Override
    public String getName() {
        return ResidualSeasonalityDiagnosticsFactory.NAME;
    }

    @Override
    public void reset() {
        setCore(ResidualSeasonalityDiagnosticsConfiguration.getDefault());
    }

    static final class BeanConverter implements Converter<Bean, ResidualSeasonalityDiagnosticsConfiguration> {

        @Override
        public ResidualSeasonalityDiagnosticsConfiguration doForward(Bean a) {
            return a.asCore();
        }

        @Override
        public Bean doBackward(ResidualSeasonalityDiagnosticsConfiguration b) {
            return Bean.of(b);
        }
    }

    static final class CoreConverter implements Converter<ResidualSeasonalityDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", ResidualSeasonalityDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty severeSaParam = DoubleProperty.of("sevSaThreshold", ResidualSeasonalityDiagnosticsConfiguration.SASEV);
        private final DoubleProperty badSaParam = DoubleProperty.of("badSaThreshold", ResidualSeasonalityDiagnosticsConfiguration.SABAD);
        private final DoubleProperty uncertainSaParam = DoubleProperty.of("uncSaThreshold", ResidualSeasonalityDiagnosticsConfiguration.SAUNC);
        private final DoubleProperty severeIrrParam = DoubleProperty.of("sevIrrThreshold", ResidualSeasonalityDiagnosticsConfiguration.ISEV);
        private final DoubleProperty badIrrParam = DoubleProperty.of("badIrrThreshold", ResidualSeasonalityDiagnosticsConfiguration.IBAD);
        private final DoubleProperty uncertainIrrParam = DoubleProperty.of("uncIrrThreshold", ResidualSeasonalityDiagnosticsConfiguration.IUNC);
        private final DoubleProperty severeLastSaParam = DoubleProperty.of("sevLastThreshold", ResidualSeasonalityDiagnosticsConfiguration.SA3SEV);
        private final DoubleProperty badLastSaParam = DoubleProperty.of("badLastSaThreshold", ResidualSeasonalityDiagnosticsConfiguration.SA3BAD);
        private final DoubleProperty uncertainLastSaParam = DoubleProperty.of("uncLastSaThreshold", ResidualSeasonalityDiagnosticsConfiguration.SA3UNC);

        @Override
        public Config doForward(ResidualSeasonalityDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder("diagnostics", "combined_seasonality", "");
            activeParam.set(result::parameter, a.isActive());
            severeSaParam.set(result::parameter, a.getSevereThresholdForSa());
            badSaParam.set(result::parameter, a.getBadThresholdForSa());
            uncertainSaParam.set(result::parameter, a.getUncertainThresholdForSa());
            severeIrrParam.set(result::parameter, a.getSevereThresholdForIrregular());
            badIrrParam.set(result::parameter, a.getBadThresholdForIrregular());
            uncertainIrrParam.set(result::parameter, a.getUncertainThresholdForIrregular());
            severeLastSaParam.set(result::parameter, a.getSevereThresholdForLastSa());
            badLastSaParam.set(result::parameter, a.getBadThresholdForLastSa());
            uncertainLastSaParam.set(result::parameter, a.getUncertainThresholdForLastSa());
            return result.build();
        }

        @Override
        public ResidualSeasonalityDiagnosticsConfiguration doBackward(Config b) {
            return ResidualSeasonalityDiagnosticsConfiguration.builder()
                    .active(activeParam.get(b::getParameter))
                    .severeThresholdForSa(severeSaParam.get(b::getParameter))
                    .badThresholdForSa(badSaParam.get(b::getParameter))
                    .uncertainThresholdForSa(uncertainSaParam.get(b::getParameter))
                    .severeThresholdForIrregular(severeLastSaParam.get(b::getParameter))
                    .badThresholdForIrregular(badLastSaParam.get(b::getParameter))
                    .uncertainThresholdForIrregular(uncertainLastSaParam.get(b::getParameter))
                    .severeThresholdForLastSa(severeLastSaParam.get(b::getParameter))
                    .badThresholdForLastSa(badLastSaParam.get(b::getParameter))
                    .uncertainThresholdForLastSa(uncertainLastSaParam.get(b::getParameter))
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
            builder.reset("Residual seasonality in SA (thresholds)");
            builder.withDouble()
                    .select(bean, "severeThresholdForSa")
                    .display("Severe")
                    .add();
            builder.withDouble()
                    .select(bean, "badThresholdForSa")
                    .display("Bad")
                    .add();
            builder.withDouble()
                    .select(bean, "uncertainThresholdForSa")
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());
            builder.reset("Residual seasonality in Irr. (thresholds)r");
            builder.withDouble()
                    .select(bean, "severeThresholdForIrregular")
                    .display("Severe")
                    .add();
            builder.withDouble()
                    .select(bean, "badThresholdForIrregular")
                    .display("Bad")
                    .add();
            builder.withDouble()
                    .select(bean, "uncertainThresholdForIrregular")
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());
            builder.reset("Residual seasonality in Last SA (thresholds)");
            builder.withDouble()
                    .select(bean, "severeThresholdForLastSa")
                    .display("Severe")
                    .add();
            builder.withDouble()
                    .select(bean, "badThresholdForLastSa")
                    .display("Bad")
                    .add();
            builder.withDouble()
                    .select(bean, "uncertainThresholdForLastSa")
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());

            return sheet;
        }
    }
}
