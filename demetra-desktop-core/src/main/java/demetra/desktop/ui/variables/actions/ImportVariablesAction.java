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
package demetra.desktop.ui.variables.actions;

import demetra.desktop.Config;
import demetra.desktop.Converter;
import demetra.desktop.interchange.Importable;
import demetra.desktop.interchange.InterchangeManager;
import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.ui.variables.VariablesDocumentManager;
import demetra.desktop.workspace.Workspace;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.timeseries.regression.ModellingContext;
import demetra.timeseries.regression.TsDataSuppliers;
import demetra.util.NameManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

import javax.swing.*;
import java.util.List;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "Edit", id = "demetra.desktop.ui.variables.actions.ImportVariablesAction")
@ActionRegistration(displayName = "#CTL_ImportVariablesAction", lazy = false)
@ActionReferences({
    @ActionReference(path = VariablesDocumentManager.PATH, position = 1430)
})
@Messages("CTL_ImportVariablesAction=Import from")
public final class ImportVariablesAction extends SingleNodeAction<ItemWsNode> implements Presenter.Popup {

    private static final Converter<Config, TsDataSuppliers> CONVERTER = new VariablesConfig().reverse();
    private static final List<Importable> IMPORTABLES = List.of(new ImportableVariables());

    public ImportVariablesAction() {
        super(ItemWsNode.class);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = InterchangeManager.get().newImportMenu(IMPORTABLES);
        result.setText(Bundle.CTL_ImportVariablesAction());
        return result;
    }

    @Override
    protected void performAction(ItemWsNode activatedNode) {
    }

    @Override
    protected boolean enable(ItemWsNode activatedNode) {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }

    private static final class ImportableVariables implements Importable {

        @Override
        public String getDomain() {
            return VariablesConfig.DOMAIN;
        }

        @Override
        public void importConfig(Config config) throws IllegalArgumentException {
            TsDataSuppliers value = CONVERTER.doForward(config);
            if (value == null || value.isEmpty()) {
                return;
            }
            Workspace ws = WorkspaceFactory.getInstance().getActiveWorkspace();
            VariablesDocumentManager mgr = WorkspaceFactory.getInstance().getManager(VariablesDocumentManager.class);
            WorkspaceItem<TsDataSuppliers> item = mgr.create(ws);
            TsDataSuppliers element = item.getElement();
            String[] all = value.unlockedNames();
            if (all != null) {
                for (String s : all) {
                    element.set(s, value.get(s));
                }
            }
            // add the variables into the active processingcontext
            NameManager<TsDataSuppliers> vars = ModellingContext.getActiveContext().getTsVariableManagers();
            String name = config.getName();
            if (!name.equals(item.getDisplayName()) && !vars.contains(name)) {
                vars.rename(item.getDisplayName(), name);
                item.setDisplayName(name);
            }
        }
    }
}
