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
import ec.tss.sa.diagnostics.ResidualSeasonalityDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.ResidualSeasonalityDiagnosticsFactory;
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
public final class ResidualSeasonalityDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements IConfigurable, IResetable {

    private static final String NAME = "ResidualSeasonalityDiagnostics";

    private final Configurator<ResidualSeasonalityDiagnosticsFactory> configurator = createConfigurator();

    @Override
    public String getName() {
        return NAME;
    }

    @Messages("residualSeasonalityDiagnostics.display=Residual seasonality")
    @Override
    public String getDisplayName() {
        return Bundle.residualSeasonalityDiagnostics_display();
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
        lookup().setProperties(new ResidualSeasonalityDiagnosticsConfiguration());
    }

    @Override
    public Sheet createSheet() {
        return createSheet(lookup().getConfiguration());
    }

    private ResidualSeasonalityDiagnosticsFactory lookup() {
        return Lookup.getDefault().lookup(ResidualSeasonalityDiagnosticsFactory.class);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @Messages({
        "residualSeasonalityDiagnostics.enabled.display=Enabled",
        "residualSeasonalityDiagnostics.enabled.description=Enable or not the diagnostic",
        "residualSeasonalityDiagnostics.severe.display=Severe",
        "residualSeasonalityDiagnostics.severe.description=Severe",
        "residualSeasonalityDiagnostics.bad.display=Bad",
        "residualSeasonalityDiagnostics.bad.description=Bad",
        "residualSeasonalityDiagnostics.uncertain.display=Uncertain",
        "residualSeasonalityDiagnostics.uncertain.description=Uncertain",
        "residualSeasonalityDiagnostics.appearance.display=Appearance",
        "residualSeasonalityDiagnostics.saCategory.display=SA series (complete)",
        "residualSeasonalityDiagnostics.irrCategory.display=Irregular (complete)",
        "residualSeasonalityDiagnostics.sa3Category.display=SA series (last 3 years)"
    })
    private static Sheet createSheet(ResidualSeasonalityDiagnosticsConfiguration config) {
        Sheet sheet = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("appearance").display(Bundle.residualSeasonalityDiagnostics_appearance_display());
        b.withBoolean()
                .select(config, "enabled")
                .display(Bundle.residualSeasonalityDiagnostics_enabled_display())
                .description(Bundle.residualSeasonalityDiagnostics_enabled_description())
                .add();
        sheet.put(b.build());

        b.reset("saCat").display(Bundle.residualSeasonalityDiagnostics_saCategory_display());
        b.withDouble()
                .select(config, "SASevere")
                .display(Bundle.residualSeasonalityDiagnostics_severe_display())
                .description(Bundle.residualSeasonalityDiagnostics_severe_description())
                .add();
        b.withDouble()
                .select(config, "SABad")
                .display(Bundle.residualSeasonalityDiagnostics_bad_display())
                .description(Bundle.residualSeasonalityDiagnostics_bad_description())
                .add();
        b.withDouble()
                .select(config, "SAUncertain")
                .display(Bundle.residualSeasonalityDiagnostics_uncertain_display())
                .description(Bundle.residualSeasonalityDiagnostics_uncertain_description())
                .max(1.0)
                .add();
        sheet.put(b.build());
        
        b.reset("irrCat").display(Bundle.residualSeasonalityDiagnostics_irrCategory_display());
        b.withDouble()
                .select(config, "irrSevere")
                .display(Bundle.residualSeasonalityDiagnostics_severe_display())
                .description(Bundle.residualSeasonalityDiagnostics_severe_description())
                .add();
        b.withDouble()
                .select(config, "irrBad")
                .display(Bundle.residualSeasonalityDiagnostics_bad_display())
                .description(Bundle.residualSeasonalityDiagnostics_bad_description())
                .add();
        b.withDouble()
                .select(config, "irrUncertain")
                .display(Bundle.residualSeasonalityDiagnostics_uncertain_display())
                .description(Bundle.residualSeasonalityDiagnostics_uncertain_description())
                .max(1.0)
                .add();
        sheet.put(b.build());
        
        b.reset("sa3Cat").display(Bundle.residualSeasonalityDiagnostics_sa3Category_display());
        b.withDouble()
                .select(config, "SA3Severe")
                .display(Bundle.residualSeasonalityDiagnostics_severe_display())
                .description(Bundle.residualSeasonalityDiagnostics_severe_description())
                .add();
        b.withDouble()
                .select(config, "SA3Bad")
                .display(Bundle.residualSeasonalityDiagnostics_bad_display())
                .description(Bundle.residualSeasonalityDiagnostics_bad_description())
                .add();
        b.withDouble()
                .select(config, "SA3Uncertain")
                .display(Bundle.residualSeasonalityDiagnostics_uncertain_display())
                .description(Bundle.residualSeasonalityDiagnostics_uncertain_description())
                .max(1.0)
                .add();
        sheet.put(b.build());

        return sheet;
    }

    private static Image getIcon() {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    private static Configurator<ResidualSeasonalityDiagnosticsFactory> createConfigurator() {
        return new ConfigHandler().toConfigurator(new ConfigConverter(), new ConfigEditor());

    }

    private static final class ConfigHandler extends BeanHandler<ResidualSeasonalityDiagnosticsConfiguration, ResidualSeasonalityDiagnosticsFactory> {

        @Override
        public ResidualSeasonalityDiagnosticsConfiguration loadBean(ResidualSeasonalityDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(ResidualSeasonalityDiagnosticsFactory resource, ResidualSeasonalityDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter extends Converter<ResidualSeasonalityDiagnosticsConfiguration, Config> {

        private final IParam<Config, Boolean> enabledParam = Params.onBoolean(true, "enabled");
        private final IParam<Config, Double> saSevereParam = Params.onDouble(.01, "sasevere");
        private final IParam<Config, Double> saBadParam = Params.onDouble(.05, "saBad");
        private final IParam<Config, Double> saUncertainParam = Params.onDouble(.1, "saUncertain");
        private final IParam<Config, Double> irrSevereParam = Params.onDouble(.01, "irrSevere");
        private final IParam<Config, Double> irrBadParam = Params.onDouble(.05, "irrBad");
        private final IParam<Config, Double> irrUncertainParam = Params.onDouble(.1, "irrUncertain");
        private final IParam<Config, Double> sa3SevereParam = Params.onDouble(.01, "sa3Severe");
        private final IParam<Config, Double> sa3BadParam = Params.onDouble(.05, "sa3Bad");
        private final IParam<Config, Double> sa3UncertainParam = Params.onDouble(.1, "sa3Uncertain");

        @Override
        protected Config doForward(ResidualSeasonalityDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            enabledParam.set(result, a.isEnabled());
            saSevereParam.set(result, a.getSASevere());
            saBadParam.set(result, a.getSABad());
            saUncertainParam.set(result, a.getSAUncertain());
            irrSevereParam.set(result, a.getIrrSevere());
            irrBadParam.set(result, a.getIrrBad());
            irrUncertainParam.set(result, a.getIrrUncertain());
            sa3SevereParam.set(result, a.getSA3Severe());
            sa3BadParam.set(result, a.getSA3Bad());
            sa3UncertainParam.set(result, a.getSA3Uncertain());
            return result.build();
        }

        @Override
        protected ResidualSeasonalityDiagnosticsConfiguration doBackward(Config b) {
            ResidualSeasonalityDiagnosticsConfiguration result = new ResidualSeasonalityDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b));
            result.setSASevere(saSevereParam.get(b));
            result.setSABad(saBadParam.get(b));
            result.setSAUncertain(saUncertainParam.get(b));
            result.setIrrSevere(irrSevereParam.get(b));
            result.setIrrBad(irrBadParam.get(b));
            result.setIrrUncertain(irrUncertainParam.get(b));
            result.setSA3Severe(sa3SevereParam.get(b));
            result.setSA3Bad(sa3BadParam.get(b));
            result.setSA3Uncertain(sa3UncertainParam.get(b));
            return result;
        }
    }

    private static final class ConfigEditor implements IBeanEditor {

        @Messages({"residualSeasonalityDiagnostics.edit.title=Edit Residual seasonality",
            "residualSeasonalityDiagnostics.edit.errorTitle=Invalid Input",
            "residualSeasonalityDiagnostics.edit.errorMessage=\nWould you like to modify your choice?"
        })
        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = createSheet((ResidualSeasonalityDiagnosticsConfiguration) bean);
            String title = Bundle.residualSeasonalityDiagnostics_edit_title();
            while (true) {
                if (!new PropertySheetDialogBuilder().title(title).icon(getIcon()).editSheet(sheet)) {
                    return false;
                }
                try {
                    ((ResidualSeasonalityDiagnosticsConfiguration)bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.residualSeasonalityDiagnostics_edit_errorMessage();
                    if(JOptionPane.showConfirmDialog(null, message , Bundle.residualSeasonalityDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION){
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
