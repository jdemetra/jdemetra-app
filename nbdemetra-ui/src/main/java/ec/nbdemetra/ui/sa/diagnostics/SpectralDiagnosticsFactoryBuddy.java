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
import ec.nbdemetra.ui.properties.PropertySheetDialogBuilder;
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.sa.SaDiagnosticsFactoryBuddy;
import ec.tss.sa.diagnostics.SpectralDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.SpectralDiagnosticsFactory;
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
import demetra.ui.actions.Resetable;

/**
 *
 * @author Laurent Jadoul
 */
@ServiceProvider(service = SaDiagnosticsFactoryBuddy.class)
public final class SpectralDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements IConfigurable, Resetable {

    private static final String NAME = "SpectralDiagnostics";

    private final Configurator<SpectralDiagnosticsFactory> configurator = createConfigurator();

    @Override
    public String getName() {
        return NAME;
    }

    @Messages("spectralDiagnostics.display=Visual spectral analysis")
    @Override
    public String getDisplayName() {
        return Bundle.spectralDiagnostics_display();
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
        lookup().setProperties(new SpectralDiagnosticsConfiguration());
    }
    
    @Override
    public Sheet createSheet() {
        return createSheet(lookup().getConfiguration());
    }

    private SpectralDiagnosticsFactory lookup() {
        return Lookup.getDefault().lookup(SpectralDiagnosticsFactory.class);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @Messages({
        "spectralDiagnostics.enabled.display=Enabled",
        "spectralDiagnostics.enabled.description=Enable or not the diagnostic",
        "spectralDiagnostics.strict.display=Strict",
        "spectralDiagnostics.strict.description=Control that spectral peaks appear on both SA and irregular series. If strict is true, a severe diagnostic is generated when only one series contains a peak. Otherwise, both series must contain a peak.",
        "spectralDiagnostics.sens.display=Sensitivity",
        "spectralDiagnostics.sens.description=Threshold for the identification of peaks (see X12 documentation; default = 6/52).",
        "spectralDiagnostics.length.display=Length",
        "spectralDiagnostics.length.description=Number of years considered in the spectral analysis (end of the series). 0 for the complete series.",
        "spectralDiagnostics.appearanceCategory.display=Appearance",
        "spectralDiagnostics.spectralCategory.display=Spectral test"
    })
    private static Sheet createSheet(SpectralDiagnosticsConfiguration config) {
        Sheet sheet = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("appearanceCategory").display(Bundle.spectralDiagnostics_appearanceCategory_display());
        b.withBoolean()
                .select(config, "enabled")
                .display(Bundle.spectralDiagnostics_enabled_display())
                .description(Bundle.spectralDiagnostics_enabled_description())
                .add();
        sheet.put(b.build());

        b.reset("spectralCategory").display(Bundle.spectralDiagnostics_spectralCategory_display());
        b.withDouble()
                .select(config, "sensitivity")
                .display(Bundle.spectralDiagnostics_sens_display())
                .description(Bundle.spectralDiagnostics_sens_description())
                .min(3.0 / 52)
                .add();
        b.withInt()
                .select(config, "length")
                .display(Bundle.spectralDiagnostics_length_display())
                .description(Bundle.spectralDiagnostics_length_description())
                .add();
        b.withBoolean()
                .select(config, "strict")
                .display(Bundle.spectralDiagnostics_strict_display())
                .description(Bundle.spectralDiagnostics_strict_description())
                .add();
        sheet.put(b.build());

        return sheet;
    }

    private static Image getIcon() {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    private static Configurator<SpectralDiagnosticsFactory> createConfigurator() {
        return new ConfigHandler().toConfigurator(new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler extends BeanHandler<SpectralDiagnosticsConfiguration, SpectralDiagnosticsFactory> {

        @Override
        public SpectralDiagnosticsConfiguration loadBean(SpectralDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(SpectralDiagnosticsFactory resource, SpectralDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter extends Converter<SpectralDiagnosticsConfiguration, Config> {

        private final IParam<Config, Boolean> enabledParam = Params.onBoolean(false, "enabled");
        private final IParam<Config, Boolean> strictParam = Params.onBoolean(true, "strict");
        private final IParam<Config, Double> sensParam = Params.onDouble(6.0 / 52, "sensitivity");
        private final IParam<Config, Integer> lengthParam = Params.onInteger(8, "length");

        @Override
        protected Config doForward(SpectralDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            enabledParam.set(result, a.isEnabled());
            strictParam.set(result, a.isStrict());
            sensParam.set(result, a.getSensitivity());
            lengthParam.set(result, a.getLength());
            return result.build();
        }

        @Override
        protected SpectralDiagnosticsConfiguration doBackward(Config b) {
            SpectralDiagnosticsConfiguration result = new SpectralDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b));
            result.setStrict(strictParam.get(b));
            result.setSensitivity(sensParam.get(b));
            result.setLength(lengthParam.get(b));
            return result;
        }
    }

    private static final class ConfigEditor implements IBeanEditor {

        @Messages({"spectralDiagnostics.edit.title=Edit Visual spectral analysis",
            "spectralDiagnostics.edit.errorTitle=Invalid Input",
            "spectralDiagnostics.edit.errorMessage=\nWould you like to modify your choice?"
        })
        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = createSheet((SpectralDiagnosticsConfiguration) bean);
            String title = Bundle.spectralDiagnostics_edit_title();
            while (true) {
                if (!new PropertySheetDialogBuilder().title(title).icon(getIcon()).editSheet(sheet)) {
                    return false;
                }
                try {
                    ((SpectralDiagnosticsConfiguration)bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.spectralDiagnostics_edit_errorMessage();
                    if(JOptionPane.showConfirmDialog(null, message , Bundle.spectralDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION){
                        return false;
                    }
                }
            }
        }

    }
    //</editor-fold>
}
