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
import ec.tss.sa.diagnostics.OutOfSampleDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.OutOfSampleDiagnosticsFactory;
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
public final class OutOfSampleDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements Configurable, Persistable, demetra.ui.ConfigEditor, Resetable {

    private static final String NAME = "OutOfSampleDiagnostics";

    private final BeanConfigurator<OutOfSampleDiagnosticsConfiguration, OutOfSampleDiagnosticsFactory> configurator = createConfigurator();

    @Override
    public String getName() {
        return NAME;
    }

    @Messages("outOfSampleDiagnostics.display=Out of Sample")
    @Override
    public String getDisplayName() {
        return Bundle.outOfSampleDiagnostics_display();
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
        lookup().setProperties(new OutOfSampleDiagnosticsConfiguration());
    }

    @Override
    public Sheet createSheet() {
        return createSheet(lookup().getConfiguration());
    }

    private OutOfSampleDiagnosticsFactory lookup() {
        return Lookup.getDefault().lookup(OutOfSampleDiagnosticsFactory.class);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @Messages({
        "outOfSampleDiagnostics.menabled.display=Mean test enabled",
        "outOfSampleDiagnostics.menabled.description=Enable or not the diagnostic",
        "outOfSampleDiagnostics.venabled.display=MSE test enabled",
        "outOfSampleDiagnostics.venabled.description=Enable or not the diagnostic",
        "outOfSampleDiagnostics.bad.display=Bad",
        "outOfSampleDiagnostics.bad.description=Bad",
        "outOfSampleDiagnostics.uncertain.display=Uncertain",
        "outOfSampleDiagnostics.uncertain.description=Uncertain",
        "outOfSampleDiagnostics.length.display=Forecasting length",
        "outOfSampleDiagnostics.length.description=Forecasting length (in years)",
        "outOfSampleDiagnostics.appearance.display=Appearance",
        "outOfSampleDiagnostics.test.display=Test options"
    })
    private static Sheet createSheet(OutOfSampleDiagnosticsConfiguration config) {
        Sheet sheet = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("appearance").display(Bundle.outOfSampleDiagnostics_appearance_display());
        b.withBoolean()
                .select(config, "meanTestEnabled")
                .display(Bundle.outOfSampleDiagnostics_menabled_display())
                .description(Bundle.outOfSampleDiagnostics_menabled_description())
                .add();
        b.withBoolean()
                .select(config, "mSETestEnabled")
                .display(Bundle.outOfSampleDiagnostics_venabled_display())
                .description(Bundle.outOfSampleDiagnostics_venabled_description())
                .add();
        sheet.put(b.build());

        b.reset("test").display(Bundle.outOfSampleDiagnostics_test_display());
        b.withDouble()
                .select(config, "bad")
                .display(Bundle.outOfSampleDiagnostics_bad_display())
                .description(Bundle.outOfSampleDiagnostics_bad_description())
                .add();
        b.withDouble()
                .select(config, "uncertain")
                .display(Bundle.outOfSampleDiagnostics_uncertain_display())
                .description(Bundle.outOfSampleDiagnostics_uncertain_description())
                .min(0.0)
                .add();
        b.withDouble()
                .select(config, "forecastingLength")
                .display(Bundle.outOfSampleDiagnostics_length_display())
                .description(Bundle.outOfSampleDiagnostics_length_description())
                .add();
        sheet.put(b.build());

        return sheet;
    }

    private static Image getIcon() {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    private static BeanConfigurator<OutOfSampleDiagnosticsConfiguration, OutOfSampleDiagnosticsFactory> createConfigurator() {
        return new BeanConfigurator<>(new ConfigHandler(), new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler implements BeanHandler<OutOfSampleDiagnosticsConfiguration, OutOfSampleDiagnosticsFactory> {

        @Override
        public OutOfSampleDiagnosticsConfiguration loadBean(OutOfSampleDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(OutOfSampleDiagnosticsFactory resource, OutOfSampleDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter implements Converter<OutOfSampleDiagnosticsConfiguration, Config> {

        private final Property<Double> badParam = Property.of("bad", .01, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> uncertainParam = Property.of("uncertain", .1, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> lengthParam = Property.of("length", .01, Parser.onDouble(), Formatter.onDouble());
        private final BooleanProperty menabledParam = BooleanProperty.of("menabled", false);
        private final BooleanProperty venabledParam = BooleanProperty.of("venabled", false);

        @Override
        public Config doForward(OutOfSampleDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            menabledParam.set(result::parameter, a.isMeanTestEnabled());
            venabledParam.set(result::parameter, a.isMSETestEnabled());
            badParam.set(result::parameter, a.getBad());
            uncertainParam.set(result::parameter, a.getUncertain());
            lengthParam.set(result::parameter, a.getForecastingLength());
            return result.build();
        }

        @Override
        public OutOfSampleDiagnosticsConfiguration doBackward(Config b) {
            OutOfSampleDiagnosticsConfiguration result = new OutOfSampleDiagnosticsConfiguration();
            result.setMeanTestEnabled(menabledParam.get(b::getParameter));
            result.setMSETestEnabled(venabledParam.get(b::getParameter));
            result.setBad(badParam.get(b::getParameter));
            result.setUncertain(uncertainParam.get(b::getParameter));
            result.setForecastingLength(lengthParam.get(b::getParameter));
            return result;
        }
    }

    private static final class ConfigEditor implements BeanEditor {

        @Messages({"outOfSampleDiagnostics.edit.title=Edit Out of Sample",
            "outOfSampleDiagnostics.edit.errorTitle=Invalid Input",
            "outOfSampleDiagnostics.edit.errorMessage=\nWould you like to modify your choice?"
        })
        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = createSheet((OutOfSampleDiagnosticsConfiguration) bean);
            String title = Bundle.outOfSampleDiagnostics_edit_title();
            while (true) {
                if (!new PropertySheetDialogBuilder().title(title).icon(getIcon()).editSheet(sheet)) {
                    return false;
                }
                try {
                    ((OutOfSampleDiagnosticsConfiguration) bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.outOfSampleDiagnostics_edit_errorMessage();
                    if (JOptionPane.showConfirmDialog(null, message, Bundle.outOfSampleDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
