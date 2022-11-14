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
import jdplus.sa.diagnostics.SpectralDiagnosticsFactory;
import jdplus.sa.diagnostics.SpectralDiagnosticsConfiguration;
import nbbrd.io.text.DoubleProperty;
import nbbrd.io.text.IntProperty;

/**
 *
 * @author Mats Maggi
 */
public class SpectralDiagnosticsBuddy extends AbstractSaDiagnosticsFactoryBuddy<SpectralDiagnosticsConfiguration, SpectralDiagnosticsBuddy.Bean> {

    @lombok.Data
    public static class Bean {

        private boolean active;
        private double sensibility;
        private int length;
        private boolean strict;

        static Bean of(SpectralDiagnosticsConfiguration config) {
            Bean bean = new Bean();
            bean.active = config.isActive();
            bean.length = config.getLength();
            bean.sensibility = config.getSensibility();
            bean.strict = config.isStrict();

            return bean;
        }

        SpectralDiagnosticsConfiguration asCore() {
            return SpectralDiagnosticsConfiguration.builder()
                    .active(active)
                    .strict(strict)
                    .length(length)
                    .sensibility(sensibility)
                    .build();
        }
    }

    private static final Converter<Bean, SpectralDiagnosticsConfiguration> BEANCONVERTER = new BeanConverter();

    protected SpectralDiagnosticsBuddy() {
        super(new CoreConverter(), BEANCONVERTER);
    }
    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new DiagnosticsNode(bean());
    }

    @Override
    public String getName() {
        return SpectralDiagnosticsFactory.NAME;
    }

    @Override
    public void reset() {
        setCore(SpectralDiagnosticsConfiguration.getDefault());
    }

    static final class BeanConverter implements Converter<Bean, SpectralDiagnosticsConfiguration> {

        @Override
        public SpectralDiagnosticsConfiguration doForward(Bean a) {
            return a.asCore();
        }

        @Override
        public Bean doBackward(SpectralDiagnosticsConfiguration b) {
            return Bean.of(b);
        }
    }

    static final class CoreConverter implements Converter<SpectralDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", SpectralDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty sensibilityParam = DoubleProperty.of("sensibility", SpectralDiagnosticsConfiguration.SENSIBILITY);
        private final IntProperty lengthParam = IntProperty.of("length", SpectralDiagnosticsConfiguration.LENGTH);
        private final BooleanProperty strictParam = BooleanProperty.of("strict", SpectralDiagnosticsConfiguration.STRICT);

        @Override
        public Config doForward(SpectralDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder("diagnostics", "spectral_diagnostics", "3.0");
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

    private static class DiagnosticsNode extends AbstractSaDiagnosticsNode<Bean> {

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
            builder.withBoolean()
                    .select(bean, "strict")
                    .display("Strict")
                    .add();
            sheet.put(builder.build());

            builder.reset("Properties");
            builder.withDouble()
                    .select(bean, "sensibility")
                    .display("Sensibility")
                    .add();
            builder.withInt()
                    .select(bean, "length")
                    .display("Length")
                    .add();
            sheet.put(builder.build());

            return sheet;
        }
    }

}
