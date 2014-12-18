/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.nbb.nbdemetra.sa.ext.tramoseats;

import ec.nbdemetra.tramoseats.TramoSeatsDocumentManager;
import ec.nbdemetra.tramoseats.TramoSeatsTopComponent;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import be.nbb.tramoseats.io.IDecoder;
import be.nbb.tramoseats.io.LegacyDecoder;
import ec.tss.TsFactory;
import ec.tss.sa.documents.TramoSeatsDocument;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "be.nbb.nbdemetra.sa.ext.tramoseats.ReadInputFile")
@ActionRegistration(displayName = "#CTL_ReadInputFile", lazy = false)
@ActionReferences({
    @ActionReference(path = TramoSeatsDocumentManager.CONTEXTPATH + SpecFiles.PATH, position = 9200, separatorBefore=9199),
    @ActionReference(path = "Shortcuts", name = "R")
})
@Messages("CTL_ReadInputFile=Read input file")
public final class ReadInputFile extends AbstractViewAction<TramoSeatsTopComponent> {

    public ReadInputFile() {
        super(TramoSeatsTopComponent.class);
        putValue(NAME, Bundle.CTL_ReadInputFile());
        refreshAction();
    }

    @Override
    protected void refreshAction() {
        TramoSeatsTopComponent top = context();
        enabled = top != null && top.getDocument().getElement() != null;
    }

    @Override
    protected void process(TramoSeatsTopComponent cur) {
        TramoSeatsTopComponent top = context();
        if (top != null) {
            WorkspaceItem<TramoSeatsDocument> document = top.getDocument();
            TramoSeatsDocument element = document.getElement();
            if (element != null) {
                if (readInputFile(element)) {
                    document.notify(WorkspaceFactory.Event.ITEMCHANGED, null);
                }
            }
        }
    }

    private boolean readInputFile(TramoSeatsDocument doc) {
        java.io.File file = SpecFiles.fileChooserBuilder.showOpenDialog();
        if (file != null) {
            try {
                FileReader reader = new FileReader(file);
                BufferedReader br = new BufferedReader(reader);

                LegacyDecoder decoder = new LegacyDecoder(null);
                decoder.setFolder(file.getParentFile());
                IDecoder.Document ndoc = decoder.decodeDocument(br);
                if (ndoc != null) {
                    doc.setSpecification(ndoc.spec);
                    doc.setInput(TsFactory.instance.createTs(ndoc.name, null, ndoc.series));
                   return true;
                }
            } catch (IOException ex) {
                return false;
            }
        }
        return false;
    }

}
