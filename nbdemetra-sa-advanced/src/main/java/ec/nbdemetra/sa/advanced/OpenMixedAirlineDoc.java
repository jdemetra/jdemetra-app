/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced;

import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.tss.sa.documents.MixedAirlineDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "ec.nbdemetra.sa.advanced.OpenMixedAirlineDoc")
@ActionRegistration(displayName = "#CTL_OpenMixedAirlineDoc")
@ActionReferences({
    @ActionReference(path = MixedAirlineDocumentManager.ITEMPATH, position = 1600, separatorBefore = 1300)
 })
@NbBundle.Messages("CTL_OpenMixedAirlineDoc=Open")
public class OpenMixedAirlineDoc implements ActionListener {

    private final WsNode context;

    public OpenMixedAirlineDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<MixedAirlineDocument> doc = context.getWorkspace().searchDocument(context.lookup(), MixedAirlineDocument.class);
        MixedAirlineDocumentManager manager = WorkspaceFactory.getInstance().getManager(MixedAirlineDocumentManager.class);
        manager.openDocument(doc);
    }
}
