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
import ec.tss.sa.diagnostics.ResidualsDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.ResidualsDiagnosticsFactory;
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
public final class ResidualsDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements Configurable, Persistable, ConfigEditor, Resetable {

    private static final String NAME = "ResidualsDiagnostics";

    private final BeanConfigurator<ResidualsDiagnosticsConfiguration, ResidualsDiagnosticsFactory> configurator = createConfigurator();

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
    public void configure() {
        Configurable.configure(this, this);
    }

    @Override
    public void reset() {
        lookup().setProperties(new ResidualsDiagnosticsConfiguration());
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
        "residualsDiagnostics.niddTestCategory.display=NIID tests",
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
        return ImageUtilities.icon2Image(DemetraIcons.PUZZLE_16);
    }

    private static BeanConfigurator<ResidualsDiagnosticsConfiguration, ResidualsDiagnosticsFactory> createConfigurator() {
        return new BeanConfigurator<>(new ConfigHandler(), new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler implements BeanHandler<ResidualsDiagnosticsConfiguration, ResidualsDiagnosticsFactory> {

        @Override
        public ResidualsDiagnosticsConfiguration loadBean(ResidualsDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(ResidualsDiagnosticsFactory resource, ResidualsDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter implements Converter<ResidualsDiagnosticsConfiguration, Config> {

        private final BooleanProperty enabledParam = BooleanProperty.of("enabled", true);
        private final Property<Double> normalityBadParam = Property.of("normalityBad", .01, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> normalityUncertainParam = Property.of("normalityUncertain", .1, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> specTDSevereParam = Property.of("specTDSevereP", .001, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> specTDBadParam = Property.of("specTDBad", .01, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> specTDUncertainParam = Property.of("specTDUncertain", .1, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> specSeasSevereParam = Property.of("specSeasSevere", .001, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> specSeasBadParam = Property.of("specSeasBad", .01, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> specSeasUncertainParam = Property.of("specSeasUncertain", .1, Parser.onDouble(), Formatter.onDouble());

        @Override
        public Config doForward(ResidualsDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            enabledParam.set(result::parameter, a.isEnabled());
            normalityBadParam.set(result::parameter, a.getNIIDBad());
            normalityUncertainParam.set(result::parameter, a.getNIIDUncertain());
            specTDSevereParam.set(result::parameter, a.getSpecTDSevere());
            specTDBadParam.set(result::parameter, a.getSpecTDBad());
            specTDUncertainParam.set(result::parameter, a.getSpecTDUncertain());
            specSeasSevereParam.set(result::parameter, a.getSpecSeasSevere());
            specSeasBadParam.set(result::parameter, a.getSpecSeasBad());
            specSeasUncertainParam.set(result::parameter, a.getSpecSeasUncertain());
            return result.build();
        }

        @Override
        public ResidualsDiagnosticsConfiguration doBackward(Config b) {
            ResidualsDiagnosticsConfiguration result = new ResidualsDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b::getParameter));
            result.setNIIDBad(normalityBadParam.get(b::getParameter));
            result.setNIIDUncertain(normalityUncertainParam.get(b::getParameter));
            result.setSpecTDSevere(specTDSevereParam.get(b::getParameter));
            result.setSpecTDBad(specTDBadParam.get(b::getParameter));
            result.setSpecTDUncertain(specTDUncertainParam.get(b::getParameter));
            result.setSpecSeasSevere(specSeasSevereParam.get(b::getParameter));
            result.setSpecSeasBad(specSeasBadParam.get(b::getParameter));
            result.setSpecSeasUncertain(specSeasUncertainParam.get(b::getParameter));
            return result;
        }
    }

    private static final class ConfigEditor implements BeanEditor {

        @Messages({"residualsDiagnostics.edit.title=Edit Regarima residuals",
            "residualsDiagnostics.edit.errorTitle=Invalid Input",
            "residualsDiagnostics.edit.errorMessage=\nWould you like to modify your choice?"
        })
        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = createSheet((ResidualsDiagnosticsConfiguration) bean);
            String title = Bundle.residualsDiagnostics_edit_title();
            while (true) {
                if (!new PropertySheetDialogBuilder().title(title).icon(getIcon()).editSheet(sheet)) {
                    return false;
                }
                try {
                    ((ResidualsDiagnosticsConfiguration) bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.residualsDiagnostics_edit_errorMessage();
                    if (JOptionPane.showConfirmDialog(null, message, Bundle.residualsDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
