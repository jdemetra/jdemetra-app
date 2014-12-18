/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.core;

import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
public abstract class LookupStep<T> implements IInstallerStep, LookupListener {

    protected final Class<T> clazz;
    protected Lookup.Result<T> lookup;

    public LookupStep(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void restore() {
        lookup = Lookup.getDefault().lookupResult(clazz);
        lookup.addLookupListener(this);
        onRestore();
    }

    @Override
    public void close() {
        onClose();
        // TODO: find out why it is sometimes null here
        if (lookup != null) {
            lookup.removeLookupListener(this);
            lookup = null;
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(lookup)) {
            onResultChanged();
        }
    }

    protected abstract void onResultChanged();

    protected abstract void onRestore();

    protected abstract void onClose();
}
