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
package ec.nbdemetra.ui.variables.actions;

import com.google.common.base.Converter;
import com.google.common.collect.ImmutableList;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.interchange.ImportAction;
import ec.nbdemetra.ui.interchange.Importable;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ui.variables.VariablesDocumentManager;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.utilities.NameManager;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "Edit", id = "ec.nbdemetra.ui.variables.actions.ImportVariablesAction")
@ActionRegistration(displayName = "#CTL_ImportVariablesAction", lazy = false)
@ActionReferences({
    @ActionReference(path = VariablesDocumentManager.PATH, position = 1430)
})
@Messages("CTL_ImportVariablesAction=Import from")
public final class ImportVariablesAction extends SingleNodeAction<ItemWsNode> implements Presenter.Popup {

    private static final Converter<Config, TsVariables> CONVERTER = new VariablesConfig().reverse();
    private static final ImmutableList<Importable> IMPORTABLES = ImmutableList.<Importable>of(new ImportableVariables());

    public ImportVariablesAction() {
        super(ItemWsNode.class);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = ImportAction.getPopupPresenter(IMPORTABLES);
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
            TsVariables value = CONVERTER.convert(config);
            if (value == null || value.isEmpty()) {
                return;
            }
            Workspace ws = WorkspaceFactory.getInstance().getActiveWorkspace();
            VariablesDocumentManager mgr = WorkspaceFactory.getInstance().getManager(VariablesDocumentManager.class);
            WorkspaceItem<TsVariables> item = mgr.create(ws);
            TsVariables element = item.getElement();
            String[] all = value.unlockedNames();
            if (all != null) {
                for (String s : all) {
                    element.set(s, value.get(s));
                }
            }
            // add the variables into the active processingcontext
            NameManager<TsVariables> vars = ProcessingContext.getActiveContext().getTsVariableManagers();
            String name = config.getName();
            if (!name.equals(item.getDisplayName()) && !vars.contains(name)) {
                vars.rename(item.getDisplayName(), name);
                item.setDisplayName(name);
            }
        }
    }
}
