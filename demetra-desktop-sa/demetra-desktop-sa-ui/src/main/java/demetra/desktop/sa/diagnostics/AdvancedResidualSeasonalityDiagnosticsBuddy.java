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
import demetra.desktop.sa.output.OutputFactoryBuddy;
import jdplus.regarima.diagnostics.OutliersDiagnosticsConfiguration;
import jdplus.sa.diagnostics.AdvancedResidualSeasonalityDiagnosticsConfiguration;
import jdplus.sa.diagnostics.AdvancedResidualSeasonalityDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;

/**
 *
 * @author Mats Maggi
 */
public class AdvancedResidualSeasonalityDiagnosticsBuddy
        extends AbstractSaDiagnosticsFactoryBuddy<AdvancedResidualSeasonalityDiagnosticsConfiguration, AdvancedResidualSeasonalityDiagnosticsBuddy.Bean> {

    @lombok.Data
    public static final class Bean {

        private boolean active;
        private double severeThreshold;
        private double badThreshold;
        private double uncertainThreshold;
        private boolean qs;
        private boolean ftest;

        static Bean of(AdvancedResidualSeasonalityDiagnosticsConfiguration config) {
            Bean bean = new Bean();
            bean.active = config.isActive();
            bean.severeThreshold = config.getSevereThreshold();
            bean.badThreshold = config.getBadThreshold();
            bean.uncertainThreshold = config.getUncertainThreshold();
            bean.qs = config.isQs();
            bean.ftest = config.isFtest();
            return bean;
        }

        AdvancedResidualSeasonalityDiagnosticsConfiguration asCore() {
            return AdvancedResidualSeasonalityDiagnosticsConfiguration.builder()
                    .active(active)
                    .severeThreshold(severeThreshold)
                    .badThreshold(badThreshold)
                    .uncertainThreshold(uncertainThreshold)
                    .qs(qs)
                    .ftest(ftest)
                    .build();
        }
    }
    
    private static final Converter<Bean, AdvancedResidualSeasonalityDiagnosticsConfiguration> BEANCONVERTER=new BeanConverter();
            
    protected AdvancedResidualSeasonalityDiagnosticsBuddy() {
        super(new CoreConverter(), BEANCONVERTER);
    }

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new DiagnosticsNode(bean());
    }

    @Override
    public String getName() {
        return AdvancedResidualSeasonalityDiagnosticsFactory.NAME;
    }

    @Override
    public void reset() {
        setCore(AdvancedResidualSeasonalityDiagnosticsConfiguration.getDefault());
    }
    
    static final class BeanConverter implements Converter<Bean, AdvancedResidualSeasonalityDiagnosticsConfiguration>{

        @Override
        public AdvancedResidualSeasonalityDiagnosticsConfiguration doForward(Bean a) {
            return a.asCore();
        }

        @Override
        public Bean doBackward(AdvancedResidualSeasonalityDiagnosticsConfiguration b) {
            return Bean.of(b);
        }

    }

    static final class CoreConverter implements Converter<AdvancedResidualSeasonalityDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", AdvancedResidualSeasonalityDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty severeParam = DoubleProperty.of("severeThreshold", OutliersDiagnosticsConfiguration.SEV);
        private final DoubleProperty badParam = DoubleProperty.of("badThreshold", OutliersDiagnosticsConfiguration.BAD);
        private final DoubleProperty uncertainParam = DoubleProperty.of("uncertainThreshold", OutliersDiagnosticsConfiguration.UNC);
        private final BooleanProperty qsParam = BooleanProperty.of("qs", true);
        private final BooleanProperty fParam = BooleanProperty.of("ftest", true);

        @Override
        public Config doForward(AdvancedResidualSeasonalityDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(OutputFactoryBuddy.class.getName(), "Csv_Matrix", "");
            activeParam.set(result::parameter, a.isActive());
            severeParam.set(result::parameter, a.getSevereThreshold());
            badParam.set(result::parameter, a.getBadThreshold());
            uncertainParam.set(result::parameter, a.getUncertainThreshold());
            qsParam.set(result::parameter, a.isQs());
            fParam.set(result::parameter, a.isFtest());
            return result.build();
        }

        @Override
        public AdvancedResidualSeasonalityDiagnosticsConfiguration doBackward(Config b) {
            return AdvancedResidualSeasonalityDiagnosticsConfiguration.builder()
                    .active(activeParam.get(b::getParameter))
                    .severeThreshold(severeParam.get(b::getParameter))
                    .badThreshold(badParam.get(b::getParameter))
                    .uncertainThreshold(uncertainParam.get(b::getParameter))
                    .qs(qsParam.get(b::getParameter))
                    .ftest(fParam.get(b::getParameter))
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
            builder.withBoolean()
                    .select(bean, "active")
                    .display("Enabled")
                    .add();
            sheet.put(builder.build());
            builder.reset("Tests");
            builder.withBoolean()
                    .select(bean, "qs")
                    .display("QS test")
                    .add();
            builder.withBoolean()
                    .select(bean, "ftest")
                    .display("F Test on seas. dummies")
                    .add();
            sheet.put(builder.build());

            builder.reset("Thresholds");
            builder.withDouble()
                    .select(bean, "severeThreshold")
                    .display("Severe")
                    .add();
            builder.withDouble()
                    .select(bean, "badThreshold")
                    .display("Bad")
                    .add();
            builder.withDouble()
                    .select(bean, "uncertainThreshold")
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());

            return sheet;
        }
    }

}
