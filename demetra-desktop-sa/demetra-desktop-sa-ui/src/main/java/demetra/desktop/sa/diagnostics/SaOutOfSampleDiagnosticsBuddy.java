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
import jdplus.regarima.diagnostics.OutOfSampleDiagnosticsConfiguration;
import jdplus.sa.diagnostics.SaOutOfSampleDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;

/**
 *
 * @author Mats Maggi
 */
public abstract class SaOutOfSampleDiagnosticsBuddy implements SaDiagnosticsFactoryBuddy, Configurable, Persistable, ConfigEditor, Resetable {

    private static final BeanConfigurator<OutOfSampleDiagnosticsConfiguration, SaOutOfSampleDiagnosticsBuddy> configurator = createConfigurator();

    protected OutOfSampleDiagnosticsConfiguration config = OutOfSampleDiagnosticsConfiguration.getDefault();

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new SaOutOfSampleDiagnosticsBuddy.OutOfSampleDiagnosticsNode<>(config);
    }

    @Override
    public AbstractSaDiagnosticsNode createNodeFor(SaDiagnosticsFactory fac) {
        if (fac instanceof SaOutOfSampleDiagnosticsFactory ofac) {
            return new SaOutOfSampleDiagnosticsBuddy.OutOfSampleDiagnosticsNode(ofac.getConfiguration());
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return SaOutOfSampleDiagnosticsFactory.NAME;
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
        config = OutOfSampleDiagnosticsConfiguration.getDefault();
    }

    static final class SaOutOfSampleDiagnosticsConverter implements Converter<OutOfSampleDiagnosticsConfiguration, Config> {

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

    static class OutOfSampleDiagnosticsNode<R> extends AbstractSaDiagnosticsNode<OutOfSampleDiagnosticsConfiguration, R> {

        public OutOfSampleDiagnosticsNode(OutOfSampleDiagnosticsConfiguration config) {
            super(config);
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();

            NodePropertySetBuilder builder = new NodePropertySetBuilder();
            builder.reset("Behaviour");
            builder.withBoolean().select("active", config::isActive, active -> activate(active)).display("Enabled").add();
            sheet.put(builder.build());
            builder.reset("Tests");
            builder.withBoolean().select("mean", config::isDiagnosticOnMean, ok -> {
                config = config.toBuilder().diagnosticOnMean(ok).build();
            }).display("Test on mean").add();
            builder.withBoolean().select("variance", config::isDiagnosticOnVariance, ok -> {
                config = config.toBuilder().diagnosticOnVariance(ok).build();
            }).display("Test on variance").add();
            sheet.put(builder.build());

            builder.reset("Thresholds");
            builder.withDouble().select("bad", config::getBadThreshold, d -> {
                config = config.toBuilder().badThreshold(d).build();
            }).display("Bad").add();
            builder.withDouble().select("uncertain", config::getUncertainThreshold, d -> {
                config = config.toBuilder().uncertainThreshold(d).build();
            }).display("Uncertain").add();
            sheet.put(builder.build());

            builder.reset("Other");
            builder.withDouble().select("length", config::getOutOfSampleLength, d -> {
                config = config.toBuilder().outOfSampleLength(d).build();
            }).display("Uncertain").add();
            sheet.put(builder.build());

            return sheet;
        }
    }

    static final class Handler implements BeanHandler<OutOfSampleDiagnosticsConfiguration, SaOutOfSampleDiagnosticsBuddy> {

        @Override
        public OutOfSampleDiagnosticsConfiguration load(SaOutOfSampleDiagnosticsBuddy resource) {
            return resource.config;
        }

        @Override
        public void store(SaOutOfSampleDiagnosticsBuddy resource, OutOfSampleDiagnosticsConfiguration bean) {
            resource.config = bean;
        }
    }

    private static final class Editor implements BeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            return new PropertySheetDialogBuilder()
                    .title("Edit out of sample diagnostics")
                    .editNode(new SaOutOfSampleDiagnosticsBuddy.OutOfSampleDiagnosticsNode<>((OutOfSampleDiagnosticsConfiguration) bean));
        }
    }

    private static BeanConfigurator<OutOfSampleDiagnosticsConfiguration, SaOutOfSampleDiagnosticsBuddy> createConfigurator() {
        return new BeanConfigurator<>(new Handler(),
                new SaOutOfSampleDiagnosticsBuddy.SaOutOfSampleDiagnosticsConverter(),
                new Editor()
        );
    }
}
