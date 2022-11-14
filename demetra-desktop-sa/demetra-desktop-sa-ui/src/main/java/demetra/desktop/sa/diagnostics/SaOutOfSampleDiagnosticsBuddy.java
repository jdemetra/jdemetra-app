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
import jdplus.regarima.diagnostics.OutOfSampleDiagnosticsConfiguration;
import jdplus.sa.diagnostics.SaOutOfSampleDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;

/**
 *
 * @author Mats Maggi
 */
public class SaOutOfSampleDiagnosticsBuddy extends AbstractSaDiagnosticsFactoryBuddy<OutOfSampleDiagnosticsConfiguration, SaOutOfSampleDiagnosticsBuddy.Bean> {

    @lombok.Data
    public static class Bean {

        private boolean active;
        private double badThreshold;
        private double uncertainThreshold;
        private boolean diagnosticOnMean, diagnosticOnVariance;
        private double outOfSampleLength;

        public static Bean of(OutOfSampleDiagnosticsConfiguration config) {
            Bean bean = new Bean();
            bean.active = config.isActive();
            bean.diagnosticOnMean = config.isDiagnosticOnMean();
            bean.diagnosticOnVariance = config.isDiagnosticOnVariance();
            bean.badThreshold = config.getBadThreshold();
            bean.uncertainThreshold = config.getUncertainThreshold();
            bean.outOfSampleLength = config.getOutOfSampleLength();
            return bean;
        }

        public OutOfSampleDiagnosticsConfiguration asCore() {
            return OutOfSampleDiagnosticsConfiguration.builder()
                    .active(active)
                    .diagnosticOnMean(diagnosticOnMean)
                    .diagnosticOnVariance(diagnosticOnVariance)
                    .badThreshold(badThreshold)
                    .uncertainThreshold(uncertainThreshold)
                    .outOfSampleLength(outOfSampleLength)
                    .build();
        }

    }

    private static final Converter<Bean, OutOfSampleDiagnosticsConfiguration> BEANCONVERTER = new BeanConverter();

    protected SaOutOfSampleDiagnosticsBuddy() {
        super(new CoreConverter(), BEANCONVERTER);
    }

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new DiagnosticsNode(bean());
    }

    @Override
    public String getName() {
        return SaOutOfSampleDiagnosticsFactory.NAME;
    }

    @Override
    public void reset() {
        setCore(OutOfSampleDiagnosticsConfiguration.getDefault());
    }

    static final class BeanConverter implements Converter<Bean, OutOfSampleDiagnosticsConfiguration> {

        @Override
        public OutOfSampleDiagnosticsConfiguration doForward(Bean a) {
            return a.asCore();
        }

        @Override
        public Bean doBackward(OutOfSampleDiagnosticsConfiguration b) {
            return Bean.of(b);
        }
    }

    static final class CoreConverter implements Converter<OutOfSampleDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", OutOfSampleDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty badParam = DoubleProperty.of("badThreshold", OutOfSampleDiagnosticsConfiguration.BAD);
        private final DoubleProperty uncertainParam = DoubleProperty.of("uncertainThreshold", OutOfSampleDiagnosticsConfiguration.UNC);
        private final DoubleProperty lengthParam = DoubleProperty.of("length", OutOfSampleDiagnosticsConfiguration.LENGTH);
        private final BooleanProperty meanParam = BooleanProperty.of("testMean", true);
        private final BooleanProperty varianceParam = BooleanProperty.of("testVariance", true);

        @Override
        public Config doForward(OutOfSampleDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(OutputFactoryBuddy.class.getName(), "Csv_Matrix", "");
            activeParam.set(result::parameter, a.isActive());
            badParam.set(result::parameter, a.getBadThreshold());
            uncertainParam.set(result::parameter, a.getUncertainThreshold());
            lengthParam.set(result::parameter, a.getOutOfSampleLength());
            meanParam.set(result::parameter, a.isDiagnosticOnMean());
            varianceParam.set(result::parameter, a.isDiagnosticOnVariance());
            return result.build();
        }

        @Override
        public OutOfSampleDiagnosticsConfiguration doBackward(Config b) {
            return OutOfSampleDiagnosticsConfiguration.builder()
                    .active(activeParam.get(b::getParameter))
                    .badThreshold(badParam.get(b::getParameter))
                    .uncertainThreshold(uncertainParam.get(b::getParameter))
                    .outOfSampleLength(lengthParam.get(b::getParameter))
                    .diagnosticOnMean(meanParam.get(b::getParameter))
                    .diagnosticOnVariance(varianceParam.get(b::getParameter))
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
            builder.reset("Tests");
            builder.withBoolean().select(bean, "diagnosticOnMean").display("Mean").add();
            builder.withBoolean().select(bean, "diagnosticOnVariance").display("Variance").add();
            sheet.put(builder.build());

            builder.reset("Thresholds");
            builder.withDouble().select(bean, "badThreshold").display("Bad").add();
            builder.withDouble().select(bean, "uncertainThreshold").display("Uncertain").add();
            sheet.put(builder.build());

            builder.reset("Other");
            builder.withDouble().select(bean, "outOfSampleLength").display("Uncertain").add();
            sheet.put(builder.build());

            return sheet;
        }
    }

}
