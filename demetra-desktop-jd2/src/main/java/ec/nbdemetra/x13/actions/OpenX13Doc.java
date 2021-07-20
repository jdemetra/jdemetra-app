/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.actions;

import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.nbdemetra.x13.X13DocumentManager;
import ec.tss.sa.documents.X13Document;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "ec.nbdemetra.x13.actions.OpenX13Doc")
@ActionRegistration(displayName = "#CTL_OpenX13Doc")
@ActionReferences({
    @ActionReference(path = X13DocumentManager.ITEMPATH, position = 1000, separatorAfter = 1090)
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
