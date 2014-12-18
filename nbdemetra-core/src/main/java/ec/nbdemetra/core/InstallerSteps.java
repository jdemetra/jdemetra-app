/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.core;

import com.google.common.base.Optional;
import ec.tss.tsproviders.utils.IFormatter;
import ec.tss.tsproviders.utils.Parsers;
import java.util.prefs.Preferences;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
public final class InstallerSteps {

    private InstallerSteps() {
        // static class
    }

    @Deprecated
    public static void restoreAll(IInstallerStep... steps) {
        for (int i = 0; i < steps.length; i++) {
            steps[i].restore();
        }
    }

    @Deprecated
    public static void closeAll(IInstallerStep... steps) {
        for (int i = steps.length - 1; i >= 0; i--) {
            steps[i].close();
        }
    }

    @Deprecated
    public static <X> Optional<X> tryGet(Preferences prefs, String key, Parsers.Parser<X> parser) {
        return InstallerStep.tryGet(prefs, key, parser);
    }

    @Deprecated
    public static <X> boolean tryPut(Preferences prefs, String key, IFormatter<X> formatter, X value) {
        return InstallerStep.tryPut(prefs, key, formatter, value);
    }
}
