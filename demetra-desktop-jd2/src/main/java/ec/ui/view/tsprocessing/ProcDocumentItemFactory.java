/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.design.ServiceDefinition;
import ec.tstoolkit.utilities.Id;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;

/**
 *
 * @author Philippe Charles
 */
@ServiceDefinition(hasPosition = true)
public abstract class ProcDocumentItemFactory {

    @NonNull
    abstract public Class<? extends IProcDocument> getDocumentType();

    @NonNull
    abstract public Id getItemId();

    @NonNull
    abstract public JComponent getView(@NonNull IProcDocumentView<? extends IProcDocument> host, @NonNull IProcDocument doc) throws IllegalArgumentException;

    @Nullable
    public Icon getIcon() {
        return null;
    }

    @Nullable
    public Action[] getActions() {
        return null;
    }
}
