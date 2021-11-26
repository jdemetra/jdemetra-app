/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.processing.ProcDocument;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Jean Palate
 * @param <D>
 */
public interface IProcDocumentViewFactory<D extends ProcDocument> {

    @NonNull
    IProcDocumentView<D> create(@NonNull D document);
}
