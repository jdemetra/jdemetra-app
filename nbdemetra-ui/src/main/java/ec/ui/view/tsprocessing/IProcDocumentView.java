/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.utilities.Id;
import ec.ui.interfaces.IDisposable;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public interface IProcDocumentView<D extends IProcDocument> extends IDisposable {

    @Nonnull
    D getDocument();

    @Nonnull
    ITsViewToolkit getToolkit();

    @Nonnull
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
