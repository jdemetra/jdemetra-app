/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.ui.nodes;

import java.util.List;
import org.openide.nodes.ChildFactory;

/**
 *
 * @author Philippe Charles
 */
public abstract class BasicChildFactory<T> extends ChildFactory.Detachable<T> {

    protected Exception exception;

    public BasicChildFactory() {
        this.exception = null;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    protected boolean createKeys(List<T> list) {
        exception = null;
        try {
            tryCreateKeys(list);
        } catch (Exception ex) {
            exception = ex;
        }
        return true;
    }

    abstract protected void tryCreateKeys(List<T> list) throws Exception;
}
