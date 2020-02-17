/*
 * Copyright 2018 National Bank of Belgium
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
package ec.nbdemetra.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.logging.Level;
import nbbrd.io.Resource;

/**
 * A bug prevents NetBeans module to auto-load package-info classes under
 * Java9+.
 *
 * @author Philippe Charles
 */
@lombok.extern.java.Log
@lombok.experimental.UtilityClass
class PackageInfoFix {

    public void doLoadPackageInfos(ClassLoader classLoader) {
        try {
            Enumeration<URL> urls = classLoader.getResources("META-INF/MANIFEST.MF");
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Resource.process(url.toURI(), o -> loadPackageInfos(classLoader, getRootOfManifest(o)));
            }
        } catch (IOException | URISyntaxException | UncheckedIOException ex) {
            log.log(Level.WARNING, "While loading package infos", ex);
        }
    }

    private Path getRootOfManifest(Path manifest) {
        return manifest.getParent().getParent();
    }

    private void loadPackageInfos(ClassLoader classLoader, Path root) throws IOException {
        Files
                .walk(root)
                .filter(o -> isPackageInfo(o))
                .map(o -> getClassName(root, o))
                .forEach(o -> loadClass(classLoader, o));
    }

    private boolean isPackageInfo(Path file) {
        return Files.isReadable(file) && file.endsWith("package-info.class");
    }

    private String getClassName(Path root, Path file) {
        return root
                .relativize(file)
                .toString()
                .replace(file.getFileSystem().getSeparator(), ".")
                .replace(".class", "");
    }

    private void loadClass(ClassLoader classLoader, String className) {
        try {
            classLoader.loadClass(className);
        } catch (ClassNotFoundException ex) {
            log.log(Level.WARNING, "Cannot load class '" + className + "'", ex);
        }
    }
}
