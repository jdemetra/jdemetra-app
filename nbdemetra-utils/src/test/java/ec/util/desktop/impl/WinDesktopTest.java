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

import ec.util.desktop.Desktop;
import static ec.util.desktop.Desktop.Action.SEARCH;
import static ec.util.desktop.Desktop.Action.SHOW_IN_FOLDER;
import static ec.util.desktop.Desktop.KnownFolder.DESKTOP;
import static ec.util.desktop.impl.WinDesktop.DESKTOP_DIR;
import static ec.util.desktop.impl.WinDesktop.DESKTOP_SEARCH_KEY_PATH;
import static ec.util.desktop.impl.WinDesktop.SHELL_FOLDERS_KEY_PATH;
import static ec.util.desktop.impl.WinRegistry.Root.HKEY_CURRENT_USER;
import static ec.util.desktop.impl.WinRegistry.Root.HKEY_LOCAL_MACHINE;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class WinDesktopTest {

    static Input GOOD, BAD, UGLY;

    @BeforeClass
    public static void beforeClass() throws IOException {
        File script = File.createTempFile("search", "");
        script.deleteOnExit();
        GOOD = new Input(FakeRegistry.create(), script, new FakeLauncher(), new FakeScriptHost());
        BAD = new Input(WinRegistry.noOp(), new File("helloworld"), ZSystem.noOp(), WinScriptHost.noOp());
        UGLY = new Input(WinRegistry.failing(), null, ZSystem.failing(), WinScriptHost.failing());
    }

    @Test
    public void testIsSupportedShowInFolder() {
        assertTrue(new WinDesktop(BAD.registry, BAD.script, BAD.system, BAD.wsh).isSupported(SHOW_IN_FOLDER));
    }

    @Test
    public void testIsSupportedSearch() {
        assertFalse(new WinDesktop(BAD.registry, BAD.script, BAD.system, BAD.wsh).isSupported(SEARCH));
        assertFalse(new WinDesktop(BAD.registry, GOOD.script, BAD.system, GOOD.wsh).isSupported(SEARCH));
        assertFalse(new WinDesktop(GOOD.registry, BAD.script, BAD.system, BAD.wsh).isSupported(SEARCH));
        assertTrue(new WinDesktop(GOOD.registry, GOOD.script, BAD.system, GOOD.wsh).isSupported(SEARCH));
    }

    @Test()
    public void testShowInfolder1() throws IOException {
        new WinDesktop(BAD.registry, BAD.script, GOOD.system, BAD.wsh).showInFolder(GOOD.script);
    }

    @Test()
    public void testShowInfolder2() throws IOException {
        new WinDesktop(BAD.registry, BAD.script, BAD.system, BAD.wsh).showInFolder(GOOD.script);
    }

    @Test(expected = IOException.class)
    public void testShowInfolder3() throws IOException {
        new WinDesktop(BAD.registry, BAD.script, UGLY.system, BAD.wsh).showInFolder(GOOD.script);
    }

    @Test
    public void testGetKnownFolder() {
        for (Desktop.KnownFolder o : Desktop.KnownFolder.values()) {
            assertNull(new WinDesktop(BAD.registry, BAD.script, BAD.system, BAD.wsh).getKnownFolder(o));
        }
        assertEquals(new File("hello"), new WinDesktop(GOOD.registry, BAD.script, BAD.system, BAD.wsh).getKnownFolder(DESKTOP));
        assertNull(new WinDesktop(UGLY.registry, BAD.script, BAD.system, BAD.wsh).getKnownFolder(DESKTOP));
    }

    @Test
    public void testSearch1() throws IOException {
        assertArrayEquals(new File[]{new File("hello.html")}, new WinDesktop(GOOD.registry, GOOD.script, GOOD.system, GOOD.wsh).search("hello"));
    }

    @Test
    public void testSearch2() throws IOException {
        assertFalse(new WinDesktop(GOOD.registry, GOOD.script, BAD.system, BAD.wsh).isSupported(SEARCH));
    }

    @Test(expected = IOException.class)
    public void testSearch3() throws IOException {
        new WinDesktop(GOOD.registry, GOOD.script, UGLY.system, UGLY.wsh).search("hello");
    }

    //<editor-fold defaultstate="collapsed" desc="Details">
    private static final class Input {

        final WinRegistry registry;
        final File script;
        final ZSystem system;
        final WinScriptHost wsh;

        public Input(WinRegistry registry, File script, ZSystem launcher, WinScriptHost wsh) {
            this.registry = registry;
            this.script = script;
            this.system = launcher;
            this.wsh = wsh;
        }
    }

    private static final class FakeRegistry extends WinRegistry {

        static FakeRegistry create() {
            return new FakeRegistry().putKey(HKEY_LOCAL_MACHINE, DESKTOP_SEARCH_KEY_PATH)
                    .putStringValue(HKEY_CURRENT_USER, SHELL_FOLDERS_KEY_PATH, DESKTOP_DIR, "hello");
        }

        private final Map<Root, Map<String, Map<String, String>>> data;

        public FakeRegistry() {
            this.data = new HashMap<>();
            for (WinRegistry.Root o : WinRegistry.Root.values()) {
                data.put(o, new HashMap<String, Map<String, String>>());
            }
        }

        public FakeRegistry putKey(Root root, String key) {
            data.get(root).put(key, new HashMap<String, String>());
            return this;
        }

        public FakeRegistry putStringValue(Root root, String key, String value, String xyz) {
            Map<String, String> map = data.get(root).get(key);
            if (map == null) {
                map = new HashMap<>();
                data.get(root).put(key, map);
            }
            map.put(value, xyz);
            return this;
        }

        @Override
        public boolean keyExists(Root root, String key) throws IOException {
            return data.get(root).containsKey(key);
        }

        @Override
        public Object getValue(Root root, String key, String value) throws IOException {
            return data.get(root).get(key).get(value);
        }

        @Override
        public SortedMap<String, Object> getValues(Root root, String key) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static final class FakeLauncher extends ZSystem {

        @Override
        public String getProperty(String key) throws SecurityException, IllegalArgumentException, NullPointerException {
            return null;
        }

        @Override
        public Process exec(String... cmdArray) throws IOException {
            return new Process() {

                @Override
                public OutputStream getOutputStream() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public InputStream getInputStream() {
                    return new ByteArrayInputStream("hello.html".getBytes(StandardCharsets.UTF_8));
                }

                @Override
                public InputStream getErrorStream() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public int waitFor() throws InterruptedException {
                    return 0;
                }

                @Override
                public int exitValue() {
                    return 0;
                }

                @Override
                public void destroy() {
                }
            };
        }
    }

    private static final class FakeScriptHost extends WinScriptHost {

        private final FakeLauncher fakeLauncher = new FakeLauncher();

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
            return fakeLauncher.exec(args);
        }

        @Override
        public Process exec(String script, String language, String... args) throws IOException {
            return fakeLauncher.exec(args);
        }
    }
    //</editor-fold>
}
