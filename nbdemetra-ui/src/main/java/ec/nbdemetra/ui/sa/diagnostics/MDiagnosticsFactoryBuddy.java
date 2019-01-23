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
import demetra.ui.properties.PropertySheetDialogBuilder;
import demetra.ui.properties.IBeanEditor;
import demetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.sa.SaDiagnosticsFactoryBuddy;
import ec.tss.sa.diagnostics.MDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.MDiagnosticsFactory;
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
public final class MDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements IConfigurable, Resetable {

    private static final String NAME = "MDiagnostics";

    private final Configurator<MDiagnosticsFactory> configurator = createConfigurator();

    @Override
    public String getName() {
        return NAME;
    }

    @Messages("mDiagnostics.display=M-Statistics")
    @Override
    public String getDisplayName() {
        return Bundle.mDiagnostics_display();
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
        lookup().setProperties(new MDiagnosticsConfiguration());
    }
    
    @Override
    public Sheet createSheet() {
        return createSheet(lookup().getConfiguration());
    }

    private MDiagnosticsFactory lookup() {
        return Lookup.getDefault().lookup(MDiagnosticsFactory.class);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @Messages({
        "mDiagnostics.enabled.display=Enabled",
        "mDiagnostics.enabled.description=Enable or not the diagnostic",
        "mDiagnostics.severe.display=Severe",
        "mDiagnostics.severe.description=Severe",
        "mDiagnostics.bad.display=Bad",
        "mDiagnostics.bad.description=Bad",
        "mDiagnostics.all.display=All",
        "mDiagnostics.all.description=Use all M-statistics",
        "mDiagnostics.Thresholds.display=Thresholds"
    })
    private static Sheet createSheet(MDiagnosticsConfiguration config) {
        Sheet sheet = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.withBoolean()
                .select(config, "enabled")
                .display(Bundle.mDiagnostics_enabled_display())
                .description(Bundle.mDiagnostics_enabled_description())
                .add();
        b.withBoolean()
                .select(config, "useAll")
                .display(Bundle.mDiagnostics_all_display())
                .description(Bundle.mDiagnostics_all_description())
                .add();
        sheet.put(b.build());

        b.reset("thresholds").display(Bundle.mDiagnostics_Thresholds_display());
        b.withDouble()
                .select(config, "severe")
                .display(Bundle.mDiagnostics_severe_display())
                .description(Bundle.mDiagnostics_severe_description())
                .max(3.0)
                .add();
        b.withDouble()
                .select(config, "bad")
                .display(Bundle.mDiagnostics_bad_display())
                .description(Bundle.mDiagnostics_bad_description())
                .min(0.0)
                .add();
        sheet.put(b.build());

        return sheet;
    }

    private static Image getIcon() {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    private static Configurator<MDiagnosticsFactory> createConfigurator() {
        return new ConfigHandler().toConfigurator(new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler extends BeanHandler<MDiagnosticsConfiguration, MDiagnosticsFactory> {

        @Override
        public MDiagnosticsConfiguration loadBean(MDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(MDiagnosticsFactory resource, MDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter extends Converter<MDiagnosticsConfiguration, Config> {

        private final IParam<Config, Boolean> enabledParam = Params.onBoolean(true, "enabled");
        private final IParam<Config, Double> severeParam = Params.onDouble(2d, "severe");
        private final IParam<Config, Double> badParam = Params.onDouble(1d, "bad");
        private final IParam<Config, Boolean> allParam = Params.onBoolean(true, "all");

        @Override
        protected Config doForward(MDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            enabledParam.set(result, a.isEnabled());
            severeParam.set(result, a.getSevere());
            badParam.set(result, a.getBad());
            allParam.set(result, a.isUseAll());
            return result.build();
        }

        @Override
        protected MDiagnosticsConfiguration doBackward(Config b) {
            MDiagnosticsConfiguration result = new MDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b));
            result.setSevere(severeParam.get(b));
            result.setBad(badParam.get(b));
            result.setUseAll(allParam.get(b));
            return result;
        }
    }

    private static final class ConfigEditor implements IBeanEditor {

        @Messages({"mDiagnostics.edit.title=Edit M-Statistics",
            "mDiagnostics.edit.errorTitle=Invalid Input",
            "mDiagnostics.edit.errorMessage=\nWould you like to modify your choice?"
        })
        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = createSheet((MDiagnosticsConfiguration) bean);
            String title = Bundle.mDiagnostics_edit_title();
            while (true) {
                if (!new PropertySheetDialogBuilder().title(title).icon(getIcon()).editSheet(sheet)) {
                    return false;
                }
                try {
                    ((MDiagnosticsConfiguration)bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.mDiagnostics_edit_errorMessage();
                    if(JOptionPane.showConfirmDialog(null, message , Bundle.mDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION){
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
