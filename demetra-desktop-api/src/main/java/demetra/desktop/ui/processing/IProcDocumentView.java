/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.processing.ProcDocument;
import demetra.desktop.interfaces.Disposable;
import demetra.util.Id;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * @author Jean Palate
 */
public interface IProcDocumentView<D extends ProcDocument> extends Disposable {

    @NonNull
    D getDocument();

    @NonNull
    List<Id> getItems();

    @Nullable
    JComponent getView(Id path);

    @Nullable
    Icon getIcon(Id path);

    @Nullable
    Action[] getActions(Id path);

    @Nullable
    Id getPreferredView();

    void refresh();
}
