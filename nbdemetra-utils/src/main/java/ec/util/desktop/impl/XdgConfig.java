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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author Philippe Charles
 */
abstract class XdgConfig {

    @Nullable
    abstract public String get(@Nonnull String name);

    @Nonnull
    abstract public Set<String> keySet();

    @Nonnull
    public static XdgConfig getInstance(ZSystem system) throws IOException {
        File configFile = getConfigFile(system);
        if (configFile != null) {
            try (InputStream stream = new FileInputStream(configFile)) {
                return parseConfig(stream, system.getEnv());
            }
        }
        throw new IOException("Config file not found");
    }

    @Nonnull
    public static XdgConfig noOp() {
        return NoOpConfig.INSTANCE;
    }

    @Nullable
    private static File getConfigFile(@Nonnull ZSystem system) {
        // http://www.freedesktop.org/wiki/Software/xdg-user-dirs/
        File result;
        if (isFile(result = newFile(system.getEnv("XDG_CONFIG_HOME"), "user-dirs.dirs"))) {
            return result;
        }
        if (isFile(result = newFile(system.getProperty("user.home"), ".config", "user-dirs.dirs"))) {
            return result;
        }
        return null;
    }

    @Nonnull
    static XdgConfig parseConfig(@Nonnull InputStream stream, @Nonnull Map<String, String> env) throws IOException {
        Map<String, String> result = new HashMap<>();

        Map<String, String> tmp = new HashMap<>();
        try (Scanner s = new Scanner(stream, "UTF-8")) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (!line.startsWith("#")) {
                    int idx = line.indexOf('=');
                    if (idx != -1) {
                        tmp.put(line.substring(0, idx), line.substring(idx));
                    }
                }
            }
        }

        for (Map.Entry<String, String> entry : tmp.entrySet()) {
            String value = entry.getValue().replace("\"", "");
            for (Map.Entry<String, String> o : env.entrySet()) {
                int idx = value.indexOf(o.getKey());
                if (idx != -1) {
                    value = o.getValue() + value.substring(idx + o.getKey().length());
                }
            }
            result.put(entry.getKey(), value);
        }
        return new MapConfig(result);
    }

    @Nullable
    private static File newFile(@Nullable String parent, @Nonnull String... children) {
        if (parent == null || parent.isEmpty()) {
            return null;
        }
        StringBuilder result = new StringBuilder(parent);
        for (String o : children) {
            result.append(File.separatorChar).append(o);
        }
        return new File(result.toString());
    }

    private static boolean isFile(@Nullable File file) {
        return file != null && file.isFile();
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final class MapConfig extends XdgConfig {

        private final Map<String, String> map;

        public MapConfig(Map<String, String> map) {
            this.map = map;
        }

        @Override
        public String get(String name) {
            return map.get(name);
        }

        @Override
        public Set<String> keySet() {
            return map.keySet();
        }
    }

    private static final class NoOpConfig extends XdgConfig {

        public static final NoOpConfig INSTANCE = new NoOpConfig();

        @Override
        public String get(String name) {
            return null;
        }

        @Override
        public Set<String> keySet() {
            return Collections.emptySet();
        }
    }
    //</editor-fold>
}
