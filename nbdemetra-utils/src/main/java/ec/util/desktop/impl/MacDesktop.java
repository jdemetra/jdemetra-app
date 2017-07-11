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
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openide.util.lookup.ServiceProvider;

/**
 * A generic {@link Desktop} implementation for Mac OS X.
 *
 * @author Philippe Charles
 */
public class MacDesktop extends AwtDesktop {

    //<editor-fold defaultstate="collapsed" desc="Resources">
    /*
     * http://www.declaresub.com/wiki/index.php/Mac_OS_X_basics_for_Windows_and_Linux_users
     */
    static final String DESKTOP_DIR = "Desktop";
    static final String DOCUMENTS_DIR = "Documents";
    static final String DOWNLOAD_DIR = "Downloads";
    static final String MUSIC_DIR = "Music";
    static final String PICTURES_DIR = "Pictures";
    static final String PUBLICSHARE_DIR = "Public";
    static final String VIDEOS_DIR = "Movies";
    //</editor-fold>

    @Nonnull
    private final ZSystem system;

    // VisibleForTesting
    MacDesktop(ZSystem system) {
        this.system = system;
    }

    @Override
    public boolean isSupported(Action action) {
        switch (action) {
            case SHOW_IN_FOLDER:
                return true;
            case SEARCH:
                return true;
        }
        return super.isSupported(action);
    }

    @Override
    public void showInFolder(File file) throws IOException {
        Util.checkFileValidation(file);
        // https://developer.apple.com/library/mac/#documentation/Darwin/Reference/ManPages/man1/open.1.html
        system.exec("open", "-R", file.getAbsolutePath());
    }

    @Override
    public File getKnownFolder(Desktop.KnownFolder userDir) {
        switch (userDir) {
            case DESKTOP:
                return getKnownFolderByName(system, DESKTOP_DIR);
            case DOCUMENTS:
                return getKnownFolderByName(system, DOCUMENTS_DIR);
            case DOWNLOAD:
                return getKnownFolderByName(system, DOWNLOAD_DIR);
            case MUSIC:
                return getKnownFolderByName(system, MUSIC_DIR);
            case PICTURES:
                return getKnownFolderByName(system, PICTURES_DIR);
            case PUBLICSHARE:
                return getKnownFolderByName(system, PUBLICSHARE_DIR);
            case TEMPLATES:
                return null;
            case VIDEOS:
                return getKnownFolderByName(system, VIDEOS_DIR);
        }
        return null;
    }

    @Override
    public File[] search(String query) throws IOException {
        // http://macdevcenter.com/pub/a/mac/2006/01/04/mdfind.html
        // https://developer.apple.com/library/mac/documentation/Darwin/Reference/ManPages/man1/mdfind.1.html
        String quotedQuery = "\"" + query.replace("\"", "") + "\"";
        Process p = system.exec("mdfind", quotedQuery);
        return Util.toFiles(p, Charset.defaultCharset());
    }

    @ServiceProvider(service = Desktop.Factory.class)
    public static class Factory implements Desktop.Factory {

        @Override
        public Desktop.Factory.SupportType getSupportType(String osArch, String osName, String osVersion) {
            return osName.equals("Mac OS X") || osName.startsWith("Darwin") ? Desktop.Factory.SupportType.GENERIC : Desktop.Factory.SupportType.NONE;
        }

        @Override
        public Desktop create(String osArch, String osName, String osVersion) {
            return new MacDesktop(ZSystem.getDefault());
        }
    }

    @Nullable
    private static File getKnownFolderByName(@Nonnull ZSystem system, @Nonnull String osxFolderName) {
        File result = new File(system.getProperty("user.home"), osxFolderName);
        return result.exists() ? result : null;
    }
}
