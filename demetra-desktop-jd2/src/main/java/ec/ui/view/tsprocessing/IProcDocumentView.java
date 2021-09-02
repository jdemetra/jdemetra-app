/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.utilities.Id;
import ec.ui.interfaces.IDisposable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * @author Jean Palate
 */
public interface IProcDocumentView<D extends IProcDocument> extends IDisposable {

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
