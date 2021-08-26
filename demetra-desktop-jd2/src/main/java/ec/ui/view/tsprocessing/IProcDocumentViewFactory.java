/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tstoolkit.algorithm.IProcDocument;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Jean Palate
 */
public interface IProcDocumentViewFactory<D extends IProcDocument> {

    @NonNull
    IProcDocumentView<D> create(@NonNull D document);
}
