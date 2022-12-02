/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.util;

import demetra.desktop.Config;

import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbPreferences;

/**
 *
 * @author Philippe Charles
 */
public abstract class InstallerStep {

    public void restore() {
    }

    public void close() {
    }

    public InstallerStep and(InstallerStep... steps) {
        switch (steps.length) {
            case 0:
                return this;
            case 1:
                return new AllInstallerStep(List.of(this, steps[0]));
            default:
                List<InstallerStep> result = new ArrayList<>();
                result.add(this);
                Collections.addAll(result, steps);
                return new AllInstallerStep(result);
        }
    }

    public Preferences prefs() {
        return NbPreferences.forModule(getClass());
    }
    //
    public static final InstallerStep EMPTY = new EmptyInstallerStep();

    public static InstallerStep all(InstallerStep... steps) {
        return all(List.copyOf(Arrays.asList(steps)));
    }

    public static InstallerStep all(List<? extends InstallerStep> steps) {
        return steps.isEmpty() ? EMPTY : new AllInstallerStep(steps);
    }

    public abstract static class LookupStep<T> extends InstallerStep implements LookupListener {

        private final Class<T> clazz;
        private org.openide.util.Lookup.Result<T> lookup;

        public LookupStep(Class<T> clazz) {
            this.clazz = clazz;
        }

        protected Class<T> getLookupClass() {
            return clazz;
        }

        @Override
        public final void restore() {
            lookup = org.openide.util.Lookup.getDefault().lookupResult(clazz);
            lookup.addLookupListener(this);
            onRestore(lookup);
        }

        @Override
        public final void close() {
            onClose(lookup);
            // TODO: find out why it is sometimes null here
            if (lookup != null) {
                lookup.removeLookupListener(this);
                lookup = null;
            }
        }

        @Override
        public final void resultChanged(LookupEvent le) {
            if (le.getSource().equals(lookup)) {
                onResultChanged(lookup);
            }
        }

        protected void onResultChanged(org.openide.util.Lookup.Result<T> lookup) {
        }

        protected void onRestore(org.openide.util.Lookup.Result<T> lookup) {
        }

        protected void onClose(org.openide.util.Lookup.Result<T> lookup) {
        }
    }

    public static <X> Optional<X> tryGet(Preferences prefs, String key, Parser<X> parser) {
        String stringValue = prefs.get(key, null);
        if (stringValue == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(parser.parse(stringValue));
    }

    public static <X> boolean tryPut(Preferences prefs, String key, Formatter<X> formatter, X value) {
        CharSequence stringValue = formatter.format(value);
        if (stringValue == null) {
            return false;
        }
        prefs.put(key, stringValue.toString());
        return true;
    }

    public static void put(Preferences prefs, Config config) {
        prefs.put("domain", config.getDomain());
        prefs.put("name", config.getName());
        prefs.put("version", config.getVersion());
        config.getParameters().forEach((k, v) -> prefs.put(k, v));
    }

    public static Optional<Config> tryGet(Preferences prefs) {
        String domain = prefs.get("domain", "");
        String name = prefs.get("name", "");
        String version = prefs.get("version", "");
        if (domain.isEmpty() || name.isEmpty() || version.isEmpty()) {
            return Optional.empty();
        }
        String[] keys;
        try {
            keys = prefs.keys();
        } catch (BackingStoreException ex) {
            keys = null;
        }
        Config.Builder builder = Config.builder(domain, name, version);
        if (keys != null) {
            for (String key : keys) {
                if (!key.equals("domain") && !key.equals("name") && !key.equals("version")) {
                    builder.parameter(key, prefs.get(key, ""));
                }
            }
        }
        return Optional.of(builder.build());
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static final class AllInstallerStep extends InstallerStep {

        private final List<? extends InstallerStep> steps;

        public AllInstallerStep(List<? extends InstallerStep> steps) {
            this.steps = steps;
        }

        @Override
        public void restore() {
            for (InstallerStep o : steps) {
                o.restore();
            }
        }

        @Override
        public void close() {
            for (int i = steps.size(); i-- > 0; ) {
                steps.get(i).close();
            }
        }
    }

    private static final class EmptyInstallerStep extends InstallerStep {

        @Override
        public InstallerStep and(InstallerStep... steps) {
            switch (steps.length) {
                case 0:
                    return this;
                case 1:
                    return new AllInstallerStep(List.of(steps[0]));
                default:
                    return new AllInstallerStep(List.copyOf(Arrays.asList(steps)));
            }
        }
    }
    //</editor-fold>
}
