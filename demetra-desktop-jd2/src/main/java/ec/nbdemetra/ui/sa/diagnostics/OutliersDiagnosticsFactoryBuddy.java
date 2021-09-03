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
import ec.tss.sa.diagnostics.OutliersDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.OutliersDiagnosticsFactory;
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
public final class OutliersDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements Configurable, Persistable, ConfigEditor, Resetable {

    private static final String NAME = "OutliersDiagnostics";

    private final BeanConfigurator<OutliersDiagnosticsConfiguration, OutliersDiagnosticsFactory> configurator = createConfigurator();

    @Override
    public String getName() {
        return NAME;
    }

    @Messages("outliersDiagnostics.display=Outliers")
    @Override
    public String getDisplayName() {
        return Bundle.outliersDiagnostics_display();
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
        lookup().setProperties(new OutliersDiagnosticsConfiguration());
    }

    @Override
    public Sheet createSheet() {
        return createSheet(lookup().getConfiguration());
    }

    private OutliersDiagnosticsFactory lookup() {
        return Lookup.getDefault().lookup(OutliersDiagnosticsFactory.class);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @Messages({
        "outliersDiagnostics.enabled.display=Enabled",
        "outliersDiagnostics.enabled.description=Enable or not the diagnostic",
        "outliersDiagnostics.severe.display=Severe",
        "outliersDiagnostics.severe.description=Severe",
        "outliersDiagnostics.bad.display=Bad",
        "outliersDiagnostics.bad.description=Bad",
        "outliersDiagnostics.uncertain.display=Uncertain",
        "outliersDiagnostics.uncertain.description=Uncertain",
        "outliersDiagnostics.appearance.display=Appearance",
        "outliersDiagnostics.relnr.display=Relative number of outliers"
    })
    private static Sheet createSheet(OutliersDiagnosticsConfiguration config) {
        Sheet sheet = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("appearance").display(Bundle.outliersDiagnostics_appearance_display());
        b.withBoolean()
                .select(config, "enabled")
                .display(Bundle.outliersDiagnostics_enabled_display())
                .description(Bundle.outliersDiagnostics_enabled_description())
                .add();
        sheet.put(b.build());

        b.reset("relnr").display(Bundle.outliersDiagnostics_relnr_display());
        b.withDouble()
                .select(config, "severe")
                .display(Bundle.outliersDiagnostics_severe_display())
                .description(Bundle.outliersDiagnostics_severe_description())
                .add();
        b.withDouble()
                .select(config, "bad")
                .display(Bundle.outliersDiagnostics_bad_display())
                .description(Bundle.outliersDiagnostics_bad_description())
                .add();
        b.withDouble()
                .select(config, "uncertain")
                .display(Bundle.outliersDiagnostics_uncertain_display())
                .description(Bundle.outliersDiagnostics_uncertain_description())
                .min(0.0)
                .add();
        sheet.put(b.build());

        return sheet;
    }

    private static Image getIcon() {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    private static BeanConfigurator<OutliersDiagnosticsConfiguration, OutliersDiagnosticsFactory> createConfigurator() {
        return new BeanConfigurator<>(new ConfigHandler(), new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler implements BeanHandler<OutliersDiagnosticsConfiguration, OutliersDiagnosticsFactory> {

        @Override
        public OutliersDiagnosticsConfiguration loadBean(OutliersDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(OutliersDiagnosticsFactory resource, OutliersDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter implements Converter<OutliersDiagnosticsConfiguration, Config> {

        private final BooleanProperty enabledParam = BooleanProperty.of("enabled", true);
        private final Property<Double> severeParam = Property.of("severe", .10, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> badParam = Property.of("bad",.05, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> uncertainParam = Property.of("uncertain",.03, Parser.onDouble(), Formatter.onDouble());

        @Override
        public Config doForward(OutliersDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            enabledParam.set(result::parameter, a.isEnabled());
            severeParam.set(result::parameter, a.getSevere());
            badParam.set(result::parameter, a.getBad());
            uncertainParam.set(result::parameter, a.getUncertain());
            return result.build();
        }

        @Override
        public OutliersDiagnosticsConfiguration doBackward(Config b) {
            OutliersDiagnosticsConfiguration result = new OutliersDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b::getParameter));
            result.setSevere(severeParam.get(b::getParameter));
            result.setBad(badParam.get(b::getParameter));
            result.setUncertain(uncertainParam.get(b::getParameter));
            return result;
        }
    }

    private static final class ConfigEditor implements BeanEditor {

        @Messages({"outliersDiagnostics.edit.title=Edit Outliers",
            "outliersDiagnostics.edit.errorTitle=Invalid Input",
            "outliersDiagnostics.edit.errorMessage=\nWould you like to modify your choice?"
        })
        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = createSheet((OutliersDiagnosticsConfiguration) bean);
            String title = Bundle.outliersDiagnostics_edit_title();
            while (true) {
                if (!new PropertySheetDialogBuilder().title(title).icon(getIcon()).editSheet(sheet)) {
                    return false;
                }
                try {
                    ((OutliersDiagnosticsConfiguration) bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.outliersDiagnostics_edit_errorMessage();
                    if (JOptionPane.showConfirmDialog(null, message, Bundle.outliersDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
