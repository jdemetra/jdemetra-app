/*
 * Copyright 2015 National Bank of Belgium
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
final class Util {

    private Util() {
        // static class
    }

    public static boolean isClassAvailable(@Nonnull String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public static boolean is64bit() {
        return "amd64".equals(System.getProperty("os.arch"));
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
    public static File checkFile(File file) throws NullPointerException, IllegalArgumentException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("The file: " + file.getPath() + " doesn't exist.");
        }
        file.canRead();
        return file;
    }

    @Nonnull
    public static File extractResource(@Nonnull String resourceName, @Nonnull String filePrefix, @Nonnull String fileSuffix) throws IOException {
        File result = File.createTempFile(filePrefix, fileSuffix);
        result.deleteOnExit();
        try (InputStream in = AwtDesktop.class.getResourceAsStream(resourceName)) {
            Files.copy(in, result.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return result;
    }

    @Nonnull
    public static File[] toFiles(@Nonnull Process p, @Nonnull Charset charset) throws IOException {
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
}
