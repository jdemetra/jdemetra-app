/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package demetra.desktop.x13.util;

import demetra.desktop.Config;
import demetra.desktop.ui.properties.l2fprod.ArrayRenderer;
import demetra.desktop.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import demetra.desktop.ui.properties.l2fprod.CustomPropertyRendererFactory;
import demetra.desktop.util.InstallerStep;
import demetra.desktop.x13.descriptors.SeasonalFilterPropertyEditor;
import demetra.desktop.x13.diagnostics.X13DiagnosticsFactoryBuddies;
import demetra.desktop.x13.ui.X13UI;
import demetra.x11.SeasonalFilterOption;
import java.util.prefs.Preferences;
import org.openide.modules.ModuleInstall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Installer extends ModuleInstall {

    final static Logger LOGGER = LoggerFactory.getLogger(Installer.class);

    public static final InstallerStep STEP = InstallerStep.all(
            new DemetraX13DiagnosticsStep(), new PropertiesStep()
    );

    @Override
    public void restored() {
        super.restored();
        STEP.restore();
    }

    @Override
    public void close() {
        STEP.close();
        super.close();
    }

    private static final class DemetraX13DiagnosticsStep extends InstallerStep {

        final Preferences prefs = prefs().node("diagnostics");

        @Override
        public void restore() {
            X13DiagnosticsFactoryBuddies.getInstance().getFactories().forEach(buddy -> {
                Preferences nprefs = prefs.node(buddy.getDisplayName());
                tryGet(nprefs).ifPresent(buddy::setConfig);
            });
            X13UI.setDiagnostics();
        }

        @Override
        public void close() {
            X13DiagnosticsFactoryBuddies.getInstance().getFactories().forEach(buddy -> {
                Config config = buddy.getConfig();
                if (config != null) {
                    Preferences nprefs = prefs.node(buddy.getDisplayName());
                    put(nprefs, config);
                }
            });
        }
    }

    private static final class PropertiesStep extends InstallerStep {

        @Override
        public void restore() {
            CustomPropertyEditorRegistry.INSTANCE.register(SeasonalFilterOption[].class, new SeasonalFilterPropertyEditor());
            CustomPropertyRendererFactory.INSTANCE.getRegistry().registerRenderer(SeasonalFilterOption[].class, new ArrayRenderer());
        }

        @Override
        public void close() {
            CustomPropertyEditorRegistry.INSTANCE.unregister(SeasonalFilterOption[].class);
            CustomPropertyEditorRegistry.INSTANCE.unregister(SeasonalFilterOption[].class);
        }
    }

}
