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
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.nbdemetra.ui.sa.SaDiagnosticsFactoryBuddy;
import ec.tss.sa.diagnostics.ResidualsDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.ResidualsDiagnosticsFactory;
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
public final class ResidualsDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements IConfigurable {

    private static final String NAME = "ResidualsDiagnostics";

    private final Configurator<ResidualsDiagnosticsFactory> configurator = createConfigurator();

    @Override
    public String getName() {
        return NAME;
    }

    @Messages("residualsDiagnostics.display=Regarima residuals")
    @Override
    public String getDisplayName() {
        return Bundle.residualsDiagnostics_display();
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
    public Sheet createSheet() {
        return createSheet(lookup().getConfiguration());
    }

    private ResidualsDiagnosticsFactory lookup() {
        return Lookup.getDefault().lookup(ResidualsDiagnosticsFactory.class);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @Messages({
        "residualsDiagnostics.enabled.display=Enabled",
        "residualsDiagnostics.enabled.description=Enable or not the diagnostic",
        "residualsDiagnostics.severe.display=Severe",
        "residualsDiagnostics.severe.description=Severe",
        "residualsDiagnostics.bad.display=Bad",
        "residualsDiagnostics.bad.description=Bad",
        "residualsDiagnostics.uncertain.display=Uncertain",
        "residualsDiagnostics.uncertain.description=Uncertain",
        "residualsDiagnostics.appearance.display=Appearance",
        "residualsDiagnostics.niddTestCategory.display=Relative number of residuals",
        "residualsDiagnostics.specTDTestCategory.display=Spectral test on trading days frequencies",
        "residualsDiagnostics.specSeasTestCategory.display=Spectral test on seasonal frequencies"
    })
    private static Sheet createSheet(ResidualsDiagnosticsConfiguration config) {
        Sheet sheet = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("appearance").display(Bundle.residualsDiagnostics_appearance_display());
        b.withBoolean()
                .select(config, "enabled")
                .display(Bundle.residualsDiagnostics_enabled_display())
                .description(Bundle.residualsDiagnostics_enabled_description())
                .add();
        sheet.put(b.build());

        b.reset("niddTestCat").display(Bundle.residualsDiagnostics_niddTestCategory_display());
        b.withDouble()
                .select(config, "nIIDUncertain")
                .display(Bundle.residualsDiagnostics_uncertain_display())
                .description(Bundle.residualsDiagnostics_uncertain_description())
                .add();
        
        b.withDouble()
                .select(config, "nIIDBad")
                .display(Bundle.residualsDiagnostics_bad_display())
                .description(Bundle.residualsDiagnostics_bad_description())
                .add();
        sheet.put(b.build());
        
        b.reset("specTDTestCat").display(Bundle.residualsDiagnostics_specTDTestCategory_display());
        b.withDouble()
                .select(config, "specTDUncertain")
                .display(Bundle.residualsDiagnostics_uncertain_display())
                .description(Bundle.residualsDiagnostics_uncertain_description())
                .add();
        
        b.withDouble()
                .select(config, "specTDBad")
                .display(Bundle.residualsDiagnostics_bad_display())
                .description(Bundle.residualsDiagnostics_bad_description())
                .add();
        b.withDouble()
                .select(config, "specTDSevere")
                .display(Bundle.residualsDiagnostics_severe_display())
                .description(Bundle.residualsDiagnostics_severe_description())
                .add();
        sheet.put(b.build());
        
        b.reset("specSeasTestCat").display(Bundle.residualsDiagnostics_specSeasTestCategory_display());
        b.withDouble()
                .select(config, "specSeasUncertain")
                .display(Bundle.residualsDiagnostics_uncertain_display())
                .description(Bundle.residualsDiagnostics_uncertain_description())
                .add();
        
        b.withDouble()
                .select(config, "specSeasBad")
                .display(Bundle.residualsDiagnostics_bad_display())
                .description(Bundle.residualsDiagnostics_bad_description())
                .add();
        b.withDouble()
                .select(config, "specSeasSevere")
                .display(Bundle.residualsDiagnostics_severe_display())
                .description(Bundle.residualsDiagnostics_severe_description())
                .add();
        sheet.put(b.build());

        return sheet;
    }

    private static Image getIcon() {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    private static Configurator<ResidualsDiagnosticsFactory> createConfigurator() {
        return new ConfigHandler().toConfigurator(new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler extends BeanHandler<ResidualsDiagnosticsConfiguration, ResidualsDiagnosticsFactory> {

        @Override
        public ResidualsDiagnosticsConfiguration loadBean(ResidualsDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(ResidualsDiagnosticsFactory resource, ResidualsDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter extends Converter<ResidualsDiagnosticsConfiguration, Config> {

        private final IParam<Config, Boolean> enabledParam = Params.onBoolean(true, "enabled");
        private final IParam<Config, Double> normalityBadParam = Params.onDouble(.01, "normalityBad");
        private final IParam<Config, Double> normalityUncertainParam = Params.onDouble(.1, "normalityUncertain");
        private final IParam<Config, Double> specTDSevereParam = Params.onDouble(.001, "specTDSevereP");
        private final IParam<Config, Double> specTDBadParam = Params.onDouble(.01, "specTDBad");
        private final IParam<Config, Double> specTDUncertainParam = Params.onDouble(.1, "specTDUncertain");
        private final IParam<Config, Double> specSeasSevereParam = Params.onDouble(.001, "specSeasSevere");
        private final IParam<Config, Double> specSeasBadParam = Params.onDouble(.01, "specSeasBad");
        private final IParam<Config, Double> specSeasUncertainParam = Params.onDouble(.1, "specSeasUncertain");

        @Override
        protected Config doForward(ResidualsDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            enabledParam.set(result, a.isEnabled());
            normalityBadParam.set(result, a.getNIIDBad());
            normalityUncertainParam.set(result, a.getNIIDUncertain());
            specTDSevereParam.set(result, a.getSpecTDSevere());
            specTDBadParam.set(result, a.getSpecTDBad());
            specTDUncertainParam.set(result, a.getSpecTDUncertain());
            specSeasSevereParam.set(result, a.getSpecSeasSevere());
            specSeasBadParam.set(result, a.getSpecSeasBad());
            specSeasUncertainParam.set(result, a.getSpecSeasUncertain());
            return result.build();
        }

        @Override
        protected ResidualsDiagnosticsConfiguration doBackward(Config b) {
            ResidualsDiagnosticsConfiguration result = new ResidualsDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b));
            result.setNIIDBad(normalityBadParam.get(b));
            result.setNIIDUncertain(normalityUncertainParam.get(b));
            result.setSpecTDSevere(specTDSevereParam.get(b));
            result.setSpecTDBad(specTDBadParam.get(b));
            result.setSpecTDUncertain(specTDUncertainParam.get(b));
            result.setSpecSeasSevere(specSeasSevereParam.get(b));
            result.setSpecSeasBad(specSeasBadParam.get(b));
            result.setSpecSeasUncertain(specSeasUncertainParam.get(b));
            return result;
        }
    }

    private static final class ConfigEditor implements IBeanEditor {

        @Messages({"residualsDiagnostics.edit.title=Edit Regarima residuals",
            "residualsDiagnostics.edit.errorTitle=Invalid Input",
            "residualsDiagnostics.edit.errorMessage=\nWould you like to modify your choice?"
                })
        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = createSheet((ResidualsDiagnosticsConfiguration) bean);
            String title = Bundle.residualsDiagnostics_edit_title();
            while (true) {
                if (!OpenIdePropertySheetBeanEditor.editSheet(sheet, title, getIcon())) {
                    return false;
                }
                try {
                    ((ResidualsDiagnosticsConfiguration)bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.residualsDiagnostics_edit_errorMessage();
                    if(JOptionPane.showConfirmDialog(null, message , Bundle.residualsDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION){
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
