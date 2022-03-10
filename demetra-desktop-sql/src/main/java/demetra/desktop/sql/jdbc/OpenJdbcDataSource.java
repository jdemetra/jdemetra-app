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
package demetra.desktop.sql.jdbc;

import demetra.desktop.beans.BeanEditor;
import static demetra.desktop.sql.jdbc.DbExplorerUtil.findConnection;
import static demetra.desktop.sql.jdbc.DbExplorerUtil.isTableOrView;
import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.tsproviders.DataSourceProviderBuddySupport;
import demetra.sql.jdbc.JdbcBean;
import demetra.sql.jdbc.JdbcProvider;
import java.beans.IntrospectionException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.slf4j.LoggerFactory;

@ActionID(category = "Edit", id = "demetra.desktop.sql.jdbc.OpenJdbcDataSource")
@ActionRegistration(displayName = "#CTL_OpenJdbcDataSource", lazy = false)
@ActionReferences({
    @ActionReference(path = "Databases/Explorer/Connection/Actions", position = 1, separatorAfter = 10),
    @ActionReference(path = "Databases/Explorer/Table/Actions", position = 1, separatorAfter = 10),
    @ActionReference(path = "Databases/Explorer/View/Actions", position = 1, separatorAfter = 10)
})
@Messages("CTL_OpenJdbcDataSource=Open as JDemetra+ DataSource")
public final class OpenJdbcDataSource extends SingleNodeAction<Node> {

    private final JdbcProvider provider;

    public OpenJdbcDataSource() {
        super(Node.class);
        this.provider = Lookup.getDefault().lookup(JdbcProvider.class);
    }

    @Override
    protected void performAction(Node activatedNode) {
        JdbcBean bean = provider.newBean();
        preFillBean(bean, activatedNode);
        BeanEditor editor = DataSourceProviderBuddySupport.getDefault().getBeanEditor(provider.getSource(), "Open data source");
        try {
            if (editor.editBean(bean)) {
                provider.open(provider.encodeBean(bean));
            }
        } catch (IntrospectionException ex) {
            LoggerFactory.getLogger(OpenJdbcDataSource.class).error("While opening", ex);
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
        return Bundle.CTL_OpenJdbcDataSource();
    }

    static void preFillBean(JdbcBean bean, Node node) {
        findConnection(node)
                .ifPresent(o -> bean.setDatabase(o.getDisplayName()));
        if (isTableOrView(node)) {
            bean.setTable(node.getName());
        }
    }
}
