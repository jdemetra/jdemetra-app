/*
 * Copyright 2015 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.nbdemetra.ui.sa.diagnostics;

import demetra.desktop.ConfigEditor;
import demetra.desktop.beans.BeanHandler;
import demetra.desktop.Config;
import demetra.desktop.DemetraIcons;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.desktop.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.sa.SaDiagnosticsFactoryBuddy;
import ec.tss.sa.diagnostics.AdvancedResidualSeasonalityDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.AdvancedResidualSeasonalityDiagnosticsFactory;
import ec.tstoolkit.BaseException;
import java.awt.Image;
import java.beans.IntrospectionException;
import javax.swing.JOptionPane;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import demetra.desktop.actions.Resetable;
import nbbrd.io.text.BooleanProperty;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.IntProperty;
import nbbrd.io.text.Parser;
import nbbrd.io.text.Property;
import demetra.desktop.beans.BeanEditor;
import demetra.desktop.Converter;
import demetra.desktop.Persistable;
import demetra.desktop.actions.Configurable;
import demetra.desktop.beans.BeanConfigurator;
import nbbrd.service.ServiceProvider;

/**
 *
 * @author Laurent Jadoul
 */
@ServiceProvider(SaDiagnosticsFactoryBuddy.class)
public final class AdvancedResidualSeasonalityDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements Configurable, Persistable, ConfigEditor, Resetable {

    private static final String NAME = "AdvancedResidualSeasonalityDiagnostics";

    private final BeanConfigurator<AdvancedResidualSeasonalityDiagnosticsConfiguration, AdvancedResidualSeasonalityDiagnosticsFactory> configurator = createConfigurator();

    @Override
    public String getName() {
        return NAME;
    }

    @Messages("advancedResidualSeasonalityDiagnostics.display=Advanced residual seasonality ")
    @Override
    public String getDisplayName() {
        return Bundle.advancedResidualSeasonalityDiagnostics_display();
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return getIcon();
    }

    @Override
    public Config getConfig() {
        return configurator.getConfig(lookup());
    }

    @Override
    public void setConfig(Config config) throws IllegalArgumentException {
        configurator.setConfig(lookup(), config);
    }

    @Override
    public Config editConfig(Config config) throws IllegalArgumentException {
        return configurator.editConfig(config);
    }

    @Override
    public void configure() {
        Configurable.configure(this, this);
    }

    @Override
    public void reset() {
        lookup().setProperties(new AdvancedResidualSeasonalityDiagnosticsConfiguration());
    }

    @Override
    public Sheet createSheet() {
        return createSheet(lookup().getConfiguration());
    }

    private AdvancedResidualSeasonalityDiagnosticsFactory lookup() {
        return Lookup.getDefault().lookup(AdvancedResidualSeasonalityDiagnosticsFactory.class);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @Messages({
        "advancedResidualSeasonalityDiagnostics.enabled.display=Enabled",
        "advancedResidualSeasonalityDiagnostics.enabled.description=Enable or not the diagnostic",
        "advancedResidualSeasonalityDiagnostics.ftest.display=F-Test",
        "advancedResidualSeasonalityDiagnostics.ftest.description=F-Test on seasonal dummies",
        "advancedResidualSeasonalityDiagnostics.qstest.display=QS-Test",
        "advancedResidualSeasonalityDiagnostics.qstest.description=QS-Test (Ljung-Box o, seasonal lags)",
        "advancedResidualSeasonalityDiagnostics.ftestlastyears.display=Span for F-Test",
        "advancedResidualSeasonalityDiagnostics.ftestlastyears.description=Span (in years) for F-Test on seasonal dummies",
        "advancedResidualSeasonalityDiagnostics.qstestlastyears.display=Span for QS-Test",
        "advancedResidualSeasonalityDiagnostics.qstestlastyears.description=Span (in years) for QS-Test (Ljung-Box o, seasonal lags)",
        "advancedResidualSeasonalityDiagnostics.severe.display=Severe",
        "advancedResidualSeasonalityDiagnostics.severe.description=Severe",
        "advancedResidualSeasonalityDiagnostics.bad.display=Bad",
        "advancedResidualSeasonalityDiagnostics.bad.description=Bad",
        "advancedResidualSeasonalityDiagnostics.uncertain.display=Uncertain",
        "advancedResidualSeasonalityDiagnostics.uncertain.description=Uncertain",
        "advancedResidualSeasonalityDiagnostics.test.display=Test",
        "advancedResidualSeasonalityDiagnostics.thresholds.display=Thresholds",
        "advancedResidualSeasonalityDiagnostics.span.display=Time span",})
    private static Sheet createSheet(AdvancedResidualSeasonalityDiagnosticsConfiguration config) {
        Sheet sheet = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("test").display(Bundle.advancedResidualSeasonalityDiagnostics_test_display());
        b.withBoolean()
                .select(config, "enabled")
                .display(Bundle.advancedResidualSeasonalityDiagnostics_enabled_display())
                .description(Bundle.advancedResidualSeasonalityDiagnostics_enabled_description())
                .add();
        b.withBoolean()
                .select(config, "fTest")
                .display(Bundle.advancedResidualSeasonalityDiagnostics_ftest_display())
                .description(Bundle.advancedResidualSeasonalityDiagnostics_ftest_description())
                .add();
        b.withBoolean()
                .select(config, "qsTest")
                .display(Bundle.advancedResidualSeasonalityDiagnostics_qstest_display())
                .description(Bundle.advancedResidualSeasonalityDiagnostics_qstest_description())
                .add();
        sheet.put(b.build());

        b.reset("span").display(Bundle.advancedResidualSeasonalityDiagnostics_span_display());
        b.withInt()
                .select(config, "fTestLastYears")
                .display(Bundle.advancedResidualSeasonalityDiagnostics_ftestlastyears_display())
                .description(Bundle.advancedResidualSeasonalityDiagnostics_ftestlastyears_description())
                .add();
        b.withInt()
                .select(config, "qsTestLastYears")
                .display(Bundle.advancedResidualSeasonalityDiagnostics_qstestlastyears_display())
                .description(Bundle.advancedResidualSeasonalityDiagnostics_qstestlastyears_description())
                .add();
        sheet.put(b.build());

        b.reset("thresholds").display(Bundle.advancedResidualSeasonalityDiagnostics_thresholds_display());
        b.withDouble()
                .select(config, "uncertainThreshold")
                .display(Bundle.advancedResidualSeasonalityDiagnostics_uncertain_display())
                .description(Bundle.advancedResidualSeasonalityDiagnostics_uncertain_description())
                .add();

        b.withDouble()
                .select(config, "badThreshold")
                .display(Bundle.advancedResidualSeasonalityDiagnostics_bad_display())
                .description(Bundle.advancedResidualSeasonalityDiagnostics_bad_description())
                .add();

        b.withDouble()
                .select(config, "severeThreshold")
                .display(Bundle.advancedResidualSeasonalityDiagnostics_severe_display())
                .description(Bundle.advancedResidualSeasonalityDiagnostics_severe_description())
                .add();
        sheet.put(b.build());

        return sheet;
    }

    private static Image getIcon() {
        return ImageUtilities.icon2Image(DemetraIcons.PUZZLE_16);
    }

    private static BeanConfigurator<AdvancedResidualSeasonalityDiagnosticsConfiguration, AdvancedResidualSeasonalityDiagnosticsFactory> createConfigurator() {
        return new BeanConfigurator<>(new ConfigHandler(), new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler implements BeanHandler<AdvancedResidualSeasonalityDiagnosticsConfiguration, AdvancedResidualSeasonalityDiagnosticsFactory> {

        @Override
        public AdvancedResidualSeasonalityDiagnosticsConfiguration loadBean(AdvancedResidualSeasonalityDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(AdvancedResidualSeasonalityDiagnosticsFactory resource, AdvancedResidualSeasonalityDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter implements Converter<AdvancedResidualSeasonalityDiagnosticsConfiguration, Config> {

        private final BooleanProperty enabledParam = BooleanProperty.of("enabled", true);
        private final BooleanProperty qsTestParam = BooleanProperty.of("QsTest", true);
        private final BooleanProperty fTestParam = BooleanProperty.of("FTest", true);
        private final IntProperty qsTestLastYearsParam = IntProperty.of("QsTestLastYears", 0);
        private final IntProperty fTestLastYearsParam = IntProperty.of("FTestLastYears", 8);
        private final Property<Double> severeParam = Property.of("specSeasSevere", .001, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> badParam = Property.of("specSeasBad", .01, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> uncertainParam = Property.of("specSeasUncertain", .1, Parser.onDouble(), Formatter.onDouble());

        @Override
        public Config doForward(AdvancedResidualSeasonalityDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20170209");
            enabledParam.set(result::parameter, a.isEnabled());
            qsTestParam.set(result::parameter, a.isQsTest());
            fTestParam.set(result::parameter, a.isFTest());
            qsTestLastYearsParam.set(result::parameter, a.getQsTestLastYears());
            fTestLastYearsParam.set(result::parameter, a.getFTestLastYears());
            badParam.set(result::parameter, a.getBadThreshold());
            uncertainParam.set(result::parameter, a.getUncertainThreshold());
            severeParam.set(result::parameter, a.getSevereThreshold());
            return result.build();
        }

        @Override
        public AdvancedResidualSeasonalityDiagnosticsConfiguration doBackward(Config b) {
            AdvancedResidualSeasonalityDiagnosticsConfiguration result = new AdvancedResidualSeasonalityDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b::getParameter));
            result.setFTest(fTestParam.get(b::getParameter));
            result.setQsTest(qsTestParam.get(b::getParameter));
            result.setFTestLastYears(fTestLastYearsParam.get(b::getParameter));
            result.setQsTestLastYears(qsTestLastYearsParam.get(b::getParameter));
            result.setSevereThreshold(severeParam.get(b::getParameter));
            result.setBadThreshold(badParam.get(b::getParameter));
            result.setUncertainThreshold(uncertainParam.get(b::getParameter));
            return result;
        }
    }

    private static final class ConfigEditor implements BeanEditor {

        @Messages({"advancedResidualSeasonalityDiagnostics.edit.title=Edit advanced residuals diagnostics",
            "advancedResidualSeasonalityDiagnostics.edit.errorTitle=Invalid Input",
            "advancedResidualSeasonalityDiagnostics.edit.errorMessage=\nWould you like to modify your choice?"
        })
        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = createSheet((AdvancedResidualSeasonalityDiagnosticsConfiguration) bean);
            String title = Bundle.advancedResidualSeasonalityDiagnostics_edit_title();
            while (true) {
                if (!new PropertySheetDialogBuilder().title(title).icon(getIcon()).editSheet(sheet)) {
                    return false;
                }
                try {
                    ((AdvancedResidualSeasonalityDiagnosticsConfiguration) bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.advancedResidualSeasonalityDiagnostics_edit_errorMessage();
                    if (JOptionPane.showConfirmDialog(null, message, Bundle.advancedResidualSeasonalityDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
