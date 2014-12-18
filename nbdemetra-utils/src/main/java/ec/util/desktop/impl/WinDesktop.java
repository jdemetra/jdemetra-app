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

import ec.util.desktop.Desktop;
import ec.util.desktop.Desktop.Action;
import ec.util.desktop.Desktop.KnownFolder;
import static ec.util.desktop.impl.AwtDesktop.extractResource;
import static ec.util.desktop.impl.WinRegistry.Root.HKEY_CURRENT_USER;
import static ec.util.desktop.impl.WinRegistry.Root.HKEY_LOCAL_MACHINE;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A generic {@link Desktop} implementation for Windows.
 *
 * @author Philippe Charles
 */
public class WinDesktop extends AwtDesktop {

    private static final Logger LOGGER = Logger.getLogger(WinDesktop.class.getName());

    //<editor-fold defaultstate="collapsed" desc="Resources">
    /**
     * http://msdn.microsoft.com/en-us/library/dd378457(v=vs.85).aspx
     */
    static final String SHELL_FOLDERS_KEY_PATH = "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders";
    static final String DESKTOP_SEARCH_KEY_PATH = "Software\\Microsoft\\Windows Desktop Search";
    static final String DESKTOP_DIR = "Desktop";
    static final String DOCUMENTS_DIR = "Personal";
    static final String DOWNLOAD_DIR = "{374DE290-123F-4565-9164-39C4925E467B}";
    static final String MUSIC_DIR = "My Music";
    static final String PICTURES_DIR = "My Pictures";
    static final String PUBLICSHARE_DIR = "{ED4824AF-DCE4-45A8-81E2-FC7965083634}";
    static final String TEMPLATES_DIR = "Templates";
    static final String VIDEOS_DIR = "My Video";
    //
    static final String QUOTE = "\"";
    //</editor-fold>

    @Nonnull
    private final WinRegistry registry;
    @Nonnull
    private final File searchScript;
    @Nonnull
    private final ZSystem system;
    @Nonnull
    private final WinScriptHost wsh;

    // VisibleForTesting
    WinDesktop(WinRegistry registry, File searchScript, ZSystem launcher, WinScriptHost wsh) {
        this.registry = registry;
        this.searchScript = searchScript;
        this.system = launcher;
        this.wsh = wsh;
    }

    @Override
    public boolean isSupported(Action action) {
        switch (action) {
            case SHOW_IN_FOLDER:
                return true;
            case SEARCH:
                return wsh.canExec(searchScript) && isSearchEngineInstalled(registry);
        }
        return super.isSupported(action);
    }

    @Override
    public void showInFolder(File file) throws IOException {
        checkFile(file);
        showInFolder(system, file);
    }

    @Override
    public File getKnownFolder(KnownFolder userDir) {
        switch (userDir) {
            case DESKTOP:
                return getKnownFolderByName(registry, DESKTOP_DIR);
            case DOCUMENTS:
                return getKnownFolderByName(registry, DOCUMENTS_DIR);
            case DOWNLOAD:
                return getKnownFolderByName(registry, DOWNLOAD_DIR);
            case MUSIC:
                return getKnownFolderByName(registry, MUSIC_DIR);
            case PICTURES:
                return getKnownFolderByName(registry, PICTURES_DIR);
            case PUBLICSHARE:
                return getKnownFolderByName(registry, PUBLICSHARE_DIR);
            case TEMPLATES:
                return getKnownFolderByName(registry, TEMPLATES_DIR);
            case VIDEOS:
                return getKnownFolderByName(registry, VIDEOS_DIR);
        }
        return null;
    }

    @Override
    public File[] search(String query) throws IOException {
        if (!isSupported(Action.SEARCH)) {
            throw new UnsupportedOperationException(Action.SEARCH.name());
        }
        return search(wsh, searchScript, query);
    }

    public static class Factory implements Desktop.Factory {

        @Override
        public SupportType getSupportType(String osArch, String osName, String osVersion) {
            return osName.startsWith("Windows ") ? SupportType.GENERIC : SupportType.NONE;
        }

        @Override
        public Desktop create(String osArch, String osName, String osVersion) {
            WinRegistry registry = WinRegistry.getDefault();
            File searchScript = extractSearchScript();
            ZSystem launcher = ZSystem.getDefault();
            WinScriptHost wsh = WinScriptHost.getDefault();
            return new WinDesktop(registry, searchScript, launcher, wsh);
        }
    }

    private static void showInFolder(@Nonnull ZSystem system, @Nonnull File file) throws IOException {
        // http://support.microsoft.com/kb/152457
        system.exec("explorer.exe", "/select,", quote(file.getAbsolutePath()));
    }

    @Nullable
    private static File extractSearchScript() {
        try {
            return extractResource("winsearch.vbs", "winsearch", ".vbs");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot load search script", ex);
            return null;
        }
    }

    private static boolean isSearchEngineInstalled(@Nonnull WinRegistry registry) {
        try {
            return registry.keyExists(HKEY_LOCAL_MACHINE, DESKTOP_SEARCH_KEY_PATH);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "While checking desktop search existence", ex);
            return false;
        }
    }

    @Nonnull
    private static File[] search(@Nonnull WinScriptHost wsh, @Nonnull File searchScript, @Nonnull String query) throws IOException {
        // http://en.wikipedia.org/wiki/Windows_Search
        String quotedQuery = quote(query.replace(QUOTE, ""));
        Process p = wsh.exec(searchScript, quotedQuery);
        return toFiles(p, Charset.defaultCharset());
    }

    @Nullable
    private static File getKnownFolderByName(@Nonnull WinRegistry registry, @Nonnull String winFolderName) {
        try {
            Object result = registry.getValue(HKEY_CURRENT_USER, SHELL_FOLDERS_KEY_PATH, winFolderName);
            return result instanceof String && !((String) result).isEmpty() ? new File((String) result) : null;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "While getting known folder", ex);
            return null;
        }
    }

    @Nonnull
    private static String quote(@Nonnull String input) {
        return QUOTE + input + QUOTE;
    }
}
