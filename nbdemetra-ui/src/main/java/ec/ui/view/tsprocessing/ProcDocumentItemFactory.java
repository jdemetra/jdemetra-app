/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.design.ServiceDefinition;
import ec.tstoolkit.utilities.Id;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;

/**
 *
 * @author Philippe Charles
 */
@ServiceDefinition(hasPosition = true)
public abstract class ProcDocumentItemFactory {

    @Nonnull
    abstract public Class<? extends IProcDocument> getDocumentType();

    @Nonnull
    abstract public Id getItemId();

    @Nonnull
    abstract public JComponent getView(@Nonnull IProcDocumentView<? extends IProcDocument> host, @Nonnull IProcDocument doc) throws IllegalArgumentException;

    @Nullable
    public Icon getIcon() {
        return null;
    }

    @Nullable
    public Action[] getActions() {
        return null;
    }
}
