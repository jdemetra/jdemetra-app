/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import ec.nbdemetra.core.InstallerStep;
import static ec.nbdemetra.ui.Installer.loadConfig;
import static ec.nbdemetra.ui.Installer.storeConfig;
import java.util.Collections;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    final InstallerStep step = new DemetraSAStep();

    @Override
    public void restored() {
        step.restore();
    }

    @Override
    public void close() {
        step.close();
    }

    static class DemetraSAStep extends InstallerStep {

        @Override
        public void restore() {
            loadConfig(Collections.singleton(DemetraSA.getDefault()), prefs());
        }

        @Override
        public void close() {
            storeConfig(Collections.singleton(DemetraSA.getDefault()), prefs());
        }
    }
}
