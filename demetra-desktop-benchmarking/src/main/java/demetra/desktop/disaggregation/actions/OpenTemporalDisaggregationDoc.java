/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.disaggregation.actions;

import demetra.desktop.disaggregation.documents.TemporalDisaggregationDocumentManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.WsNode;
import demetra.desktop.workspace.DocumentUIServices;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import jdplus.tempdisagg.univariate.TemporalDisaggregationDocument;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "demetra.desktop.disaggregation.actions.OpenTemporalDisaggregationDoc")
@ActionRegistration(displayName = "#CTL_OpenTramoDoc")
@ActionReferences({
    @ActionReference(path = TemporalDisaggregationDocumentManager.ITEMPATH, position = 1600, separatorBefore = 1590)
})
@NbBundle.Messages("CTL_OpenTramoDoc=Open")
public class OpenTemporalDisaggregationDoc implements ActionListener {

    private final WsNode context;

    public OpenTemporalDisaggregationDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<TemporalDisaggregationDocument> doc = context.getWorkspace().searchDocument(context.lookup(), TemporalDisaggregationDocument.class);
        DocumentUIServices ui = DocumentUIServices.forDocument(TemporalDisaggregationDocument.class);
        if (ui != null) {
            ui.showDocument(doc);
        }
    }
}
