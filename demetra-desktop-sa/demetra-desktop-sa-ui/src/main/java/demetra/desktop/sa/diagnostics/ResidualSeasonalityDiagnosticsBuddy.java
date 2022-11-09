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
import jdplus.sa.diagnostics.ResidualSeasonalityDiagnosticsConfiguration;
import jdplus.sa.diagnostics.ResidualSeasonalityDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;

/**
 *
 * @author Mats Maggi
 */
public abstract class ResidualSeasonalityDiagnosticsBuddy implements SaDiagnosticsFactoryBuddy, Configurable, Persistable, ConfigEditor, Resetable {

    private static final BeanConfigurator<ResidualSeasonalityDiagnosticsConfiguration, ResidualSeasonalityDiagnosticsBuddy> configurator = createConfigurator();

    protected ResidualSeasonalityDiagnosticsConfiguration config = ResidualSeasonalityDiagnosticsConfiguration.getDefault();

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new ResidualSeasonalityDiagnosticsBuddy.ResidualSeasonalityDiagnosticsNode<>(config);
    }

    @Override
    public AbstractSaDiagnosticsNode createNodeFor(SaDiagnosticsFactory fac) {
        if (fac instanceof ResidualSeasonalityDiagnosticsFactory ofac) {
            return new ResidualSeasonalityDiagnosticsBuddy.ResidualSeasonalityDiagnosticsNode(ofac.getConfiguration());
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return ResidualSeasonalityDiagnosticsFactory.NAME;
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
        config = ResidualSeasonalityDiagnosticsConfiguration.getDefault();
    }

    static final class SaResidualSeasonalityDiagnosticsConverter implements Converter<ResidualSeasonalityDiagnosticsConfiguration, Config> {

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
            Config.Builder result = Config.builder(OutputFactoryBuddy.class.getName(), "Csv_Matrix", "");
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

    static class ResidualSeasonalityDiagnosticsNode<R> extends AbstractSaDiagnosticsNode<ResidualSeasonalityDiagnosticsConfiguration, R> {

        public ResidualSeasonalityDiagnosticsNode(ResidualSeasonalityDiagnosticsConfiguration config) {
            super(config);
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();

            NodePropertySetBuilder builder = new NodePropertySetBuilder();
            builder.reset("Behaviour");
            builder.withBoolean().select("active", config::isActive, active -> activate(active)).display("Enabled").add();
            sheet.put(builder.build());
            builder.reset("Residual seasonality in SA (thresholds)");
            builder.withDouble()
                    .select("sevsa", config::getSevereThresholdForSa, d -> {
                config = config.toBuilder().severeThresholdForSa(d).build();
            })
                    .display("Severe")
                    .add();
            builder.withDouble()
                    .select("badsa", config::getBadThresholdForSa, d -> {
                config = config.toBuilder().badThresholdForSa(d).build();
            })
                    .display("Bad")
                    .add();
            builder.withDouble()
                    .select("uncsa", config::getUncertainThresholdForSa, d -> {
                config = config.toBuilder().uncertainThresholdForSa(d).build();
            })
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());
            builder.reset("Residual seasonality in Irr. (thresholds)r");
            builder.withDouble()
                    .select("sevirr", config::getSevereThresholdForIrregular, d -> {
                config = config.toBuilder().severeThresholdForIrregular(d).build();
            })
                    .display("Severe")
                    .add();
            builder.withDouble()
                    .select("badirr", config::getBadThresholdForIrregular, d -> {
                config = config.toBuilder().badThresholdForIrregular(d).build();
            })
                    .display("Bad")
                    .add();
            builder.withDouble()
                    .select("uncirr", config::getUncertainThresholdForIrregular, d -> {
                config = config.toBuilder().uncertainThresholdForIrregular(d).build();
            })
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());
            builder.reset("Residual seasonality in Last SA (thresholds)");
            builder.withDouble()
                    .select("sevlsa", config::getSevereThresholdForLastSa, d -> {
                config = config.toBuilder().severeThresholdForLastSa(d).build();
            })
                    .display("Severe")
                    .add();
            builder.withDouble()
                    .select("badlsa", config::getBadThresholdForLastSa, d -> {
                config = config.toBuilder().badThresholdForLastSa(d).build();
            })
                    .display("Bad")
                    .add();
            builder.withDouble()
                    .select("unclsa", config::getUncertainThresholdForLastSa, d -> {
                config = config.toBuilder().uncertainThresholdForLastSa(d).build();
            })
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());

            return sheet;
        }
    }

    static final class Handler implements BeanHandler<ResidualSeasonalityDiagnosticsConfiguration, ResidualSeasonalityDiagnosticsBuddy> {

        @Override
        public ResidualSeasonalityDiagnosticsConfiguration load(ResidualSeasonalityDiagnosticsBuddy resource) {
            return resource.config;
        }

        @Override
        public void store(ResidualSeasonalityDiagnosticsBuddy resource, ResidualSeasonalityDiagnosticsConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class Editor implements BeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit residual seasonality diagnostics")
                    .editNode(new ResidualSeasonalityDiagnosticsBuddy.ResidualSeasonalityDiagnosticsNode<>((ResidualSeasonalityDiagnosticsConfiguration) bean));
        }
    }

    private static BeanConfigurator<ResidualSeasonalityDiagnosticsConfiguration, ResidualSeasonalityDiagnosticsBuddy> createConfigurator() {
        return new BeanConfigurator<>(new Handler(),
                new ResidualSeasonalityDiagnosticsBuddy.SaResidualSeasonalityDiagnosticsConverter(),
                new Editor()
        );
    }
}
