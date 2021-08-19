/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import demetra.ui.util.NetBeansServiceBackend;
import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.utilities.Id;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import nbbrd.service.ServiceSorter;

/**
 *
 * @author Philippe Charles
 */
@ServiceDefinition(
        quantifier = Quantifier.MULTIPLE,
        backend = NetBeansServiceBackend.class,
        singleton = true
)
public interface ProcDocumentItemFactory {

    @ServiceSorter
    int getPosition();

    @NonNull
    Class<? extends IProcDocument> getDocumentType();

    @NonNull
    Id getItemId();

    @NonNull
    JComponent getView(@NonNull IProcDocumentView<? extends IProcDocument> host, @NonNull IProcDocument doc) throws IllegalArgumentException;

    @Nullable
    default Icon getIcon() {
        return null;
    }

    @Nullable
    default Action[] getActions() {
        return null;
    }
}
