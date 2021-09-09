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
import ec.tss.sa.diagnostics.ResidualTradingDaysDiagnosticsConfiguration;
import ec.tss.sa.diagnostics.ResidualTradingDaysDiagnosticsFactory;
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
 * @author Laurent Jadoul
 */
@ServiceProvider(SaDiagnosticsFactoryBuddy.class)
public final class ResidualTradingDaysDiagnosticsFactoryBuddy extends SaDiagnosticsFactoryBuddy implements Configurable, Persistable, ConfigEditor, Resetable {

    private static final String NAME = "ResidualTradingDaysDiagnostics";

    private final BeanConfigurator<ResidualTradingDaysDiagnosticsConfiguration, ResidualTradingDaysDiagnosticsFactory> configurator = createConfigurator();

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
    public void configure() {
        Configurable.configure(this, this);
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
        "residualTradingDaysDiagnostics.thresholds.display=Thresholds",})
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
        return ImageUtilities.icon2Image(DemetraIcons.PUZZLE_16);
    }

    private static BeanConfigurator<ResidualTradingDaysDiagnosticsConfiguration, ResidualTradingDaysDiagnosticsFactory> createConfigurator() {
        return new BeanConfigurator<>(new ConfigHandler(), new ConfigConverter(), new ConfigEditor());
    }

    private static final class ConfigHandler implements BeanHandler<ResidualTradingDaysDiagnosticsConfiguration, ResidualTradingDaysDiagnosticsFactory> {

        @Override
        public ResidualTradingDaysDiagnosticsConfiguration loadBean(ResidualTradingDaysDiagnosticsFactory resource) {
            return resource.getConfiguration().clone();
        }

        @Override
        public void storeBean(ResidualTradingDaysDiagnosticsFactory resource, ResidualTradingDaysDiagnosticsConfiguration bean) {
            resource.setProperties(bean);
        }
    }

    private static final class ConfigConverter implements Converter<ResidualTradingDaysDiagnosticsConfiguration, Config> {

        private final BooleanProperty enabledParam = BooleanProperty.of("enabled", true);
        private final BooleanProperty arParam = BooleanProperty.of("ar", true);
        private final IntProperty spanParam = IntProperty.of("span", 8);
        private final Property<Double> severeParam = Property.of("severe", .001, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> badParam = Property.of("bad", .01, Parser.onDouble(), Formatter.onDouble());
        private final Property<Double> uncertainParam = Property.of("uncertain", .1, Parser.onDouble(), Formatter.onDouble());

        @Override
        public Config doForward(ResidualTradingDaysDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder(NAME, "INSTANCE", "20170209");
            enabledParam.set(result::parameter, a.isEnabled());
            arParam.set(result::parameter, a.isArModelling());
            spanParam.set(result::parameter, a.getSpan());
            badParam.set(result::parameter, a.getBadThreshold());
            uncertainParam.set(result::parameter, a.getUncertainThreshold());
            severeParam.set(result::parameter, a.getSevereThreshold());
            return result.build();
        }

        @Override
        public ResidualTradingDaysDiagnosticsConfiguration doBackward(Config b) {
            ResidualTradingDaysDiagnosticsConfiguration result = new ResidualTradingDaysDiagnosticsConfiguration();
            result.setEnabled(enabledParam.get(b::getParameter));
            result.setArModelling(arParam.get(b::getParameter));
            result.setSpan(spanParam.get(b::getParameter));
            result.setSevereThreshold(severeParam.get(b::getParameter));
            result.setBadThreshold(badParam.get(b::getParameter));
            result.setUncertainThreshold(uncertainParam.get(b::getParameter));
            return result;
        }
    }

    private static final class ConfigEditor implements BeanEditor {

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
