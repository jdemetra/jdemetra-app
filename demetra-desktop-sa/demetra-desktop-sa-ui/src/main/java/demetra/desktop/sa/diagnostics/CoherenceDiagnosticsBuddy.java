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
import jdplus.sa.diagnostics.CoherenceDiagnosticsConfiguration;
import jdplus.sa.diagnostics.CoherenceDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;
import nbbrd.io.text.IntProperty;

/**
 *
 * @author Mats Maggi
 */
public abstract class CoherenceDiagnosticsBuddy implements SaDiagnosticsFactoryBuddy, Configurable, Persistable, ConfigEditor, Resetable {

    private static final BeanConfigurator<CoherenceDiagnosticsConfiguration, CoherenceDiagnosticsBuddy> configurator = createConfigurator();

    protected CoherenceDiagnosticsConfiguration config = CoherenceDiagnosticsConfiguration.getDefault();

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new CoherenceDiagnosticsBuddy.CoherenceDiagnosticsNode<>(config);
    }

    @Override
    public AbstractSaDiagnosticsNode createNodeFor(SaDiagnosticsFactory fac) {
        if (fac instanceof CoherenceDiagnosticsFactory ofac) {
            return new CoherenceDiagnosticsBuddy.CoherenceDiagnosticsNode(ofac.getConfiguration());
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return CoherenceDiagnosticsFactory.NAME;
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
        config = CoherenceDiagnosticsConfiguration.getDefault();
    }

    static final class SaCoherenceDiagnosticsConverter implements Converter<CoherenceDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", CoherenceDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty tolParam = DoubleProperty.of("tolerance", CoherenceDiagnosticsConfiguration.TOL);
        private final DoubleProperty errorParam = DoubleProperty.of("errorThreshold", CoherenceDiagnosticsConfiguration.ERR);
        private final DoubleProperty severeParam = DoubleProperty.of("severeThreshold", CoherenceDiagnosticsConfiguration.SEV);
        private final DoubleProperty badParam = DoubleProperty.of("badThreshold", CoherenceDiagnosticsConfiguration.BAD);
        private final DoubleProperty uncertainParam = DoubleProperty.of("uncertainThreshold", CoherenceDiagnosticsConfiguration.UNC);
        private final IntProperty shortParam = IntProperty.of("shortSeriesLimit", CoherenceDiagnosticsConfiguration.SHORT);

        @Override
        public Config doForward(CoherenceDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(OutputFactoryBuddy.class.getName(), "Csv_Matrix", "");
            activeParam.set(result::parameter, a.isActive());
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
                    .shortSeriesLimit(shortParam.get(b::getParameter))
                    .build();
        }
    }

    static class CoherenceDiagnosticsNode<R> extends AbstractSaDiagnosticsNode<CoherenceDiagnosticsConfiguration, R> {

        public CoherenceDiagnosticsNode(CoherenceDiagnosticsConfiguration config) {
            super(config);
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();

            NodePropertySetBuilder builder = new NodePropertySetBuilder();
            builder.reset("Behaviour");
            builder.withBoolean()
                    .select("active", config::isActive, active -> activate(active))
                    .display("Enabled")
                    .add();
            sheet.put(builder.build());

            builder.reset("Thresholds");
            builder.withDouble()
                    .select("error", config::getErrorThreshold, d -> {
                config = config.toBuilder().errorThreshold(d).build();
            })
                    .display("Error")
                    .add();
            builder.withDouble()
                    .select("Severe", config::getSevereThreshold, d -> {
                config = config.toBuilder().severeThreshold(d).build();
            })
                    .display("Severe")
                    .add();
            builder.withDouble()
                    .select("bad", config::getBadThreshold, d -> {
                config = config.toBuilder().badThreshold(d).build();
            })
                    .display("Bad")
                    .add();
            builder.withDouble()
                    .select("uncertain", config::getUncertainThreshold, d -> {
                config = config.toBuilder().uncertainThreshold(d).build();
            })
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());

            builder.reset("Other");
            builder.withInt()
                    .select("shortSeriesLimit", config::getShortSeriesLimit, d -> {
                config = config.toBuilder().shortSeriesLimit(d).build();
            })
                    .display("Short series limit")
                    .add();
            sheet.put(builder.build());

            return sheet;
        }
    }

    static final class Handler implements BeanHandler<CoherenceDiagnosticsConfiguration, CoherenceDiagnosticsBuddy> {

        @Override
        public CoherenceDiagnosticsConfiguration load(CoherenceDiagnosticsBuddy resource) {
            return resource.config;
        }

        @Override
        public void store(CoherenceDiagnosticsBuddy resource, CoherenceDiagnosticsConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class Editor implements BeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit coherence diagnostics")
                    .editNode(new CoherenceDiagnosticsBuddy.CoherenceDiagnosticsNode<>((CoherenceDiagnosticsConfiguration) bean));
        }
    }

    private static BeanConfigurator<CoherenceDiagnosticsConfiguration, CoherenceDiagnosticsBuddy> createConfigurator() {
        return new BeanConfigurator<>(new Handler(),
                new CoherenceDiagnosticsBuddy.SaCoherenceDiagnosticsConverter(),
                new Editor()
        );
    }
}
