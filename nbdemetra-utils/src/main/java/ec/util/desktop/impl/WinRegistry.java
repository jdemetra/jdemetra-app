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
package ec.util.desktop.impl;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import java.io.IOException;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Facade that allows retrieving values from the registry of Windows.
 *
 * @author Philippe Charles
 */
public abstract class WinRegistry {

    public static enum Root {

        HKEY_LOCAL_MACHINE, HKEY_CURRENT_USER
    }

    abstract public boolean keyExists(@Nonnull Root root, @Nonnull String key) throws IOException;

    @Nullable
    abstract public Object getValue(@Nonnull Root root, @Nonnull String key, @Nonnull String name) throws IOException;

    @Nonnull
    abstract public SortedMap<String, Object> getValues(@Nonnull Root root, @Nonnull String key) throws IOException;

    @Nonnull
    public static WinRegistry noOp() {
        return NoOpRegistry.INSTANCE;
    }

    @Nonnull
    public static WinRegistry failing() {
        return FailingRegistry.INSTANCE;
    }

    @Nonnull
    public static WinRegistry getDefault() {
        return LazyHolder.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    /**
     * http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
     */
    private static final class LazyHolder {

        private static final WinRegistry INSTANCE = createInstance();

        private static WinRegistry createInstance() {
            if (Util.isClassAvailable("com.sun.jna.platform.win32.Advapi32Util")) {
                return new JnaRegistry();
            }
            Logger.getLogger(WinRegistry.class.getName()).log(Level.SEVERE, "Cannot load JNA Platform");
            // fallback
            return noOp();
        }
    }

    private static final class NoOpRegistry extends WinRegistry {

        public static final NoOpRegistry INSTANCE = new NoOpRegistry();

        @Override
        public boolean keyExists(Root root, String key) throws IOException {
            return false;
        }

        @Override
        public Object getValue(Root root, String key, String name) throws IOException {
            return null;
        }

        @Override
        public SortedMap<String, Object> getValues(Root root, String key) throws IOException {
            return EMPTY_SORTED_MAP;
        }
    }

    private static final class FailingRegistry extends WinRegistry {

        public static final FailingRegistry INSTANCE = new FailingRegistry();

        @Override
        public boolean keyExists(Root root, String key) throws IOException {
            throw new IOException();
        }

        @Override
        public Object getValue(Root root, String key, String name) throws IOException {
            throw new IOException();
        }

        @Override
        public SortedMap<String, Object> getValues(Root root, String key) throws IOException {
            throw new IOException();
        }
    }

    private static final class JnaRegistry extends WinRegistry {

        @Override
        public boolean keyExists(Root root, String key) throws IOException {
            try {
                return Advapi32Util.registryKeyExists(convert(root), key);
            } catch (Win32Exception | UnsatisfiedLinkError ex) {
                throw new IOException("While checking key existence", ex);
            }
        }

        @Override
        public Object getValue(Root root, String key, String name) throws IOException {
            try {
                WinReg.HKEY hkey = convert(root);
                return Advapi32Util.registryValueExists(hkey, key, name) ? Advapi32Util.registryGetValue(hkey, key, name) : null;
            } catch (Win32Exception | UnsatisfiedLinkError ex) {
                throw new IOException("While getting string value", ex);
            }
        }

        @Override
        public SortedMap<String, Object> getValues(Root root, String key) throws IOException {
            try {
                WinReg.HKEY hkey = convert(root);
                return Advapi32Util.registryKeyExists(hkey, key) ? Advapi32Util.registryGetValues(hkey, key) : EMPTY_SORTED_MAP;
            } catch (Win32Exception | UnsatisfiedLinkError ex) {
                throw new IOException("While getting values", ex);
            }
        }

        private WinReg.HKEY convert(Root root) {
            switch (root) {
                case HKEY_CURRENT_USER:
                    return WinReg.HKEY_CURRENT_USER;
                case HKEY_LOCAL_MACHINE:
                    return WinReg.HKEY_LOCAL_MACHINE;
            }
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static final SortedMap<String, Object> EMPTY_SORTED_MAP = Collections.unmodifiableSortedMap(new TreeMap<String, Object>());
    //</editor-fold>
}
