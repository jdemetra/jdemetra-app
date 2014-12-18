/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tsaction;

import com.google.common.collect.Iterables;
import ec.nbdemetra.ui.ns.AbstractNamedService;
import ec.tss.Ts;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = ITsAction.class)
public class TsViewsTsAction extends AbstractNamedService implements ITsAction {

    public static final String NAME = "TsViewsTs";

    public TsViewsTsAction() {
        super(ITsAction.class, NAME);
    }
    
    @Override
    public String getDisplayName() {
        return "All ts views";
    }

    @Override
    public void open(Ts ts) {
        for (ITsView2 o : Iterables.filter(TopComponent.getRegistry().getOpened(), ITsView2.class)) {
            o.setTs(ts);
        }
    }
}