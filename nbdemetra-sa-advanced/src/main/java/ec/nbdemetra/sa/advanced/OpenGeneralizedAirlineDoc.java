/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced;

import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.tss.sa.documents.GeneralizedAirlineDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "ec.nbdemetra.sa.advanced.OpenGeneralizedAirlineDoc")
@ActionRegistration(displayName = "#CTL_OpenGeneralizedAirlineDoc")
@ActionReferences({
    @ActionReference(path = GeneralizedAirlineDocumentManager.ITEMPATH, position = 1600, separatorBefore = 1300)
 })
@NbBundle.Messages("CTL_OpenGeneralizedAirlineDoc=Open")
public class OpenGeneralizedAirlineDoc implements ActionListener {

    private final WsNode context;

    public OpenGeneralizedAirlineDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<GeneralizedAirlineDocument> doc = context.getWorkspace().searchDocument(context.lookup(), GeneralizedAirlineDocument.class);
        GeneralizedAirlineDocumentManager manager = WorkspaceFactory.getInstance().getManager(GeneralizedAirlineDocumentManager.class);
        manager.openDocument(doc);
    }
}
