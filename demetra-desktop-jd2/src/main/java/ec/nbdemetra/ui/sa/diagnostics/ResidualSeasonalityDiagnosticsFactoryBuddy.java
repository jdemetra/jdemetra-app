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
import ec.tss.sa.diagnostics.ResidualSeasonalityDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.ResidualSeasonalityDiagnosticsFactory;
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
public final class ResidualSeasonalityDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements Configurable, Persistable, demetra.ui.ConfigEditor, Resetable {

    private static final String NAME = "ResidualSeasonalityDiagnostics";

    private final BeanConfigurator<ResidualSeasonalityDiagnosticsConfiguration, ResidualSeasonalityDiagnosticsFactory> configurator = createConfigurator();

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
    public void configure() {
        Configurable.configure(this, this);
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

    private static BeanConfigurator<ResidualSeasonalityDiagnosticsConfiguration, ResidualSeasonalityDiagnosticsFactory> createConfigurator() {
        return new BeanConfigurator<>(new ConfigHandler(), new ConfigConverter(), new ConfigEditor());

    }

    private static final class ConfigHandler implements BeanHandler<ResidualSeasonalityDiagnosticsConfiguration, ResidualSeasonalityDiagnosticsFactory> {

        @Override
        public ResidualSeasonalityDiagnosticsConfiguration loadBean(ResidualSeasonalityDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(ResidualSeasonalityDiagnosticsFactory resource, ResidualSeasonalityDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter implements Converter<ResidualSeasonalityDiagnosticsConfiguration, Config> {

        private final BooleanProperty enabledParam = BooleanProperty.of("enabled", false);
        private final Property<Double> saSevereParam = Property.of("sasevere", .01, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> saBadParam = Property.of("saBad", .05, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> saUncertainParam = Property.of("saUncertain", .1, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> irrSevereParam = Property.of("irrSevere", .01, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> irrBadParam = Property.of("irrBad", .05, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> irrUncertainParam = Property.of("irrUncertain", .1, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> sa3SevereParam = Property.of("sa3Severe", .01, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> sa3BadParam = Property.of("sa3Bad", .05, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> sa3UncertainParam = Property.of("sa3Uncertain", .1, Parser.onDouble(), Formatter.onDouble());

        @Override
        public Config doForward(ResidualSeasonalityDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            enabledParam.set(result::parameter, a.isEnabled());
            saSevereParam.set(result::parameter, a.getSASevere());
            saBadParam.set(result::parameter, a.getSABad());
            saUncertainParam.set(result::parameter, a.getSAUncertain());
            irrSevereParam.set(result::parameter, a.getIrrSevere());
            irrBadParam.set(result::parameter, a.getIrrBad());
            irrUncertainParam.set(result::parameter, a.getIrrUncertain());
            sa3SevereParam.set(result::parameter, a.getSA3Severe());
            sa3BadParam.set(result::parameter, a.getSA3Bad());
            sa3UncertainParam.set(result::parameter, a.getSA3Uncertain());
            return result.build();
        }

        @Override
        public ResidualSeasonalityDiagnosticsConfiguration doBackward(Config b) {
            ResidualSeasonalityDiagnosticsConfiguration result = new ResidualSeasonalityDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b::getParameter));
            result.setSASevere(saSevereParam.get(b::getParameter));
            result.setSABad(saBadParam.get(b::getParameter));
            result.setSAUncertain(saUncertainParam.get(b::getParameter));
            result.setIrrSevere(irrSevereParam.get(b::getParameter));
            result.setIrrBad(irrBadParam.get(b::getParameter));
            result.setIrrUncertain(irrUncertainParam.get(b::getParameter));
            result.setSA3Severe(sa3SevereParam.get(b::getParameter));
            result.setSA3Bad(sa3BadParam.get(b::getParameter));
            result.setSA3Uncertain(sa3UncertainParam.get(b::getParameter));
            return result;
        }
    }

    private static final class ConfigEditor implements BeanEditor {

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
                    ((ResidualSeasonalityDiagnosticsConfiguration) bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.residualSeasonalityDiagnostics_edit_errorMessage();
                    if (JOptionPane.showConfirmDialog(null, message, Bundle.residualSeasonalityDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
