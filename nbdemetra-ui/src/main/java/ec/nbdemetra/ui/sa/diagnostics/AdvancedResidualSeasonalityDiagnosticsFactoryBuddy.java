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

import com.google.common.base.Converter;
import ec.nbdemetra.ui.BeanHandler;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.IResetable;
import ec.nbdemetra.ui.properties.PropertySheetDialogBuilder;
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.sa.SaDiagnosticsFactoryBuddy;
import ec.tss.sa.diagnostics.AdvancedResidualSeasonalityDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.AdvancedResidualSeasonalityDiagnosticsFactory;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import ec.tstoolkit.BaseException;
import java.awt.Image;
import java.beans.IntrospectionException;
import javax.swing.JOptionPane;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Laurent Jadoul
 */
@ServiceProvider(service = SaDiagnosticsFactoryBuddy.class)
public final class AdvancedResidualSeasonalityDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements IConfigurable, IResetable {

    private static final String NAME = "AdvancedResidualSeasonalityDiagnostics";

    private final Configurator<AdvancedResidualSeasonalityDiagnosticsFactory> configurator = createConfigurator();

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
        "advancedResidualSeasonalityDiagnostics.span.display=Time span",
    })
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
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    private static Configurator<AdvancedResidualSeasonalityDiagnosticsFactory> createConfigurator() {
        return new ConfigHandler().toConfigurator(new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler extends BeanHandler<AdvancedResidualSeasonalityDiagnosticsConfiguration, AdvancedResidualSeasonalityDiagnosticsFactory> {

        @Override
        public AdvancedResidualSeasonalityDiagnosticsConfiguration loadBean(AdvancedResidualSeasonalityDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(AdvancedResidualSeasonalityDiagnosticsFactory resource, AdvancedResidualSeasonalityDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter extends Converter<AdvancedResidualSeasonalityDiagnosticsConfiguration, Config> {

        private final IParam<Config, Boolean> enabledParam = Params.onBoolean(true, "enabled");
        private final IParam<Config, Boolean> qsTestParam = Params.onBoolean(true, "QsTest");
        private final IParam<Config, Boolean> fTestParam = Params.onBoolean(true, "FTest");
        private final IParam<Config, Integer> qsTestLastYearsParam = Params.onInteger(0, "QsTestLastYears");
        private final IParam<Config, Integer> fTestLastYearsParam = Params.onInteger(8, "FTestLastYears");
        private final IParam<Config, Double> severeParam = Params.onDouble(.001, "specSeasSevere");
        private final IParam<Config, Double> badParam = Params.onDouble(.01, "specSeasBad");
        private final IParam<Config, Double> uncertainParam = Params.onDouble(.1, "specSeasUncertain");

        @Override
        protected Config doForward(AdvancedResidualSeasonalityDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20170209");
            enabledParam.set(result, a.isEnabled());
            qsTestParam.set(result, a.isQsTest());
            fTestParam.set(result, a.isFTest());
            qsTestLastYearsParam.set(result, a.getQsTestLastYears());
            fTestLastYearsParam.set(result, a.getFTestLastYears());
            badParam.set(result, a.getBadThreshold());
            uncertainParam.set(result, a.getUncertainThreshold());
            severeParam.set(result, a.getSevereThreshold());
            return result.build();
        }

        @Override
        protected AdvancedResidualSeasonalityDiagnosticsConfiguration doBackward(Config b) {
            AdvancedResidualSeasonalityDiagnosticsConfiguration result = new AdvancedResidualSeasonalityDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b));
            result.setFTest(fTestParam.get(b));
            result.setQsTest(fTestParam.get(b));
            result.setFTestLastYears(fTestLastYearsParam.get(b));
            result.setQsTestLastYears(qsTestLastYearsParam.get(b));
            result.setSevereThreshold(severeParam.get(b));
            result.setBadThreshold(badParam.get(b));
            result.setUncertainThreshold(uncertainParam.get(b));
            return result;
        }
    }

    private static final class ConfigEditor implements IBeanEditor {

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
