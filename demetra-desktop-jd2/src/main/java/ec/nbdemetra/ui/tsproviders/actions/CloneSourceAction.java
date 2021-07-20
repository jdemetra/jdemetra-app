/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tsproviders.actions;

import demetra.bridge.TsConverter;
import demetra.tsprovider.DataSourceLoader;
import demetra.ui.TsManager;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ui.tsproviders.DataSourceNode;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.tss.tsproviders.DataSource;
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
        demetra.tsprovider.DataSource tmp = TsConverter.toDataSource(dataSource);
        DataSourceLoader loader = TsManager.getDefault().getProvider(DataSourceLoader.class, tmp).get();
        final Object bean = loader.decodeBean(tmp);
        if (DataSourceProviderBuddySupport.getDefault().get(loader).editBean("Clone data source", bean)) {
            demetra.tsprovider.DataSource newDataSource = loader.encodeBean(bean);
            return loader.open(newDataSource) ? TsConverter.fromDataSource(newDataSource) : null;
        }
        return null;
    }

    @Override
    protected boolean enable(DataSourceNode activatedNode) {
        DataSource dataSource = activatedNode.getLookup().lookup(DataSource.class);
        demetra.tsprovider.DataSource tmp = TsConverter.toDataSource(dataSource);
        return TsManager.getDefault().getProvider(DataSourceLoader.class, tmp).isPresent();
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
