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
import demetra.ui.properties.PropertySheetDialogBuilder;
import demetra.ui.properties.IBeanEditor;
import demetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.sa.SaDiagnosticsFactoryBuddy;
import ec.tss.sa.diagnostics.ResidualTradingDaysDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.ResidualTradingDaysDiagnosticsFactory;
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
public final class ResidualTradingDaysDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements IConfigurable, Resetable {

    private static final String NAME = "ResidualTradingDaysDiagnostics";

    private final Configurator<ResidualTradingDaysDiagnosticsFactory> configurator = createConfigurator();

    @Override
    public String getName() {
        return NAME;
    }

    @Messages("residualTradingDaysDiagnostics.display=Residual trading days effects")
    @Override
    public String getDisplayName() {
        return Bundle.residualTradingDaysDiagnostics_display();
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
        lookup().setProperties(new ResidualTradingDaysDiagnosticsConfiguration());
    }

    @Override
    public Sheet createSheet() {
        return createSheet(lookup().getConfiguration());
    }

    private ResidualTradingDaysDiagnosticsFactory lookup() {
        return Lookup.getDefault().lookup(ResidualTradingDaysDiagnosticsFactory.class);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @Messages({
        "residualTradingDaysDiagnostics.enabled.display=Enabled",
        "residualTradingDaysDiagnostics.enabled.description=Enable or not the diagnostic",
        "residualTradingDaysDiagnostics.ar.display=AR Modelling",
        "residualTradingDaysDiagnostics.ar.description=Auto-regressive modelling",
        "residualTradingDaysDiagnostics.span.display=Span for F-Test",
        "residualTradingDaysDiagnostics.span.description=Span (in years) for F-Test on td variables",
        "residualTradingDaysDiagnostics.severe.display=Severe",
        "residualTradingDaysDiagnostics.severe.description=Severe",
        "residualTradingDaysDiagnostics.bad.display=Bad",
        "residualTradingDaysDiagnostics.bad.description=Bad",
        "residualTradingDaysDiagnostics.uncertain.display=Uncertain",
        "residualTradingDaysDiagnostics.uncertain.description=Uncertain",
        "residualTradingDaysDiagnostics.test.display=Test",
        "residualTradingDaysDiagnostics.thresholds.display=Thresholds",
    })
    private static Sheet createSheet(ResidualTradingDaysDiagnosticsConfiguration config) {
        Sheet sheet = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("test").display(Bundle.residualTradingDaysDiagnostics_test_display());
        b.withBoolean()
                .select(config, "enabled")
                .display(Bundle.residualTradingDaysDiagnostics_enabled_display())
                .description(Bundle.residualTradingDaysDiagnostics_enabled_description())
                .add();
        b.withBoolean()
                .select(config, "arModelling")
                .display(Bundle.residualTradingDaysDiagnostics_ar_display())
                .description(Bundle.residualTradingDaysDiagnostics_ar_description())
                .add();
        b.withInt()
                .select(config, "span")
                .display(Bundle.residualTradingDaysDiagnostics_span_display())
                .description(Bundle.residualTradingDaysDiagnostics_span_description())
                .add();
        sheet.put(b.build());

        b.reset("thresholds").display(Bundle.residualTradingDaysDiagnostics_thresholds_display());
        b.withDouble()
                .select(config, "uncertainThreshold")
                .display(Bundle.residualTradingDaysDiagnostics_uncertain_display())
                .description(Bundle.residualTradingDaysDiagnostics_uncertain_description())
                .add();

        b.withDouble()
                .select(config, "badThreshold")
                .display(Bundle.residualTradingDaysDiagnostics_bad_display())
                .description(Bundle.residualTradingDaysDiagnostics_bad_description())
                .add();
 
        b.withDouble()
                .select(config, "severeThreshold")
                .display(Bundle.residualTradingDaysDiagnostics_severe_display())
                .description(Bundle.residualTradingDaysDiagnostics_severe_description())
                .add();
        sheet.put(b.build());
  
        return sheet;
    }

    private static Image getIcon() {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    private static Configurator<ResidualTradingDaysDiagnosticsFactory> createConfigurator() {
        return new ConfigHandler().toConfigurator(new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler extends BeanHandler<ResidualTradingDaysDiagnosticsConfiguration, ResidualTradingDaysDiagnosticsFactory> {

        @Override
        public ResidualTradingDaysDiagnosticsConfiguration loadBean(ResidualTradingDaysDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(ResidualTradingDaysDiagnosticsFactory resource, ResidualTradingDaysDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter extends Converter<ResidualTradingDaysDiagnosticsConfiguration, Config> {

        private final IParam<Config, Boolean> enabledParam = Params.onBoolean(true, "enabled");
        private final IParam<Config, Boolean> arParam = Params.onBoolean(true, "ar");
        private final IParam<Config, Integer> spanParam = Params.onInteger(8, "span");
        private final IParam<Config, Double> severeParam = Params.onDouble(.001, "severe");
        private final IParam<Config, Double> badParam = Params.onDouble(.01, "bad");
        private final IParam<Config, Double> uncertainParam = Params.onDouble(.1, "uncertain");

        @Override
        protected Config doForward(ResidualTradingDaysDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20170209");
            enabledParam.set(result, a.isEnabled());
            arParam.set(result, a.isArModelling());
            spanParam.set(result, a.getSpan());
            badParam.set(result, a.getBadThreshold());
            uncertainParam.set(result, a.getUncertainThreshold());
            severeParam.set(result, a.getSevereThreshold());
            return result.build();
        }

        @Override
        protected ResidualTradingDaysDiagnosticsConfiguration doBackward(Config b) {
            ResidualTradingDaysDiagnosticsConfiguration result = new ResidualTradingDaysDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b));
            result.setArModelling(arParam.get(b));
            result.setSpan(spanParam.get(b));
            result.setSevereThreshold(severeParam.get(b));
            result.setBadThreshold(badParam.get(b));
            result.setUncertainThreshold(uncertainParam.get(b));
            return result;
        }
    }

    private static final class ConfigEditor implements IBeanEditor {

        @Messages({"residualTradingDaysDiagnostics.edit.title=Edit residual trading days diagnostics",
            "residualTradingDaysDiagnostics.edit.errorTitle=Invalid Input",
            "residualTradingDaysDiagnostics.edit.errorMessage=\nWould you like to modify your choice?"
        })
        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = createSheet((ResidualTradingDaysDiagnosticsConfiguration) bean);
            String title = Bundle.residualTradingDaysDiagnostics_edit_title();
            while (true) {
                if (!new PropertySheetDialogBuilder().title(title).icon(getIcon()).editSheet(sheet)) {
                    return false;
                }
                try {
                    ((ResidualTradingDaysDiagnosticsConfiguration) bean).check();
                    return true;
                } catch (BaseException ex) {
                    String message = ex.getMessage() + Bundle.residualTradingDaysDiagnostics_edit_errorMessage();
                    if (JOptionPane.showConfirmDialog(null, message, Bundle.residualTradingDaysDiagnostics_edit_errorTitle(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return false;
                    }
                }
            }
        }
    }
    //</editor-fold>
}
