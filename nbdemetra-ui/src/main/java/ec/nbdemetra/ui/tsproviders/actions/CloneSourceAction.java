/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tsproviders.actions;

import demetra.ui.TsManager;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ui.tsproviders.DataSourceNode;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceLoader;
import java.beans.IntrospectionException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.slf4j.LoggerFactory;

@ActionID(category = "Edit", id = "ec.nbdemetra.ui.nodes.actions.CloneSourceAction")
@ActionRegistration(displayName = "#CTL_CloneSourceAction", lazy = false)
@Messages("CTL_CloneSourceAction=Clone")
public final class CloneSourceAction extends SingleNodeAction<DataSourceNode> {

    public CloneSourceAction() {
        super(DataSourceNode.class);
    }

    @Override
    protected void performAction(DataSourceNode activatedNode) {
        try {
            clone(activatedNode.getLookup().lookup(DataSource.class));
        } catch (IntrospectionException ex) {
            LoggerFactory.getLogger(CloneSourceAction.class).error("While cloning", ex);
        }
    }

    static DataSource clone(DataSource dataSource) throws IntrospectionException {
        IDataSourceLoader loader = TsManager.getDefault().lookup(IDataSourceLoader.class, dataSource).get();
        final Object bean = loader.decodeBean(dataSource);
        if (DataSourceProviderBuddySupport.getDefault().get(loader).editBean("Clone data source", bean)) {
            DataSource newDataSource = loader.encodeBean(bean);
            return loader.open(newDataSource) ? newDataSource : null;
        }
        return null;
    }

    @Override
    protected boolean enable(DataSourceNode activatedNode) {
        return TsManager.getDefault().lookup(IDataSourceLoader.class, activatedNode.getLookup().lookup(DataSource.class)).isPresent();
    }

    @Override
    public String getName() {
        return Bundle.CTL_CloneSourceAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
