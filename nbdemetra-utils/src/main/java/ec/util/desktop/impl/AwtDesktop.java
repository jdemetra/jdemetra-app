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

import com.sun.jna.platform.FileUtils;
import ec.util.desktop.Desktop;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * A basic {@link Desktop} implementation based on
 * <code>java.awt.Desktop</code>.
 *
 * @author Philippe Charles
 */
public class AwtDesktop implements Desktop {

    private final java.awt.Desktop awt;

    public AwtDesktop() {
        this.awt = java.awt.Desktop.getDesktop();
    }

    @Override
    public boolean isSupported(Desktop.Action action) {
        switch (action) {
            case BROWSE:
                return awt.isSupported(java.awt.Desktop.Action.BROWSE);
            case EDIT:
                return awt.isSupported(java.awt.Desktop.Action.EDIT);
            case MAIL:
                return awt.isSupported(java.awt.Desktop.Action.MAIL);
            case OPEN:
                return awt.isSupported(java.awt.Desktop.Action.OPEN);
            case PRINT:
                return awt.isSupported(java.awt.Desktop.Action.PRINT);
            case SHOW_IN_FOLDER:
                return awt.isSupported(java.awt.Desktop.Action.OPEN);
            case MOVE_TO_TRASH:
                return isJnaPlatformAvailable() && FileUtils.getInstance().hasTrash();
            case SEARCH:
                return false;
        }
        return false;
    }

    @Override
    public void open(File file) throws IOException {
        awt.open(file);
    }

    @Override
    public void edit(File file) throws IOException {
        awt.edit(file);
    }

    @Override
    public void print(File file) throws IOException {
        awt.print(file);
    }

    @Override
    public void browse(URI uri) throws IOException {
        awt.browse(uri);
    }

    @Override
    public void mail() throws IOException {
        awt.mail();
    }

    @Override
    public void mail(URI mailtoURI) throws IOException {
        awt.mail(mailtoURI);
    }

    @Override
    public void showInFolder(File file) throws IOException {
        awt.open(file.isDirectory() ? file : file.getParentFile());
    }

    @Override
    public void moveToTrash(File... files) throws IOException {
        if (!isSupported(Action.MOVE_TO_TRASH)) {
            throw new UnsupportedOperationException(Action.MOVE_TO_TRASH.name());
        }
        FileUtils.getInstance().moveToTrash(files);
    }

    @Override
    public File getKnownFolder(Desktop.KnownFolder userDir) {
        return null;
    }

    @Override
    public File[] search(String query) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static class Factory implements Desktop.Factory {

        @Override
        public Desktop.Factory.SupportType getSupportType(String osArch, String osName, String osVersion) {
            return Desktop.Factory.SupportType.BASIC;
        }

        @Override
        public Desktop create(String osArch, String osName, String osVersion) {
            return new AwtDesktop();
        }
    }

    /**
     * Checks if the file is a valid file and readable.
     *
     * @param file the file to check
     * @return the validated file
     * @throws SecurityException If a security manager exists and its
     * {@link SecurityManager#checkRead(java.lang.String)} method denies read
     * access to the file
     * @throws NullPointerException if file is null
     * @throws IllegalArgumentException if file doesn't exist
     */
    @Nonnull
    protected static File checkFile(File file) throws NullPointerException, IllegalArgumentException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("The file: " + file.getPath() + " doesn't exist.");
        }
        file.canRead();
        return file;
    }

    @Deprecated
    protected static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ex) {
                // do nothing
            }
        }
    }

    @Nonnull
    protected static File extractResource(@Nonnull String resourceName, @Nonnull String filePrefix, @Nonnull String fileSuffix) throws IOException {
        File result = File.createTempFile(filePrefix, fileSuffix);
        result.deleteOnExit();
        try (InputStream in = AwtDesktop.class.getResourceAsStream(resourceName)) {
            Files.copy(in, result.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return result;
    }

    @Nonnull
    protected static File[] toFiles(@Nonnull Process p, @Nonnull Charset charset) throws IOException {
        List<File> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), charset))) {
            // we need the process to end, else we'll get an illegal Thread State Exception
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(new File(line));
            }
            try {
                p.waitFor();
            } catch (InterruptedException ex) {
                // do nothing?
            }
        }

        return result.toArray(new File[result.size()]);
    }

    private static boolean isJnaPlatformAvailable() {
        try {
            Class.forName("com.sun.jna.platform.FileUtils");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
