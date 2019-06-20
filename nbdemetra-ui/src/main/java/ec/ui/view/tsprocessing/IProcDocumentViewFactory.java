/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 * @author Jean Palate
 */
public interface IProcDocumentViewFactory<D extends IProcDocument> {

    @NonNull
    IProcDocumentView<D> create(@NonNull D document);

    @Deprecated
    <I> void register(
            @NonNull Id id,
            @Nullable InformationExtractor<? super D, I> info,
            @NonNull ItemUI<? extends IProcDocumentView<D>, I> ui);

    @Deprecated
    void unregister(@NonNull Id id);
}
