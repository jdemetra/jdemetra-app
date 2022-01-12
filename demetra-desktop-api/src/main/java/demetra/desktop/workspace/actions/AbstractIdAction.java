/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.*;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jean Palate
 */
public abstract class AbstractIdAction<T extends TopComponent> extends AbstractAction implements ContextAwareAction {

    protected final T topComponent;

    public AbstractIdAction(T topComponent) {
        this.topComponent = topComponent;
    }

    protected T content() {
        return topComponent;
    }

    protected abstract void initAction();

    protected abstract void process(T cur);

 
    @Override
    public void actionPerformed(ActionEvent ev) {
        initAction();
        if (topComponent != null) {
            process(topComponent);
        }
    }
}
