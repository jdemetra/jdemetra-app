/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import ec.tss.tsproviders.IFileLoader;
import java.io.File;

/**
 *
 * @author Philippe Charles
 */
public class FileLoaderFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {

    final IFileLoader loader;

    public FileLoaderFileFilter(IFileLoader loader) {
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
