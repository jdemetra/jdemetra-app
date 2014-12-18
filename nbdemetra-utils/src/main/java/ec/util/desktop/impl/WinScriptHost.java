/*
 * Copyright 2013 National Bank of Belgium
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
package ec.util.desktop.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Locale;
import javax.annotation.Nonnull;

/**
 * Facade that allows executing script in Windows Script Host.
 *
 * @author Philippe Charles
 */
public abstract class WinScriptHost {

    public abstract boolean canExec(@Nonnull File script);

    public abstract boolean canExec(@Nonnull String script, @Nonnull String language);

    @Nonnull
    public abstract Process exec(@Nonnull File script, @Nonnull String... args) throws IOException;

    @Nonnull
    public abstract Process exec(@Nonnull String script, @Nonnull String language, @Nonnull String... args) throws IOException;

    @Nonnull
    public static WinScriptHost noOp() {
        return NoOpScriptHost.INSTANCE;
    }

    @Nonnull
    public static WinScriptHost failing() {
        return FailingScriptHost.INSTANCE;
    }

    @Nonnull
    public static WinScriptHost getDefault() {
        return LazyHolder.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    /**
     * http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
     */
    private static final class LazyHolder {

        private static final WinScriptHost INSTANCE = createInstance();

        private static WinScriptHost createInstance() {
            ZSystem system = ZSystem.getDefault();
            return isWindows(system) ? new CScript(system) : noOp();
        }

        private static boolean isWindows(ZSystem system) {
            String osName = system.getProperty("os.name");
            return osName != null && osName.startsWith("Windows ");
        }
    }

    private static final class NoOpScriptHost extends WinScriptHost {

        public static final NoOpScriptHost INSTANCE = new NoOpScriptHost();

        @Override
        public boolean canExec(File script) {
            return false;
        }

        @Override
        public boolean canExec(String script, String language) {
            return false;
        }

        @Override
        public Process exec(File script, String... args) throws IOException {
            return Processes.noOp();
        }

        @Override
        public Process exec(String script, String language, String... args) throws IOException {
            return Processes.noOp();
        }
    }

    private static final class FailingScriptHost extends WinScriptHost {

        public static final FailingScriptHost INSTANCE = new FailingScriptHost();

        @Override
        public boolean canExec(File script) {
            return true;
        }

        @Override
        public boolean canExec(String script, String language) {
            return true;
        }

        @Override
        public Process exec(File script, String... args) throws IOException {
            throw new IOException();
        }

        @Override
        public Process exec(String script, String language, String... args) throws IOException {
            throw new IOException();
        }
    }

    /**
     * http://en.wikipedia.org/wiki/Windows_Script_Host
     */
    private static final class CScript extends WinScriptHost {

        private final ZSystem system;

        //@VisibleForTesting
        CScript(@Nonnull ZSystem system) {
            this.system = system;
        }

        @Override
        public boolean canExec(File script) {
            return script.exists() && script.isFile() && script.canRead()
                    && Language.getByExtension(script) != Language.UNKNOWN;
        }

        @Override
        public boolean canExec(String script, String language) {
            return Language.getByName(language) != Language.UNKNOWN;
        }

        @Override
        public Process exec(File script, String... args) throws IOException {
            // http://technet.microsoft.com/en-us/library/ff920171.aspx
            String[] result = new String[3 + args.length];
            result[0] = "cscript";
            result[1] = "/nologo";
            result[2] = "\"" + script.getAbsolutePath() + "\"";
            System.arraycopy(args, 0, result, 3, args.length);
            return system.exec(result);
        }

        @Override
        public Process exec(String script, String language, String... args) throws IOException {
            File file = File.createTempFile("script", Language.getByName(language).getExtension());
            file.deleteOnExit();
            Files.write(file.toPath(), Collections.singleton(script), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            return exec(file, args);
        }

        private enum Language {

            VBSCRIPT, JSCRIPT, UNKNOWN;

            public String getExtension() throws IOException {
                switch (this) {
                    case VBSCRIPT:
                        return ".vbs";
                    case JSCRIPT:
                        return ".js";
                    default:
                        throw new IOException("Unsupported language");
                }
            }

            public static Language getByName(String input) {
                switch (input) {
                    case "VBScript":
                        return VBSCRIPT;
                    case "JScript":
                        return JSCRIPT;
                    default:
                        return UNKNOWN;
                }
            }

            public static Language getByExtension(File file) {
                String fileName = file.getName().toLowerCase(Locale.ROOT);
                if (fileName.endsWith(".vbs")) {
                    return VBSCRIPT;
                }
                if (fileName.endsWith(".js")) {
                    return JSCRIPT;
                }
                return UNKNOWN;
            }
        }
    }
    //</editor-fold>
}
