/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package demetra.desktop.disaggregation.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "ec.nbdemetra.disaggregation.actions.RefreshAllDataAction")
@ActionRegistration(
        displayName = "#CTL_RefreshAllDataAction")
@ActionReferences({
    @ActionReference(path = TsDisaggregationModelManager.PATH, position = 1600, separatorBefore = 1300),
    @ActionReference(path = "Shortcuts", name = "A")})
@Messages("CTL_RefreshAllDataAction=Refresh all")
public final class RefreshAllDataAction implements ActionListener {

    public static final String REFRESH_MESSAGE = "Are you sure you want to refresh the data?";
    private final WsNode context;

    public RefreshAllDataAction(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        IWorkspaceItemManager mgr = WorkspaceFactory.getInstance().getManager(context.lookup());
        if (mgr != null) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(REFRESH_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            Workspace ws = context.getWorkspace();
            List<WorkspaceItem<TsDisaggregationModelDocument>> docs = ws.searchDocuments(TsDisaggregationModelDocument.class);
            for (WorkspaceItem<TsDisaggregationModelDocument> doc : docs) {
                doc.getElement().unfreezeTs();
            }
        }
    }
}
