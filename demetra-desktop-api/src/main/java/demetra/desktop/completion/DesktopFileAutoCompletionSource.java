/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.completion;

import ec.util.completion.FileAutoCompletionSource;
import ec.util.desktop.Desktop;
import ec.util.desktop.DesktopManager;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Philippe Charles
 */
public final class DesktopFileAutoCompletionSource extends FileAutoCompletionSource {

    public DesktopFileAutoCompletionSource(FileFilter fileFilter, File[] paths) {
        super(false, fileFilter, paths);
    }

    Desktop getDesktop() {
        return DesktopManager.get();
    }

    @Override
    public List<File> getValues(String term) throws IOException {
        final List<File> result = super.getValues(term);
        Desktop desktop = getDesktop();
        if (!desktop.isSupported(Desktop.Action.SEARCH) || term.length() < 3) {
            return result;
        }
        final List<File> enhancedResult = new ArrayList<>();
        if (fileFilter == null) {
            Collections.addAll(enhancedResult, desktop.search(term));
        } else {
            for (File o : desktop.search(term)) {
                if (fileFilter.accept(o)) {
                    enhancedResult.add(o);
                }
            }
        }
        if (enhancedResult.isEmpty()) {
            return result;
        }
        if (enhancedResult.addAll(result)) {
            Collections.sort(enhancedResult);
        }
        return enhancedResult;
    }
}
