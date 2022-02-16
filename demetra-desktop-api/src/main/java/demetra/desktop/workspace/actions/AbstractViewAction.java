/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace.actions;

import demetra.desktop.ui.ActiveViewManager;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jean Palate
 * @param <T>
 */
public abstract class AbstractViewAction<T extends TopComponent> extends AbstractAction implements ContextAwareAction {

    private static <T extends TopComponent> T getUI(Class<T> tclass) {
        TopComponent activated = TopComponent.getRegistry().getActivated();
        return tclass.isInstance(activated) ? (T) activated : null;
    }
    protected final Class<T> tclass;

    public AbstractViewAction(Class<T> tclass) {
        this.tclass = tclass;
    }

    protected T context() {
        return getUI(tclass);
    }

    protected abstract void refreshAction();

    protected abstract void process(T cur);

    @Override
    public void actionPerformed(ActionEvent ev) {
        T topComponent = context();
        if (topComponent != null) {
            process(topComponent);
        }
    }

    @Override
    public Action createContextAwareInstance(Lookup lkp) {
        refreshAction();
        return this;
    }
}
