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
package demetra.desktop.sa.util;

import demetra.desktop.sa.ui.DemetraSaUI;
import demetra.desktop.util.InstallerStep;
import demetra.desktop.workspace.WorkspaceFactory;
import java.util.prefs.Preferences;
import org.openide.modules.ModuleInstall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Installer extends ModuleInstall {

    final static Logger LOGGER = LoggerFactory.getLogger(Installer.class);

    public static final InstallerStep STEP = InstallerStep.all(
            new DemetraSaOptionsStep()
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

    @Override
    public boolean closing() {
        return WorkspaceFactory.getInstance().closeWorkspace(true);
    }

    private static final class DemetraSaOptionsStep extends InstallerStep {

        final Preferences prefs = prefs().node("options");

        @Override
        public void restore() {
            DemetraSaUI ui = DemetraSaUI.getDefault();
            tryGet(prefs).ifPresent(ui::setConfig);
        }

        @Override
        public void close() {
            DemetraSaUI ui = DemetraSaUI.getDefault();
            put(prefs, ui.getConfig());
        }
    }
}
