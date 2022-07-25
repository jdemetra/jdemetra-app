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

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Tools",
id = "ec.nbdemetra.sa.disaggregation.RefreshDataAction")
@ActionRegistration(
    displayName = "#CTL_RefreshDataAction", lazy=false)
@ActionReferences({
    @ActionReference(path = "Shortcuts", name = "D-R"),
    @ActionReference(path = TsDisaggregationModelManager.ITEMPATH, position = 1900)
})
@Messages("CTL_RefreshDataAction=Refresh Data")
public final class RefreshDataAction extends SingleNodeAction<ItemWsNode> {

    public static final String REFRESH_MESSAGE = "Are you sure you want to refresh the data?";

    public RefreshDataAction() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        if (cur.getElement() instanceof MultiTsDocument) {
            MultiTsDocument doc = (MultiTsDocument) cur.getElement();
            if (doc.isTsFrozen()) {
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation(REFRESH_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);
                if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                    return;
                }
                doc.unfreezeTs();
            }
        }
    }

    @Override
    protected boolean enable(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        if (cur.getElement() instanceof MultiTsDocument) {
            MultiTsDocument doc = (MultiTsDocument) cur.getElement();
            return doc.isTsFrozen();
        }
        return false;
    }

    @Override
    public String getName() {
        return Bundle.CTL_RefreshDataAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
