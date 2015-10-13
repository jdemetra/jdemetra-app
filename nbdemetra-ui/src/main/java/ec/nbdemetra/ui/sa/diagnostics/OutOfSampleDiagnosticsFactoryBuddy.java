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
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.nbdemetra.ui.sa.SaDiagnosticsFactoryBuddy;
import ec.tss.sa.diagnostics.OutOfSampleDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.OutOfSampleDiagnosticsFactory;
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

/**
 *
 * @author Laurent Jadoul
 */
@ServiceProvider(service = SaDiagnosticsFactoryBuddy.class)
public final class OutOfSampleDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements IConfigurable {

    private static final String NAME = "OutOfSampleDiagnostics";

    private final Configurator<OutOfSampleDiagnosticsFactory> configurator = createConfigurator();

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
        "outOfSampleDiagnostics.length.description=Forecasting length",
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

    private static Configurator<OutOfSampleDiagnosticsFactory> createConfigurator() {
        return new ConfigHandler().toConfigurator(new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler extends BeanHandler<OutOfSampleDiagnosticsConfiguration, OutOfSampleDiagnosticsFactory> {

        @Override
        public OutOfSampleDiagnosticsConfiguration loadBean(OutOfSampleDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(OutOfSampleDiagnosticsFactory resource, OutOfSampleDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter extends Converter<OutOfSampleDiagnosticsConfiguration, Config> {

        private final IParam<Config, Double> badParam = Params.onDouble(.01, "bad");
        private final IParam<Config, Double> uncertainParam = Params.onDouble(.1, "uncertain");
        private final IParam<Config, Double> lengthParam = Params.onDouble(.01, "length");
        private final IParam<Config, Boolean> menabledParam = Params.onBoolean(true, "menabled");
        private final IParam<Config, Boolean> venabledParam = Params.onBoolean(true, "venabled");
        
        @Override
        protected Config doForward(OutOfSampleDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20151008");
            menabledParam.set(result, a.isMeanTestEnabled());
            venabledParam.set(result, a.isMSETestEnabled());
            badParam.set(result, a.getBad());
            uncertainParam.set(result, a.getUncertain());
            lengthParam.set(result, a.getForecastingLength());
            return result.build();
        }

        @Override
        protected OutOfSampleDiagnosticsConfiguration doBackward(Config b) {
            OutOfSampleDiagnosticsConfiguration result = new OutOfSampleDiagnosticsConfiguration();
            result.setMeanTestEnabled(menabledParam.get(b));
            result.setMSETestEnabled(venabledParam.get(b));
            result.setBad(badParam.get(b));
            result.setUncertain(uncertainParam.get(b));
            result.setForecastingLength(lengthParam.get(b));
            return result;
        }
    }

    private static final class ConfigEditor implements IBeanEditor {

        @Messages({"outOfSampleDiagnostics.edit.title=Edit Out of Sample",
            "outOfSampleDiagnostics.edit.errorTitle=Invalid Input",
            "outOfSampleDiagnostics.edit.errorMessage=\nWould you like to modify your choice?"
        })
        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = createSheet((OutOfSampleDiagnosticsConfiguration) bean);
            String title = Bundle.outOfSampleDiagnostics_edit_title();
            while (true) {
                if (!OpenIdePropertySheetBeanEditor.editSheet(sheet, title, getIcon())) {
                    return false;
                }
                try {
                    ((OutOfSampleDiagnosticsConfiguration)bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.outOfSampleDiagnostics_edit_errorMessage();
                    if(JOptionPane.showConfirmDialog(null, message , Bundle.outOfSampleDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION){
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
