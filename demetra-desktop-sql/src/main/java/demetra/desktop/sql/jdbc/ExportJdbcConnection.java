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

import demetra.desktop.Config;
import demetra.desktop.interchange.Exportable;
import demetra.desktop.interchange.Interchange;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.Presenter;

@ActionID(category = "Edit", id = "demetra.desktop.sql.jdbc.ExportJdbcConnection")
@ActionRegistration(displayName = "#CTL_ExportJdbcConnection", lazy = false)
@ActionReferences({
    @ActionReference(path = "Databases/Explorer/Connection/Actions", position = 470)
})
@Messages("CTL_ExportJdbcConnection=Export to")
public final class ExportJdbcConnection extends NodeAction implements Presenter.Popup {

    public ExportJdbcConnection() {
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = Interchange.getDefault().newExportMenu(getExportables(getActivatedNodes()));
        result.setText(Bundle.CTL_ExportJdbcConnection());
        return result;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    private static List<Exportable> getExportables(Node[] activatedNodes) {
        List<Exportable> result = new ArrayList<>();
        for (final Node o : activatedNodes) {
            result.add(() -> toConfig(getConnectionBean(o)));
        }
        return result;
    }

    private static DriverBasedConfig getConnectionBean(Node activatedNode) {
        return DbExplorerUtil.exportConnection(DbExplorerUtil.findConnection(activatedNode).get());
    }

    @NonNull
    private static Config toConfig(DriverBasedConfig conn) {
        Config.Builder result = Config.builder(DriverBasedConfig.class.getName(), conn.getDisplayName(), "")
                .parameter("driverClass", conn.getDriverClass())
                .parameter("databaseUrl", conn.getDatabaseUrl())
                .parameter("schema", conn.getSchema());
        conn.getParams().forEach((k, v) -> result.parameter("prop_" + k, v));
        return result.build();
    }
}
