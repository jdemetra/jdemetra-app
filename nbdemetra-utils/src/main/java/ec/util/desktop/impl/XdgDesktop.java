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
import ec.util.desktop.Desktop.KnownFolder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openide.util.lookup.ServiceProvider;

/**
 * A generic {@link Desktop} implementation for Linux.<p>
 * It is based on the <a
 * href="http://portland.freedesktop.org/wiki/">Portland</a> project from <a
 * href="http://freedesktop.org/">freedesktop.org</a>.<br>The XDG acronym stands
 * for <i>X Desktop Group</i>, the formerly name of freedesktop.org
 *
 * @author Philippe Charles
 */
public class XdgDesktop extends AwtDesktop {

    private static final Logger LOGGER = Logger.getLogger(XdgDesktop.class.getName());

    //<editor-fold defaultstate="collapsed" desc="Resources">
    static final String DESKTOP_DIR = "XDG_DESKTOP_DIR";
    static final String DOCUMENTS_DIR = "XDG_DOCUMENTS_DIR";
    static final String DOWNLOAD_DIR = "XDG_DOWNLOAD_DIR";
    static final String MUSIC_DIR = "XDG_MUSIC_DIR";
    static final String PICTURES_DIR = "XDG_PICTURES_DIR";
    static final String PUBLICSHARE_DIR = "XDG_PUBLICSHARE_DIR";
    static final String TEMPLATES_DIR = "XDG_TEMPLATES_DIR";
    static final String VIDEOS_DIR = "XDG_VIDEOS_DIR";
    //</editor-fold>

    @Nonnull
    private final ZSystem system;
    @Nonnull
    private final XdgConfig config;

    /**
     * TODO: Script to find executable based on extension of a file:
     * http://askubuntu.com/a/159900
     * <code>'xdg-mime query default inode/directory'</code>
     * https://wiki.archlinux.org/index.php/Xdg-open
     */
    // VisibleForTesting
    XdgDesktop(ZSystem system, XdgConfig config) {
        this.system = system;
        this.config = config;
    }

    @Override
    public boolean isSupported(Desktop.Action action) {
        switch (action) {
            case SHOW_IN_FOLDER:
                return super.isSupported(action);
            case SEARCH:
                return true;
        }
        return super.isSupported(action);
    }

    @Override
    public File getKnownFolder(KnownFolder userDir) {
        switch (userDir) {
            case DESKTOP:
                return getKnownFolderByName(DESKTOP_DIR);
            case DOCUMENTS:
                return getKnownFolderByName(DOCUMENTS_DIR);
            case DOWNLOAD:
                return getKnownFolderByName(DOWNLOAD_DIR);
            case MUSIC:
                return getKnownFolderByName(MUSIC_DIR);
            case PICTURES:
                return getKnownFolderByName(PICTURES_DIR);
            case PUBLICSHARE:
                return getKnownFolderByName(PUBLICSHARE_DIR);
            case TEMPLATES:
                return getKnownFolderByName(TEMPLATES_DIR);
            case VIDEOS:
                return getKnownFolderByName(VIDEOS_DIR);
        }
        return null;
    }

    @Nullable
    private File getKnownFolderByName(@Nonnull String xdgFolderName) {
        String result = system.getEnv(xdgFolderName);
        return result != null && !result.isEmpty() ? new File(result) : new File(config.get(xdgFolderName));
    }

    @Override
    public File[] search(String query) throws IOException {
        // http://projects.gnome.org/tracker/
        // https://live.gnome.org/Tracker/Documentation
        // http://zeitgeist-project.com/
        // http://wiki.zeitgeist-project.com/UsageFromTerminal
        // man locate !!!
        Process p = system.exec("locate", "-i", query);
        return Util.toFiles(p, Charset.defaultCharset());
    }

    @ServiceProvider(service = Desktop.Factory.class)
    public static class Factory implements Desktop.Factory {

        @Override
        public Desktop.Factory.SupportType getSupportType(String osArch, String osName, String osVersion) {
            return osName.endsWith("Linux") ? Desktop.Factory.SupportType.GENERIC : Desktop.Factory.SupportType.NONE;
        }

        @Override
        public Desktop create(String osArch, String osName, String osVersion) {
            ZSystem system = ZSystem.getDefault();
            return new XdgDesktop(system, parseConfigFile(system));
        }
    }

    @Nonnull
    private static XdgConfig parseConfigFile(ZSystem system) {
        try {
            return XdgConfig.getInstance(system);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "While parsing config file", ex);
            return XdgConfig.noOp();
        }
    }
}
