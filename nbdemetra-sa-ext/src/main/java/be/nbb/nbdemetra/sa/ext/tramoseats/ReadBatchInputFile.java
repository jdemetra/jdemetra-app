/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.nbb.nbdemetra.sa.ext.tramoseats;

import be.nbb.tramoseats.io.IDecoder;
import be.nbb.tramoseats.io.LegacyDecoder;
import ec.nbdemetra.sa.MultiProcessingDocument;
import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.tramoseats.TramoSeatsDocumentManager;
import ec.nbdemetra.tramoseats.TramoSeatsTopComponent;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.tss.TsFactory;
import ec.tss.sa.SaItem;
import ec.tss.sa.documents.TramoSeatsDocument;
import ec.tstoolkit.algorithm.ProcessingContext;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "be.nbb.nbdemetra.sa.ext.tramoseats.ReadBatchInputFile")
@ActionRegistration(displayName = "#CTL_ReadBatchInputFile", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 99910),
    @ActionReference(path = "Shortcuts", name = "T")
})
@Messages("CTL_ReadBatchInputFile=Read Tramo-Seats input file")
public final class ReadBatchInputFile extends AbstractViewAction<SaBatchUI> {

    public ReadBatchInputFile() {
        super(SaBatchUI.class);
        putValue(NAME, Bundle.CTL_ReadBatchInputFile());
        refreshAction();
    }

    @Override
    protected void refreshAction() {
        SaBatchUI top = context();
        enabled = top != null && top.getDocument().getElement() != null;
    }

    @Override
    protected void process(SaBatchUI cur) {
        SaBatchUI top = context();
        if (top != null) {
            WorkspaceItem<MultiProcessingDocument> document = top.getDocument();
            MultiProcessingDocument element = document.getElement();
            if (element != null) {
                if (readBatchInputFile(element)) {
                    top.redrawAll();
                    document.notify(WorkspaceFactory.Event.ITEMCHANGED, null);
                }
            }
        }
    }

    private boolean readBatchInputFile(MultiProcessingDocument doc) {
        java.io.File file = SpecFiles.fileChooserBuilder.showOpenDialog();
        if (file != null) {
            try {
                FileReader reader = new FileReader(file);
                BufferedReader br = new BufferedReader(reader);
                LegacyDecoder decoder = new LegacyDecoder(ProcessingContext.getActiveContext());
                decoder.setFolder(file.getParentFile());
                List<IDecoder.Document> docs = decoder.decodeMultiDocument(br);
                if (docs != null) {
                    ArrayList<SaItem> items = new ArrayList<>();
                    for (IDecoder.Document cur : docs) {
                        SaItem nitem = new SaItem(cur.spec, TsFactory.instance.createTs(cur.name, null, cur.series));
                        items.add(nitem);
                    }
                    doc.getCurrent().addAll(items);
                    return true;
                }
            } catch (IOException ex) {
                return false;
            }
        }
        return false;
    }

}
