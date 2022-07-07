/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.variables.actions;

import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.ui.variables.VariablesDocumentManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.timeseries.regression.ModellingContext;
import demetra.timeseries.regression.TsDataSuppliers;
import demetra.util.Id;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Edit",
        id = "demetra.desktop.ui.variables.actions.RenameAction")
@ActionRegistration(
        displayName = "#CTL_RenameAction", lazy = false)
@ActionReferences({
    //    @ActionReference(path = "Menu/Edit"),
    @ActionReference(path = VariablesDocumentManager.ITEMPATH, position = 1100)
})
@Messages("CTL_RenameAction=Rename...")
public final class RenameAction extends SingleNodeAction<ItemWsNode> {

    public static final String RENAME_TITLE = "Please enter the new name",
            NAME_MESSAGE = "New name:";

    public RenameAction() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode node) {
        WorkspaceItem<TsDataSuppliers> cur = (WorkspaceItem<TsDataSuppliers>) node.getItem();
        if (cur != null && !cur.isReadOnly()) {
            // create the input dialog
            String oldName = cur.getDisplayName(), newName;
            VarsName nd = new VarsName(cur.getFamily(), NAME_MESSAGE, RENAME_TITLE, oldName);
            nd.addPropertyChangeListener(evt -> {
                if (evt.getPropertyName().equals(NotifyDescriptor.PROP_DETAIL)) {
                }
            });
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            newName = nd.getInputText();
            if (newName.equals(oldName))
                return;
            ModellingContext.getActiveContext().getTsVariableManagers().rename(oldName, newName);
           node.getWorkspace().rename(node.getItem(), newName);
        }
    }

    @Override
    protected boolean enable(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        return cur != null && !cur.isReadOnly();
    }

    @Override
    public String getName() {
        return Bundle.CTL_RenameAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}

class VarsName extends NotifyDescriptor.InputLine {

    VarsName(final Id id, String title, final String text, String input) {
        super(title, text);
        setInputText(input);
        textField.setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(JComponent input) {
                JTextField txt = (JTextField) input;
                String name = txt.getText();
                if (name.equals(text))
                    return true;
                if (ModellingContext.getActiveContext().getTsVariableManagers().contains(name)) {
                    NotifyDescriptor nd = new NotifyDescriptor.Message(name + " is in use. You should choose another name!");
                    DialogDisplayer.getDefault().notify(nd);
                    return false;
                }
                return true;
            }
        });
    }
}
