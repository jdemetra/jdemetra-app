/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.nbb.nbdemetra.sa.ext.tramoseats;

import be.nbb.tramoseats.io.IDecoder;
import be.nbb.tramoseats.io.IDecoder.Document;
import be.nbb.tramoseats.io.LegacyDecoder;
import be.nbb.tramoseats.io.LegacyEncoder;
import ec.nbdemetra.sa.MultiProcessingDocument;
import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.tramoseats.TramoSeatsDocumentManager;
import ec.nbdemetra.tramoseats.TramoSeatsTopComponent;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tss.TsFactory;
import ec.tss.sa.SaItem;
import ec.tss.sa.SaProcessing;
import ec.tss.sa.documents.TramoSeatsDocument;
import ec.tstoolkit.algorithm.ProcessingContext;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "be.nbb.nbdemetra.sa.ext.tramoseats.GenerateBatchInputFile")
@ActionRegistration(displayName = "#CTL_GenerateBatchInputFile", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 99900, separatorBefore = 99899),
    @ActionReference(path = "Shortcuts", name = "T")
})
@Messages("CTL_GenerateBatchInputFile=Write Tramo-Seats input file")
public final class GenerateBatchInputFile extends AbstractViewAction<SaBatchUI> {

    public GenerateBatchInputFile() {
        super(SaBatchUI.class);
        putValue(NAME, Bundle.CTL_GenerateBatchInputFile());
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
                if (writeBatchInputFile(element)) {
                    top.redrawAll();
                    document.notify(WorkspaceFactory.Event.ITEMCHANGED, null);
                }
            }
        }
    }

    private boolean writeBatchInputFile(MultiProcessingDocument doc) {
        LegacyEncoder encoder = new LegacyEncoder(ProcessingContext.getActiveContext());
        List<Document> docs = new ArrayList<>();
        SaProcessing sap = doc.getCurrent();
        SaItem[] items = sap.toArray();
        for (int i = 0; i < items.length; ++i) {
            if (items[i].getEstimationSpecification() instanceof TramoSeatsSpecification) {
                Document cur = new Document();
                cur.name = items[i].getTs().getName();
                cur.series = items[i].getTsData();
                cur.spec = (TramoSeatsSpecification) items[i].getEstimationSpecification();
                if (cur.series != null) {
                    docs.add(cur);
                }
            }
        }
        java.io.File file = SpecFiles.fileChooserBuilder.showSaveDialog();
        if (file != null) {
            try {
                String sfile = file.getAbsolutePath();
                try (FileWriter writer = new FileWriter(sfile)) {
                    encoder.encodeMultiDocument(writer, docs);
                    return true;
                }
            } catch (Exception ex) {
            }
        }
        return false;
    }

}
