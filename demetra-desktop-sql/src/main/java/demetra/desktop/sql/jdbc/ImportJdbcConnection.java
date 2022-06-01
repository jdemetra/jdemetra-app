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
import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.interchange.Importable;
import demetra.desktop.interchange.InterchangeManager;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(category = "Edit", id = "demetra.desktop.sql.jdbc.ImportJdbcConnection")
@ActionRegistration(displayName = "#CTL_ImportJdbcConnection", lazy = false)
@ActionReferences({
    @ActionReference(path = "Databases/Explorer/Root/Actions", position = 155, separatorAfter = 170)
})
@Messages("CTL_ImportJdbcConnection=Import from")
public final class ImportJdbcConnection extends SingleNodeAction<Node> implements Presenter.Popup {

    public ImportJdbcConnection() {
        super(Node.class);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = InterchangeManager.get().newImportMenu(getImportables());
        result.setText(Bundle.CTL_ImportJdbcConnection());
        return result;
    }

    @Override
    protected boolean enable(Node activatedNode) {
        return true;
    }

    @Override
    protected void performAction(Node activatedNode) {
    }

    @Override
    public String getName() {
        return null;
    }

    private List<Importable> getImportables() {
        return Collections.singletonList(new Importable() {

            @Override
            public String getDomain() {
                return DriverBasedConfig.class.getName();
            }

            @Override
            public void importConfig(Config config) throws IllegalArgumentException {
                DriverBasedConfig bean = fromConfig(config);
                DbExplorerUtil.importConnection(bean);
            }
        });
    }

    @NonNull
    private static DriverBasedConfig fromConfig(@NonNull Config config) throws IllegalArgumentException {
        if (!DriverBasedConfig.class.getName().equals(config.getDomain())) {
            throw new IllegalArgumentException("Invalid config");
        }
        DriverBasedConfig.Builder result = DriverBasedConfig.builder(config.getParameter("driverClass"), config.getParameter("databaseUrl"), config.getParameter("schema"), config.getName());
        config.getParameters().entrySet().stream()
                .filter(o -> o.getKey().startsWith("prop_"))
                .forEach(o -> result.param(o.getKey().substring(5), o.getValue()));
        return result.build();
    }
}
