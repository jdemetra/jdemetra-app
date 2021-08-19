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

import demetra.ui.beans.BeanHandler;
import demetra.ui.Config;
import ec.nbdemetra.ui.DemetraUiIcon;
import demetra.ui.properties.PropertySheetDialogBuilder;
import demetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.sa.SaDiagnosticsFactoryBuddy;
import ec.tss.sa.diagnostics.SpectralDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.SpectralDiagnosticsFactory;
import ec.tstoolkit.BaseException;
import java.awt.Image;
import java.beans.IntrospectionException;
import javax.swing.JOptionPane;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import demetra.ui.actions.Resetable;
import nbbrd.io.text.BooleanProperty;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.IntProperty;
import nbbrd.io.text.Parser;
import nbbrd.io.text.Property;
import demetra.ui.properties.BeanEditor;
import demetra.ui.Converter;
import demetra.ui.Persistable;
import demetra.ui.actions.Configurable;
import demetra.ui.beans.BeanConfigurator;
import nbbrd.service.ServiceProvider;

/**
 *
 * @author Laurent Jadoul
 */
@ServiceProvider(SaDiagnosticsFactoryBuddy.class)
public final class SpectralDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements Configurable, Persistable, demetra.ui.ConfigEditor, Resetable {

    private static final String NAME = "SpectralDiagnostics";

    private final BeanConfigurator<SpectralDiagnosticsConfiguration, SpectralDiagnosticsFactory> configurator = createConfigurator();

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
    public void configure() {
        Configurable.configure(this, this);
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

    private static BeanConfigurator<SpectralDiagnosticsConfiguration, SpectralDiagnosticsFactory> createConfigurator() {
        return new BeanConfigurator<>(new ConfigHandler(), new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler implements BeanHandler<SpectralDiagnosticsConfiguration, SpectralDiagnosticsFactory> {

        @Override
        public SpectralDiagnosticsConfiguration loadBean(SpectralDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(SpectralDiagnosticsFactory resource, SpectralDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter implements Converter<SpectralDiagnosticsConfiguration, Config> {

        private final BooleanProperty enabledParam = BooleanProperty.of("enabled", false);
        private final BooleanProperty strictParam = BooleanProperty.of("strict", true);
        private final Property<Double> sensParam = Property.of("sensitivity", 6.0 / 52, Parser.onDouble(), Formatter.onDouble());
        private final IntProperty lengthParam = IntProperty.of("length", 8);

        @Override
        public Config doForward(SpectralDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            enabledParam.set(result::parameter, a.isEnabled());
            strictParam.set(result::parameter, a.isStrict());
            sensParam.set(result::parameter, a.getSensitivity());
            lengthParam.set(result::parameter, a.getLength());
            return result.build();
        }

        @Override
        public SpectralDiagnosticsConfiguration doBackward(Config b) {
            SpectralDiagnosticsConfiguration result = new SpectralDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b::getParameter));
            result.setStrict(strictParam.get(b::getParameter));
            result.setSensitivity(sensParam.get(b::getParameter));
            result.setLength(lengthParam.get(b::getParameter));
            return result;
        }
    }

    private static final class ConfigEditor implements BeanEditor {

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
                    ((SpectralDiagnosticsConfiguration) bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.spectralDiagnostics_edit_errorMessage();
                    if (JOptionPane.showConfirmDialog(null, message, Bundle.spectralDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
            }
        }

    }
    //</editor-fold>
}
