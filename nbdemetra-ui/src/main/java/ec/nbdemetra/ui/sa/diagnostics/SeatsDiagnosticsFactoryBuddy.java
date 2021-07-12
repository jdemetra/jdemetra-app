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
import ec.tss.sa.diagnostics.SeatsDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.SeatsDiagnosticsFactory;
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
import nbbrd.io.text.BooleanProperty;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import nbbrd.io.text.Property;
import demetra.ui.properties.BeanEditor;
import demetra.ui.Converter;
import demetra.ui.Persistable;
import demetra.ui.actions.Configurable;
import demetra.ui.beans.BeanConfigurator;

/**
 *
 * @author Laurent Jadoul
 */
@ServiceProvider(service = SaDiagnosticsFactoryBuddy.class)
public final class SeatsDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements Configurable, Persistable, demetra.ui.ConfigEditor, Resetable {

    private static final String NAME = "SeatsDiagnostics";

    private final BeanConfigurator<SeatsDiagnosticsConfiguration, SeatsDiagnosticsFactory> configurator = createConfigurator();

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
    public void configure() {
        Configurable.configure(this, this);
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

    private static BeanConfigurator<SeatsDiagnosticsConfiguration, SeatsDiagnosticsFactory> createConfigurator() {
        return new BeanConfigurator<>(new ConfigHandler(), new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler implements BeanHandler<SeatsDiagnosticsConfiguration, SeatsDiagnosticsFactory> {

        @Override
        public SeatsDiagnosticsConfiguration loadBean(SeatsDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(SeatsDiagnosticsFactory resource, SeatsDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter implements Converter<SeatsDiagnosticsConfiguration, Config> {

        private final BooleanProperty enabledParam = BooleanProperty.of("enabled", true);
        private final Property<Double> badParam = Property.of("bad", .005, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> uncertainParam = Property.of("uncertain", .05, Parser.onDouble(), Formatter.onDouble());

        @Override
        public Config doForward(SeatsDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            enabledParam.set(result::parameter, a.isEnabled());
            badParam.set(result::parameter, a.getBad());
            uncertainParam.set(result::parameter, a.getUncertain());
            return result.build();
        }

        @Override
        public SeatsDiagnosticsConfiguration doBackward(Config b) {
            SeatsDiagnosticsConfiguration result = new SeatsDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b::getParameter));
            result.setBad(badParam.get(b::getParameter));
            result.setUncertain(uncertainParam.get(b::getParameter));
            return result;
        }
    }

    private static final class ConfigEditor implements BeanEditor {

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
                    ((SeatsDiagnosticsConfiguration) bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.seatsDiagnostics_edit_errorMessage();
                    if (JOptionPane.showConfirmDialog(null, message, Bundle.seatsDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
