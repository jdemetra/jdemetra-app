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
package ec.nbdemetra.ui.properties;

import demetra.tsprovider.FileLoader;
import java.io.File;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Philippe Charles
 */
public class FileLoaderFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {

    private final FileLoader loader;

    public FileLoaderFileFilter(@NonNull FileLoader loader) {
        this.loader = loader;
    }

    @Override
    public boolean accept(File file) {
        return file.isDirectory() || loader.accept(file);
    }

    @Override
    public String getDescription() {
        return loader.getFileDescription();
    }
}
