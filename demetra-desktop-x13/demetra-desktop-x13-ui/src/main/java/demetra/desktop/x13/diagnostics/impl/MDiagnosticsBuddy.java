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
import demetra.desktop.properties.NodePropertySetBuilder;
import org.openide.nodes.Sheet;
import nbbrd.io.text.BooleanProperty;
import demetra.desktop.Converter;
import demetra.desktop.sa.diagnostics.AbstractSaDiagnosticsFactoryBuddy;
import demetra.desktop.sa.diagnostics.AbstractSaDiagnosticsNode;
import demetra.desktop.sa.output.OutputFactoryBuddy;
import demetra.desktop.x13.diagnostics.X13DiagnosticsFactoryBuddy;
import jdplus.x13.diagnostics.MDiagnosticsConfiguration;
import jdplus.x13.diagnostics.MDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mats Maggi
 */
@ServiceProvider(service = X13DiagnosticsFactoryBuddy.class, position = 1400)
public class MDiagnosticsBuddy extends AbstractSaDiagnosticsFactoryBuddy<MDiagnosticsConfiguration, MDiagnosticsBuddy.Bean>
        implements X13DiagnosticsFactoryBuddy<MDiagnosticsConfiguration> {

    @lombok.Data
    public static class Bean {

        private boolean active;
        private double severeThreshold;
        private double badThreshold;
        private boolean all;

        public static Bean of(MDiagnosticsConfiguration config) {
            Bean bean = new Bean();
            bean.active = config.isActive();
            bean.severeThreshold = config.getSevereThreshold();
            bean.badThreshold = config.getBadThreshold();
            bean.all = config.isAll();
            return bean;
        }

        public MDiagnosticsConfiguration asCore() {
            return MDiagnosticsConfiguration.builder()
                    .active(active)
                    .severeThreshold(severeThreshold)
                    .badThreshold(badThreshold)
                    .all(all)
                    .build();
        }
    }

    private static final Converter<Bean, MDiagnosticsConfiguration> BEANCONVERTER = new BeanConverter();

    public MDiagnosticsBuddy() {
        super(new CoreConverter(), BEANCONVERTER);
        this.setActiveDiagnosticsConfiguration(MDiagnosticsConfiguration.getDefault());
    }

    @Override
    public MDiagnosticsFactory createFactory() {
        return new MDiagnosticsFactory(this.getActiveDiagnosticsConfiguration());
    }

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new DiagnosticsNode(bean());
    }

    @Override
    public String getName() {
        return MDiagnosticsFactory.NAME;
    }

    @Override
    public void reset() {
        setCore(MDiagnosticsConfiguration.getDefault());
    }

    static final class BeanConverter implements Converter<Bean, MDiagnosticsConfiguration> {

        @Override
        public MDiagnosticsConfiguration doForward(Bean a) {
            return a.asCore();
        }

        @Override
        public Bean doBackward(MDiagnosticsConfiguration b) {
            return Bean.of(b);
        }
    }

    static final class CoreConverter implements Converter<MDiagnosticsConfiguration, Config> {

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
            builder.withBoolean().select(bean, "all").display("All").add();
            builder.reset("Thresholds");
            builder.withDouble().select(bean, "severeThreshold").display("Severe").add();
            builder.withDouble().select(bean, "badThreshold").display("Bad").add();
            sheet.put(builder.build());
            return sheet;
        }
    }

}
