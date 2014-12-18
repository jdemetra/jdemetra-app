/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.actions;

import ec.nbdemetra.ui.ActiveViewManager;
import ec.tstoolkit.utilities.Id;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.openide.util.*;
import org.openide.windows.TopComponent;

/**
 *
 * @author pcuser
 */
public abstract class AbstractIdAction2<T extends TopComponent> extends AbstractAction implements LookupListener {

    protected final Lookup context;
    protected Lookup.Result<Id> all;
    private final Class<T> tclass;

    public AbstractIdAction2(Class<T> tclass) {
        this.tclass = tclass;
        context = Utilities.actionsGlobalContext();
    }

    protected AbstractIdAction2(Class<T> tclass, Lookup context) {
        this.tclass = tclass;
        this.context = context;
    }

    protected void init() {
        assert SwingUtilities.isEventDispatchThread() : "this shall be called just from AWT thread";

        if (all == null) {

            //The thing we want to listen for the presence or absence of
            //on the global selection
            all = context.lookupResult(Id.class);
            all.addLookupListener(this);
            resultChanged(null);
        }
        initAction();
    }

    protected T content() {
//        Collection<? extends Id> allInstances = all.allInstances();
//        if (allInstances.isEmpty()) {
//            return null;
//        }
//        else {
//            for (Id id : allInstances) {
//                T search = ActiveViewManager.getInstance().search(id, tclass);
                return ActiveViewManager.getInstance().getLookup().lookup(tclass);
//                if (search != null) {
//                    return search;
//                }
//            }
//            return null;
//        }

    }

    protected abstract void initAction();

    protected abstract void process(T cur);

    @Override
    public void resultChanged(LookupEvent ev) {
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        init();
        T cur = content();
        if (cur != null) {
            process(cur);
        }
    }
}
