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
package demetra.desktop.workspace.nodes;

import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.util.Id;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Edit",
        id = "demetra.desktop.workspace.nodes.RenameAction")
@ActionRegistration(displayName = "#CTL_RenameAction", lazy = false)
@Messages("CTL_RenameAction=Rename...")
public final class RenameAction extends SingleNodeAction<ItemWsNode> {

    public static final String RENAME_TITLE = "Please enter the new name",
            NAME_MESSAGE = "New name:";

    public RenameAction() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        if (cur != null && !cur.isReadOnly()) {
            // create the input dialog
            String oldName = cur.getDisplayName(), newName = null;
            WsName nd = new WsName(cur.getFamily(), NAME_MESSAGE, RENAME_TITLE, oldName);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            newName = nd.getInputText().trim();
            cur.setDisplayName(newName);
            WorkspaceFactory.Event ev = new WorkspaceFactory.Event(cur.getOwner(), cur.getId(), WorkspaceFactory.Event.ITEMRENAMED);
            WorkspaceFactory.getInstance().notifyEvent(ev);
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

class WsName extends NotifyDescriptor.InputLine {

    WsName(final Id id, String title, String text, final String old) {
        super(title, text);
        setInputText(old);
        textField.addKeyListener(new KeyListener() {
                // To handle VK_ENTER !!!
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && ! textField.getInputVerifier().verify(textField)){
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        textField.setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(JComponent input) {
                JTextField txt = (JTextField) input;
                String name = txt.getText().trim();
                if (name.equals(old))
                    return true;
                if (null != WorkspaceFactory.getInstance().getActiveWorkspace().searchDocumentByName(id, name)) {
                    NotifyDescriptor nd = new NotifyDescriptor.Message(name + " is in use. You should choose another name!");
                    DialogDisplayer.getDefault().notify(nd);
                    return false;
                }
                return true;
            }
        });
    }
}
