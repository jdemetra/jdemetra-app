/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.mru;

import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;
import demetra.desktop.util.InstallerStep;

/**
 *
 * @author Philippe Charles
 */
public class MruWorkspacesStep extends InstallerStep {

    final Preferences prefsWs = NbPreferences.forModule(MruWorkspacesStep.class).node("MruWs");

    @Override
    public void restore() {
//        MruPreferences.INSTANCE.load(prefsWs, MruList.getWorkspacesInstance());
    }

    @Override
    public void close() {
//        MruPreferences.INSTANCE.store(prefsWs, MruList.getWorkspacesInstance());
    }
}
