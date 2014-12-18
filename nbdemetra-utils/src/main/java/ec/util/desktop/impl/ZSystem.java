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

import java.io.IOException;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Philippe Charles
 */
abstract class ZSystem {

    @Nullable
    abstract public String getProperty(@Nonnull String key);

    @Nonnull
    abstract public Process exec(@Nonnull String... cmdarray) throws IOException;

    // quick&dirty
    public Map<String, String> getEnv() {
        return System.getenv();
    }

    // quick&dirty
    public String getEnv(String name) {
        return System.getenv(name);
    }

    @Nonnull
    public static ZSystem getDefault() {
        return DefaultSystem.INSTANCE;
    }

    @Nonnull
    public static ZSystem noOp() {
        return NoOpSystem.INSTANCE;
    }

    @Nonnull
    public static ZSystem failing() {
        return FailingSystem.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final class DefaultSystem extends ZSystem {

        public static final DefaultSystem INSTANCE = new DefaultSystem();

        @Override
        public String getProperty(String key) {
            return System.getProperty(key);
        }

        @Override
        public Process exec(String... cmdarray) throws IOException {
            return Runtime.getRuntime().exec(cmdarray);
        }
    }

    private static final class NoOpSystem extends ZSystem {

        public static final NoOpSystem INSTANCE = new NoOpSystem();

        @Override
        public String getProperty(String key) {
            return null;
        }

        @Override
        public Process exec(String... cmdarray) throws IOException {
            return Processes.noOp();
        }
    }

    private static final class FailingSystem extends ZSystem {

        public static final FailingSystem INSTANCE = new FailingSystem();

        @Override
        public String getProperty(String key) {
            return "";
        }

        @Override
        public Process exec(String... cmdarray) throws IOException {
            throw new IOException();
        }
    }
    //</editor-fold>
}
