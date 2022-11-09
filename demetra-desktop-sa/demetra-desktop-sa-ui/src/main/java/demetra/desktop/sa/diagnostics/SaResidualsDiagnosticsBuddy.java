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
import jdplus.regarima.diagnostics.ResidualsDiagnosticsConfiguration;
import jdplus.sa.diagnostics.SaResidualsDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;

/**
 *
 * @author Mats Maggi
 */
public abstract class SaResidualsDiagnosticsBuddy implements SaDiagnosticsFactoryBuddy, Configurable, Persistable, ConfigEditor, Resetable {

    private static final BeanConfigurator<ResidualsDiagnosticsConfiguration, SaResidualsDiagnosticsBuddy> configurator = createConfigurator();

    protected ResidualsDiagnosticsConfiguration config = ResidualsDiagnosticsConfiguration.getDefault();

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new SaResidualsDiagnosticsBuddy.ResidualsDiagnosticsNode<>(config);
    }

    @Override
    public AbstractSaDiagnosticsNode createNodeFor(SaDiagnosticsFactory fac) {
        if (fac instanceof SaResidualsDiagnosticsFactory ofac) {
            return new SaResidualsDiagnosticsBuddy.ResidualsDiagnosticsNode(ofac.getConfiguration());
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return SaResidualsDiagnosticsFactory.NAME;
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
        config = ResidualsDiagnosticsConfiguration.getDefault();
    }

    static final class SaResidualsDiagnosticsConverter implements Converter<ResidualsDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", ResidualsDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty nBadParam = DoubleProperty.of("badNormalityThreshold", ResidualsDiagnosticsConfiguration.NBAD);
        private final DoubleProperty nUncertainParam = DoubleProperty.of("uncertainNormalityThreshold", ResidualsDiagnosticsConfiguration.NUNC);
        private final DoubleProperty tdSevereParam = DoubleProperty.of("severeTdThreshold", ResidualsDiagnosticsConfiguration.TDSEV);
        private final DoubleProperty tdBadParam = DoubleProperty.of("badTdThreshold", ResidualsDiagnosticsConfiguration.TDBAD);
        private final DoubleProperty tdUncertainParam = DoubleProperty.of("uncertainTdThreshold", ResidualsDiagnosticsConfiguration.TDUNC);
        private final DoubleProperty sSevereParam = DoubleProperty.of("severeSeasThreshold", ResidualsDiagnosticsConfiguration.SSEV);
        private final DoubleProperty sBadParam = DoubleProperty.of("badSeasThreshold", ResidualsDiagnosticsConfiguration.SBAD);
        private final DoubleProperty sUncertainParam = DoubleProperty.of("uncertainSeasThreshold", ResidualsDiagnosticsConfiguration.SUNC);

        @Override
        public Config doForward(ResidualsDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(OutputFactoryBuddy.class.getName(), "Csv_Matrix", "");
            activeParam.set(result::parameter, a.isActive());
            nBadParam.set(result::parameter, a.getBadThresholdForNormality());
            nUncertainParam.set(result::parameter, a.getUncertainThresholdForNormality());
            tdSevereParam.set(result::parameter, a.getSevereThresholdForTradingDaysPeak());
            tdBadParam.set(result::parameter, a.getBadThresholdForTradingDaysPeak());
            tdUncertainParam.set(result::parameter, a.getUncertainThresholdForTradingDaysPeak());
            sSevereParam.set(result::parameter, a.getSevereThresholdForSeasonalPeaks());
            sBadParam.set(result::parameter, a.getBadThresholdForSeasonalPeaks());
            sUncertainParam.set(result::parameter, a.getUncertainThresholdForSeasonalPeaks());
            return result.build();
        }

        @Override
        public ResidualsDiagnosticsConfiguration doBackward(Config b) {
            return ResidualsDiagnosticsConfiguration.builder()
                    .active(activeParam.get(b::getParameter))
                    .badThresholdForNormality(nBadParam.get(b::getParameter))
                    .uncertainThresholdForNormality(nUncertainParam.get(b::getParameter))
                    .severeThresholdForTradingDaysPeak(tdSevereParam.get(b::getParameter))
                    .badThresholdForTradingDaysPeak(tdBadParam.get(b::getParameter))
                    .uncertainThresholdForTradingDaysPeak(tdUncertainParam.get(b::getParameter))
                    .severeThresholdForSeasonalPeaks(sSevereParam.get(b::getParameter))
                    .badThresholdForSeasonalPeaks(sBadParam.get(b::getParameter))
                    .uncertainThresholdForSeasonalPeaks(sUncertainParam.get(b::getParameter))
                    .build();
        }
    }

    static class ResidualsDiagnosticsNode<R> extends AbstractSaDiagnosticsNode<ResidualsDiagnosticsConfiguration, R> {

        public ResidualsDiagnosticsNode(ResidualsDiagnosticsConfiguration config) {
            super(config);
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();

            NodePropertySetBuilder builder = new NodePropertySetBuilder();
            builder.reset("Behaviour");
            builder.withBoolean().select("active", config::isActive, active -> activate(active)).display("Enabled").add();
            sheet.put(builder.build());
            builder.reset("Normality");
            builder.withDouble()
                    .select("nbad", config::getBadThresholdForNormality, d -> {
                config = config.toBuilder().badThresholdForNormality(d).build();
            })
                    .display("Bad")
                    .add();
            builder.withDouble().select("nuncertain", config::getUncertainThresholdForNormality, d -> {
                config = config.toBuilder().uncertainThresholdForNormality(d).build();
            })
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());
            builder.reset("Trading days");
            builder.withDouble()
                    .select("tdsevere", config::getSevereThresholdForTradingDaysPeak, d -> {
                config = config.toBuilder().severeThresholdForTradingDaysPeak(d).build();
            })
                    .display("Severe")
                    .add();
            builder.withDouble()
                    .select("tdbad", config::getBadThresholdForTradingDaysPeak, d -> {
                config = config.toBuilder().badThresholdForTradingDaysPeak(d).build();
            })
                    .display("Bad")
                    .add();
            builder.withDouble().select("tduncertain", config::getUncertainThresholdForTradingDaysPeak, d -> {
                config = config.toBuilder().uncertainThresholdForTradingDaysPeak(d).build();
            })
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());
            builder.reset("Seasonal");
            builder.withDouble()
                    .select("ssevere", config::getSevereThresholdForSeasonalPeaks, d -> {
                config = config.toBuilder().severeThresholdForTradingDaysPeak(d).build();
            })
                    .display("Severe")
                    .add();
            builder.withDouble()
                    .select("sbad", config::getBadThresholdForSeasonalPeaks, d -> {
                config = config.toBuilder().badThresholdForSeasonalPeaks(d).build();
            })
                    .display("Bad")
                    .add();
            builder.withDouble().select("suncertain", config::getUncertainThresholdForSeasonalPeaks, d -> {
                config = config.toBuilder().uncertainThresholdForSeasonalPeaks(d).build();
            })
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());

            return sheet;
        }
    }

    static final class Handler implements BeanHandler<ResidualsDiagnosticsConfiguration, SaResidualsDiagnosticsBuddy> {

        @Override
        public ResidualsDiagnosticsConfiguration load(SaResidualsDiagnosticsBuddy resource) {
            return resource.config;
        }

        @Override
        public void store(SaResidualsDiagnosticsBuddy resource, ResidualsDiagnosticsConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class Editor implements BeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit diagnostics on residuals")
                    .editNode(new SaResidualsDiagnosticsBuddy.ResidualsDiagnosticsNode<>((ResidualsDiagnosticsConfiguration) bean));
        }
    }

    private static BeanConfigurator<ResidualsDiagnosticsConfiguration, SaResidualsDiagnosticsBuddy> createConfigurator() {
        return new BeanConfigurator<>(new Handler(),
                new SaResidualsDiagnosticsBuddy.SaResidualsDiagnosticsConverter(),
                new Editor()
        );
    }
}
