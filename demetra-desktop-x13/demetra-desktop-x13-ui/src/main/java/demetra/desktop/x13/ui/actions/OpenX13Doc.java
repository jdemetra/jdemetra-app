/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.x13.ui.actions;

import demetra.desktop.x13.ui.X13DocumentManager;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.WsNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import jdplus.x13.X13Document;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "demetra.desktop.x13.ui.OpenX13Doc")
@ActionRegistration(displayName = "#CTL_OpenX13Doc")
@ActionReferences({
    @ActionReference(path = X13DocumentManager.ITEMPATH, position = 1600, separatorBefore = 1590)
})
@NbBundle.Messages("CTL_OpenX13Doc=Open")
public class OpenX13Doc implements ActionListener {

    private final WsNode context;

    public OpenX13Doc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<X13Document> doc = context.getWorkspace().searchDocument(context.lookup(), X13Document.class);
        X13DocumentManager mgr = WorkspaceFactory.getInstance().getManager(X13DocumentManager.class);
        mgr.openDocument(doc);
    }
}
