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
import ec.tss.sa.diagnostics.CoherenceDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.CoherenceDiagnosticsFactory;
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
import nbbrd.io.text.IntProperty;
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
 * @author Philippe Charles
 */
@ServiceProvider(SaDiagnosticsFactoryBuddy.class)
public final class CoherenceDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements Configurable, Persistable, ConfigEditor, Resetable {

    private static final String NAME = "CoherenceDiagnostics";

    private final BeanConfigurator<CoherenceDiagnosticsConfiguration, CoherenceDiagnosticsFactory> configurator = createConfigurator();

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
    public void configure() {
        Configurable.configure(this, this);
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
        return ImageUtilities.icon2Image(DemetraIcons.PUZZLE_16);
    }

    private static BeanConfigurator<CoherenceDiagnosticsConfiguration, CoherenceDiagnosticsFactory> createConfigurator() {
        return new BeanConfigurator<>(new ConfigHandler(), new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler implements BeanHandler<CoherenceDiagnosticsConfiguration, CoherenceDiagnosticsFactory> {

        @Override
        public CoherenceDiagnosticsConfiguration loadBean(CoherenceDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(CoherenceDiagnosticsFactory resource, CoherenceDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter implements Converter<CoherenceDiagnosticsConfiguration, Config> {

        private final BooleanProperty enabledParam = BooleanProperty.of("enabled", true);
        private final Property<Double> toleranceParam = Property.of("tolerance", 1e-3, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> errorParam = Property.of("error", .5, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> severeParam = Property.of("severe", .1, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> badParam = Property.of("bad", .05, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> uncertainParam = Property.of("uncertain", .01, Parser.onDouble(), Formatter.onDouble());
        private final IntProperty shortSeriesParam = IntProperty.of("shortSeries", 7);

        @Override
        public Config doForward(CoherenceDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            enabledParam.set(result::parameter, a.isEnabled());
            toleranceParam.set(result::parameter, a.getTolerance());
            errorParam.set(result::parameter, a.getError());
            severeParam.set(result::parameter, a.getSevere());
            badParam.set(result::parameter, a.getBad());
            uncertainParam.set(result::parameter, a.getUncertain());
            shortSeriesParam.set(result::parameter, a.getShortSeries());
            return result.build();
        }

        @Override
        public CoherenceDiagnosticsConfiguration doBackward(Config b) {
            CoherenceDiagnosticsConfiguration result = new CoherenceDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b::getParameter));
            result.setTolerance(toleranceParam.get(b::getParameter));
            result.setError(errorParam.get(b::getParameter));
            result.setSevere(severeParam.get(b::getParameter));
            result.setBad(badParam.get(b::getParameter));
            result.setUncertain(uncertainParam.get(b::getParameter));
            result.setShortSeries(shortSeriesParam.get(b::getParameter));
            return result;
        }
    }

    private static final class ConfigEditor implements BeanEditor {

        @Messages({"coherenceDiagnostics.edit.title=Edit basic checks",
            "coherenceDiagnostics.edit.errorTitle=Invalid Input",
            "coherenceDiagnostics.edit.errorMessage=\nWould you like to modify your choice?"
        })
        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = createSheet((CoherenceDiagnosticsConfiguration) bean);
            String title = Bundle.coherenceDiagnostics_edit_title();
            while (true) {
                if (!new PropertySheetDialogBuilder().title(title).icon(getIcon()).editSheet(sheet)) {
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
