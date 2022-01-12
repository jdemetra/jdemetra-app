/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.desktop.util.NetBeansServiceBackend;
import demetra.processing.ProcDocument;
import demetra.util.Id;
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
public interface IProcDocumentItemFactory {

    @ServiceSorter
    int getPosition();

    @NonNull
    Class<? extends ProcDocument> getDocumentType();

    @NonNull
    Id getItemId();

    @NonNull
    JComponent getView(@NonNull ProcDocument doc) throws IllegalArgumentException;

    @Nullable
    default Icon getIcon() {
        return null;
    }

    @Nullable
    default Action[] getActions() {
        return null;
    }
}
