/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.jdbc;

import static ec.nbdemetra.jdbc.DbExplorerUtil.findConnection;
import static ec.nbdemetra.jdbc.DbExplorerUtil.isTableOrView;
import demetra.desktop.nodes.SingleNodeAction;
import ec.tss.tsproviders.jdbc.JdbcBean;
import ec.tss.tsproviders.jdbc.jndi.JndiJdbcProvider;
import java.beans.IntrospectionException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.slf4j.LoggerFactory;

@ActionID(category = "Edit", id = "ec.nbdemetra.jdbc.OpenJndiJdbcDataSource")
@ActionRegistration(displayName = "#CTL_OpenJndiJdbcDataSource", lazy = false)
@ActionReferences({
    @ActionReference(path = "Databases/Explorer/Connection/Actions", position = 1, separatorAfter = 10),
    @ActionReference(path = "Databases/Explorer/Table/Actions", position = 1, separatorAfter = 10),
    @ActionReference(path = "Databases/Explorer/View/Actions", position = 1, separatorAfter = 10)
})
@Messages("CTL_OpenJndiJdbcDataSource=Open as JDemetra+ DataSource")
public final class OpenJndiJdbcDataSource extends SingleNodeAction<Node> {

    private final JndiJdbcProvider provider;
    private final JndiJdbcProviderBuddy buddy;

    public OpenJndiJdbcDataSource() {
        super(Node.class);
        this.provider = Lookup.getDefault().lookup(JndiJdbcProvider.class);
        this.buddy = Lookup.getDefault().lookup(JndiJdbcProviderBuddy.class);
    }

    @Override
    protected void performAction(Node activatedNode) {
        JdbcBean bean = provider.newBean();
        preFillBean(bean, activatedNode);
        try {
            if (buddy.editBean("Open data source", bean)) {
                provider.open(provider.encodeBean(bean));
            }
        } catch (IntrospectionException ex) {
            LoggerFactory.getLogger(OpenJndiJdbcDataSource.class).error("While opening", ex);
        }
    }

    @Override
    protected boolean enable(Node activatedNode) {
        return findConnection(activatedNode)
                .filter(DbExplorerUtil::isConnected)
                .isPresent();
    }

    @Override
    public String getName() {
        return Bundle.CTL_OpenJndiJdbcDataSource();
    }

    static void preFillBean(JdbcBean bean, Node node) {
        findConnection(node)
                .ifPresent(o -> bean.setDbName(o.getDisplayName()));
        if (isTableOrView(node)) {
            bean.setTableName(node.getName());
        }
    }
}
