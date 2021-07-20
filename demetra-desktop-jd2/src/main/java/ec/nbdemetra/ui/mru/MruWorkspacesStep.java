/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.mru;

import ec.nbdemetra.core.InstallerStep;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author Philippe Charles
 */
public class MruWorkspacesStep extends InstallerStep {

    final Preferences prefsWs = NbPreferences.forModule(MruWorkspacesStep.class).node("MruWs");

    @Override
    public void restore() {
        MruPreferences.INSTANCE.load(prefsWs, MruList.getWorkspacesInstance());
    }

    @Override
    public void close() {
        MruPreferences.INSTANCE.store(prefsWs, MruList.getWorkspacesInstance());
    }
}
