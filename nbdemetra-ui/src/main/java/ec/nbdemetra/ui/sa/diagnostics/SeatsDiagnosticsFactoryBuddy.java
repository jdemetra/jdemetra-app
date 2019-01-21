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
import ec.tss.sa.diagnostics.SeatsDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.SeatsDiagnosticsFactory;
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
public final class SeatsDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements IConfigurable, Resetable {

    private static final String NAME = "SeatsDiagnostics";

    private final Configurator<SeatsDiagnosticsFactory> configurator = createConfigurator();

    @Override
    public String getName() {
        return NAME;
    }

    @Messages("seatsDiagnostics.display=Seats")
    @Override
    public String getDisplayName() {
        return Bundle.seatsDiagnostics_display();
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
        lookup().setProperties(new SeatsDiagnosticsConfiguration());
    }

    @Override
    public Sheet createSheet() {
        return createSheet(lookup().getConfiguration());
    }

    private SeatsDiagnosticsFactory lookup() {
        return Lookup.getDefault().lookup(SeatsDiagnosticsFactory.class);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @Messages({
        "seatsDiagnostics.enabled.display=Enabled",
        "seatsDiagnostics.enabled.description=Enable or not the diagnostic",
        "seatsDiagnostics.bad.display=Bad",
        "seatsDiagnostics.bad.description=Bad",
        "seatsDiagnostics.uncertain.display=Uncertain",
        "seatsDiagnostics.uncertain.description=Uncertain",
        "seatsDiagnostics.varCovCategory.display=Variance/covariance tests"
    })
    private static Sheet createSheet(SeatsDiagnosticsConfiguration config) {
        Sheet sheet = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.withBoolean()
                .select(config, "enabled")
                .display(Bundle.seatsDiagnostics_enabled_display())
                .description(Bundle.seatsDiagnostics_enabled_description())
                .add();
        sheet.put(b.build());

        b.reset("varCovCategory").display(Bundle.seatsDiagnostics_varCovCategory_display());
        b.withDouble()
                .select(config, "bad")
                .display(Bundle.seatsDiagnostics_bad_display())
                .description(Bundle.seatsDiagnostics_bad_description())
                .min(0.0)
                .add();
        b.withDouble()
                .select(config, "uncertain")
                .display(Bundle.seatsDiagnostics_uncertain_display())
                .description(Bundle.seatsDiagnostics_uncertain_description())
                .max(1.0)
                .add();
        sheet.put(b.build());

        return sheet;
    }

    private static Image getIcon() {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    private static Configurator<SeatsDiagnosticsFactory> createConfigurator() {
        return new ConfigHandler().toConfigurator(new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler extends BeanHandler<SeatsDiagnosticsConfiguration, SeatsDiagnosticsFactory> {

        @Override
        public SeatsDiagnosticsConfiguration loadBean(SeatsDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(SeatsDiagnosticsFactory resource, SeatsDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter extends Converter<SeatsDiagnosticsConfiguration, Config> {

        private final IParam<Config, Boolean> enabledParam = Params.onBoolean(true, "enabled");
        private final IParam<Config, Double> badParam = Params.onDouble(.005, "bad");
        private final IParam<Config, Double> uncertainParam = Params.onDouble(.05, "uncertain");

        @Override
        protected Config doForward(SeatsDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            enabledParam.set(result, a.isEnabled());
            badParam.set(result, a.getBad());
            uncertainParam.set(result, a.getUncertain());
            return result.build();
        }

        @Override
        protected SeatsDiagnosticsConfiguration doBackward(Config b) {
            SeatsDiagnosticsConfiguration result = new SeatsDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b));
            result.setBad(badParam.get(b));
            result.setUncertain(uncertainParam.get(b));
            return result;
        }
    }

    private static final class ConfigEditor implements IBeanEditor {

        @Messages({"seatsDiagnostics.edit.title=Edit Seats",
            "seatsDiagnostics.edit.errorTitle=Invalid Input",
            "seatsDiagnostics.edit.errorMessage=\nWould you like to modify your choice?"
        })
        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = createSheet((SeatsDiagnosticsConfiguration) bean);
            String title = Bundle.seatsDiagnostics_edit_title();
            while (true) {
                if (!new PropertySheetDialogBuilder().title(title).icon(getIcon()).editSheet(sheet)) {
                    return false;
                }
                try {
                    ((SeatsDiagnosticsConfiguration)bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.seatsDiagnostics_edit_errorMessage();
                    if(JOptionPane.showConfirmDialog(null, message , Bundle.seatsDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION){
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
