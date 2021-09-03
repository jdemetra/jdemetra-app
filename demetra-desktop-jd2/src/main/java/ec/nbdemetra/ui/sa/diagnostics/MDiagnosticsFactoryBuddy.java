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
import ec.nbdemetra.ui.DemetraUiIcon;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.desktop.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.sa.SaDiagnosticsFactoryBuddy;
import ec.tss.sa.diagnostics.MDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.MDiagnosticsFactory;
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
import demetra.desktop.properties.BeanEditor;
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
public final class MDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements Configurable, Persistable, ConfigEditor, Resetable {

    private static final String NAME = "MDiagnostics";

    private final BeanConfigurator<MDiagnosticsConfiguration, MDiagnosticsFactory> configurator = createConfigurator();

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
    public void configure() {
        Configurable.configure(this, this);
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

    private static BeanConfigurator<MDiagnosticsConfiguration, MDiagnosticsFactory> createConfigurator() {
        return new BeanConfigurator<>(new ConfigHandler(), new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler implements BeanHandler<MDiagnosticsConfiguration, MDiagnosticsFactory> {

        @Override
        public MDiagnosticsConfiguration loadBean(MDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(MDiagnosticsFactory resource, MDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter implements Converter<MDiagnosticsConfiguration, Config> {

        private final BooleanProperty enabledParam = BooleanProperty.of("enabled", true);
        private final Property<Double> severeParam = Property.of("severe", 2d, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> badParam = Property.of("bad", 1d, Parser.onDouble(), Formatter.onDouble());
        private final BooleanProperty allParam = BooleanProperty.of("all", true);

        @Override
        public Config doForward(MDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            enabledParam.set(result::parameter, a.isEnabled());
            severeParam.set(result::parameter, a.getSevere());
            badParam.set(result::parameter, a.getBad());
            allParam.set(result::parameter, a.isUseAll());
            return result.build();
        }

        @Override
        public MDiagnosticsConfiguration doBackward(Config b) {
            MDiagnosticsConfiguration result = new MDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b::getParameter));
            result.setSevere(severeParam.get(b::getParameter));
            result.setBad(badParam.get(b::getParameter));
            result.setUseAll(allParam.get(b::getParameter));
            return result;
        }
    }

    private static final class ConfigEditor implements BeanEditor {

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
                    ((MDiagnosticsConfiguration) bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.mDiagnostics_edit_errorMessage();
                    if (JOptionPane.showConfirmDialog(null, message, Bundle.mDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
