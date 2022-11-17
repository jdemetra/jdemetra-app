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
import jdplus.sa.diagnostics.CoherenceDiagnosticsConfiguration;
import jdplus.sa.diagnostics.CoherenceDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;
import nbbrd.io.text.IntProperty;

/**
 *
 * @author Mats Maggi
 */
public class CoherenceDiagnosticsBuddy extends AbstractSaDiagnosticsFactoryBuddy<CoherenceDiagnosticsConfiguration, CoherenceDiagnosticsBuddy.Bean> {

    @lombok.Data
    public static class Bean {

        private boolean active;
        private double tolerance;
        private double errorThreshold;
        private double severeThreshold;
        private double badThreshold;
        private double uncertainThreshold;
        private int shortSeriesLimit;

        public static Bean of(CoherenceDiagnosticsConfiguration config) {
            Bean bean = new Bean();
            bean.active = config.isActive();
            bean.errorThreshold = config.getErrorThreshold();
            bean.severeThreshold = config.getSevereThreshold();
            bean.badThreshold = config.getBadThreshold();
            bean.uncertainThreshold = config.getUncertainThreshold();
            bean.tolerance = config.getTolerance();
            bean.shortSeriesLimit = config.getShortSeriesLimit();
            return bean;
        }

        public CoherenceDiagnosticsConfiguration asCore() {
            return CoherenceDiagnosticsConfiguration.builder()
                    .active(active)
                    .errorThreshold(errorThreshold)
                    .severeThreshold(severeThreshold)
                    .badThreshold(badThreshold)
                    .uncertainThreshold(uncertainThreshold)
                    .tolerance(tolerance)
                    .shortSeriesLimit(shortSeriesLimit)
                    .build();
        }

    }

    private static final Converter<Bean, CoherenceDiagnosticsConfiguration> BEANCONVERTER = new BeanConverter();

    protected CoherenceDiagnosticsBuddy() {
        super(new CoreConverter(), BEANCONVERTER);
    }

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new DiagnosticsNode(bean());
    }

    @Override
    public String getName() {
        return CoherenceDiagnosticsFactory.NAME;
    }

    @Override
    public void reset() {
        setCore(CoherenceDiagnosticsConfiguration.getDefault());
    }

    static final class BeanConverter implements Converter<Bean, CoherenceDiagnosticsConfiguration> {

        @Override
        public CoherenceDiagnosticsConfiguration doForward(Bean a) {
            return a.asCore();
        }

        @Override
        public Bean doBackward(CoherenceDiagnosticsConfiguration b) {
            return Bean.of(b);
        }
    }

    static final class CoreConverter implements Converter<CoherenceDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", CoherenceDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty tolParam = DoubleProperty.of("tolerance", CoherenceDiagnosticsConfiguration.TOL);
        private final DoubleProperty errorParam = DoubleProperty.of("errorThreshold", CoherenceDiagnosticsConfiguration.ERR);
        private final DoubleProperty severeParam = DoubleProperty.of("severeThreshold", CoherenceDiagnosticsConfiguration.SEV);
        private final DoubleProperty badParam = DoubleProperty.of("badThreshold", CoherenceDiagnosticsConfiguration.BAD);
        private final DoubleProperty uncertainParam = DoubleProperty.of("uncertainThreshold", CoherenceDiagnosticsConfiguration.UNC);
        private final IntProperty shortParam = IntProperty.of("shortSeriesLimit", CoherenceDiagnosticsConfiguration.SHORT);

        @Override
        public Config doForward(CoherenceDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder("diagnostics", CoherenceDiagnosticsFactory.NAME, "3.0");
            activeParam.set(result::parameter, a.isActive());
            tolParam.set(result::parameter, a.getTolerance());
            errorParam.set(result::parameter, a.getErrorThreshold());
            severeParam.set(result::parameter, a.getSevereThreshold());
            badParam.set(result::parameter, a.getBadThreshold());
            uncertainParam.set(result::parameter, a.getUncertainThreshold());
            shortParam.set(result::parameter, a.getShortSeriesLimit());
            return result.build();
        }

        @Override
        public CoherenceDiagnosticsConfiguration doBackward(Config b) {
            return CoherenceDiagnosticsConfiguration.builder()
                    .active(activeParam.get(b::getParameter))
                    .errorThreshold(errorParam.get(b::getParameter))
                    .severeThreshold(severeParam.get(b::getParameter))
                    .badThreshold(badParam.get(b::getParameter))
                    .uncertainThreshold(uncertainParam.get(b::getParameter))
                    .tolerance(tolParam.get(b::getParameter))
                    .shortSeriesLimit(shortParam.get(b::getParameter))
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

            builder.reset("Thresholds");
            builder.withDouble()
                    .select(bean, "errorThreshold")
                    .display("Error")
                    .add();
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

            builder.reset("Other");
            builder.withDouble()
                    .select(bean, "tolerance")
                    .display("Tolerance")
                    .add();
            builder.withInt()
                    .select(bean, "shortSeriesLimit")
                    .display("Short series limit")
                    .add();
            sheet.put(builder.build());

            return sheet;
        }
    }

}
