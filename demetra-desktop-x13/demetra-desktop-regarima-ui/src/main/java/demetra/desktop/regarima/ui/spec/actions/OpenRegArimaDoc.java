/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.ui.spec.actions;

import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.WsNode;
import demetra.desktop.regarima.ui.RegArimaDocumentManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import jdplus.x13.regarima.RegArimaDocument;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "demetra.desktop.regarima.ui.spec.actions.OpenRegArimaDoc")
@ActionRegistration(displayName = "#CTL_OpenRegArimaDoc")
@ActionReferences({
    @ActionReference(path = RegArimaDocumentManager.ITEMPATH, position = 1600, separatorBefore = 1590)
})
@NbBundle.Messages("CTL_OpenRegArimaDoc=Open")
public class OpenRegArimaDoc implements ActionListener {

    private final WsNode context;

    public OpenRegArimaDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<RegArimaDocument> doc = context.getWorkspace().searchDocument(context.lookup(), RegArimaDocument.class);
        RegArimaDocumentManager mgr = WorkspaceFactory.getInstance().getManager(RegArimaDocumentManager.class);
        mgr.openDocument(doc);
    }
}
