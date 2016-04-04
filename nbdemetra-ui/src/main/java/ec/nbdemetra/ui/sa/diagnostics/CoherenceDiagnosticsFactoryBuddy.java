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
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.nbdemetra.ui.sa.SaDiagnosticsFactoryBuddy;
import ec.tss.sa.diagnostics.CoherenceDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.CoherenceDiagnosticsFactory;
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
 * @author Philippe Charles
 */
@ServiceProvider(service = SaDiagnosticsFactoryBuddy.class)
public final class CoherenceDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements IConfigurable, IResetable {

    private static final String NAME = "CoherenceDiagnostics";

    private final Configurator<CoherenceDiagnosticsFactory> configurator = createConfigurator();

    @Override
    public String getName() {
        return NAME;
    }

    @Messages("coherenceDiagnostics.display=Basic checks")
    @Override
    public String getDisplayName() {
        return Bundle.coherenceDiagnostics_display();
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
        lookup().setProperties(new CoherenceDiagnosticsConfiguration());
    }

    @Override
    public Sheet createSheet() {
        return createSheet(lookup().getConfiguration());
    }

    private CoherenceDiagnosticsFactory lookup() {
        return Lookup.getDefault().lookup(CoherenceDiagnosticsFactory.class);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @Messages({
        "coherenceDiagnostics.enabled.display=Enabled",
        "coherenceDiagnostics.enabled.description=Enable or not the diagnostic",
        "coherenceDiagnostics.tolerance.display=Tolerance",
        "coherenceDiagnostics.tolerance.description=Tolerance",
        "coherenceDiagnostics.error.display=Error",
        "coherenceDiagnostics.error.description=Error",
        "coherenceDiagnostics.severe.display=Severe",
        "coherenceDiagnostics.severe.description=Severe",
        "coherenceDiagnostics.bad.display=Bad",
        "coherenceDiagnostics.bad.description=Bad",
        "coherenceDiagnostics.uncertain.display=Uncertain",
        "coherenceDiagnostics.uncertain.description=Uncertain",
        "coherenceDiagnostics.shortSeries.display=Short Series",
        "coherenceDiagnostics.shortSeries.description=Length of series considered as short (in years)",
        "coherenceDiagnostics.annual.display=Annual totals (lower bound)",
        "coherenceDiagnostics.misc.display=Miscellaneous"
    })
    private static Sheet createSheet(CoherenceDiagnosticsConfiguration config) {
        Sheet sheet = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.withBoolean()
                .select(config, "enabled")
                .display(Bundle.coherenceDiagnostics_enabled_display())
                .description(Bundle.coherenceDiagnostics_enabled_description())
                .add();
        b.withDouble()
                .select(config, "tolerance")
                .display(Bundle.coherenceDiagnostics_tolerance_display())
                .description(Bundle.coherenceDiagnostics_tolerance_description())
                .min(0).max(0.1)
                .add();
        sheet.put(b.build());

        b.reset("annual").display(Bundle.coherenceDiagnostics_annual_display());
        b.withDouble()
                .select(config, "error")
                .display(Bundle.coherenceDiagnostics_error_display())
                .description(Bundle.coherenceDiagnostics_error_description())
                .add();
        b.withDouble()
                .select(config, "severe")
                .display(Bundle.coherenceDiagnostics_severe_display())
                .description(Bundle.coherenceDiagnostics_severe_description())
                .add();
        b.withDouble()
                .select(config, "bad")
                .display(Bundle.coherenceDiagnostics_bad_display())
                .description(Bundle.coherenceDiagnostics_bad_description())
                .add();
        b.withDouble()
                .select(config, "uncertain")
                .display(Bundle.coherenceDiagnostics_uncertain_display())
                .description(Bundle.coherenceDiagnostics_uncertain_description())
                .min(0.0)
                .add();
        sheet.put(b.build());

        b.reset("misc").display(Bundle.coherenceDiagnostics_misc_display());
        b.withInt()
                .select(config, "shortSeries")
                .display(Bundle.coherenceDiagnostics_shortSeries_display())
                .description(Bundle.coherenceDiagnostics_shortSeries_description())
                .add();
        sheet.put(b.build());

        return sheet;
    }

    private static Image getIcon() {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    private static Configurator<CoherenceDiagnosticsFactory> createConfigurator() {
        return new ConfigHandler().toConfigurator(new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler extends BeanHandler<CoherenceDiagnosticsConfiguration, CoherenceDiagnosticsFactory> {

        @Override
        public CoherenceDiagnosticsConfiguration loadBean(CoherenceDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(CoherenceDiagnosticsFactory resource, CoherenceDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter extends Converter<CoherenceDiagnosticsConfiguration, Config> {

        private final IParam<Config, Boolean> enabledParam = Params.onBoolean(true, "enabled");
        private final IParam<Config, Double> toleranceParam = Params.onDouble(1e-3, "tolerance");
        private final IParam<Config, Double> errorParam = Params.onDouble(.5, "error");
        private final IParam<Config, Double> severeParam = Params.onDouble(.1, "severe");
        private final IParam<Config, Double> badParam = Params.onDouble(.05, "bad");
        private final IParam<Config, Double> uncertainParam = Params.onDouble(.01, "uncertain");
        private final IParam<Config, Integer> shortSeriesParam = Params.onInteger(7, "shortSeries");

        @Override
        protected Config doForward(CoherenceDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            enabledParam.set(result, a.isEnabled());
            toleranceParam.set(result, a.getTolerance());
            errorParam.set(result, a.getError());
            severeParam.set(result, a.getSevere());
            badParam.set(result, a.getBad());
            uncertainParam.set(result, a.getUncertain());
            shortSeriesParam.set(result, a.getShortSeries());
            return result.build();
        }

        @Override
        protected CoherenceDiagnosticsConfiguration doBackward(Config b) {
            CoherenceDiagnosticsConfiguration result = new CoherenceDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b));
            result.setTolerance(toleranceParam.get(b));
            result.setError(errorParam.get(b));
            result.setSevere(severeParam.get(b));
            result.setBad(badParam.get(b));
            result.setUncertain(uncertainParam.get(b));
            result.setShortSeries(shortSeriesParam.get(b));
            return result;
        }
    }

    private static final class ConfigEditor implements IBeanEditor {

        @Messages({"coherenceDiagnostics.edit.title=Edit basic checks",
            "coherenceDiagnostics.edit.errorTitle=Invalid Input",
            "coherenceDiagnostics.edit.errorMessage=\nWould you like to modify your choice?"
        })
        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = createSheet((CoherenceDiagnosticsConfiguration) bean);
            String title = Bundle.coherenceDiagnostics_edit_title();
            while (true) {
                if (!OpenIdePropertySheetBeanEditor.editSheet(sheet, title, getIcon())) {
                    return false;
                }
                try {
                    ((CoherenceDiagnosticsConfiguration) bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.coherenceDiagnostics_edit_errorMessage();
                    if (JOptionPane.showConfirmDialog(null, message, Bundle.coherenceDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
