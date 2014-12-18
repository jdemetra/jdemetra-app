/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tsaction;

import ec.nbdemetra.ui.ns.AbstractNamedService;
import ec.tss.Ts;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = ITsAction.class)
public class NullTsAction extends AbstractNamedService implements ITsAction {

    public static final String NAME = "NullTsAction";

    public NullTsAction() {
        super(ITsAction.class, NAME);
    }
    
    @Override
    public void open(Ts ts) {
    }

    @Override
    public String getDisplayName() {
        return "Do nothing";
    }
}
