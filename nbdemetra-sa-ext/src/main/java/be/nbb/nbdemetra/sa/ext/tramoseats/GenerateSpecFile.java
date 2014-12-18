/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.nbb.nbdemetra.sa.ext.tramoseats;

import be.nbb.tramoseats.io.LegacyEncoder;
import ec.nbdemetra.tramoseats.TramoSeatsDocumentManager;
import ec.nbdemetra.tramoseats.TramoSeatsTopComponent;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.tss.sa.documents.TramoSeatsDocument;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.simplets.TsData;
import java.awt.Dimension;
import java.io.FileWriter;
import javax.swing.JTextPane;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "be.nbb.nbdemetra.sa.ext.tramoseats.GenerateSpecFile")
@ActionRegistration(displayName = "#CTL_GenerateSpecFile", lazy = false)
@ActionReferences({
    @ActionReference(path = TramoSeatsDocumentManager.CONTEXTPATH + SpecFiles.PATH, position = 9100),
    @ActionReference(path = "Shortcuts", name = "G")
})
@Messages("CTL_GenerateSpecFile=Write specifications")
public final class GenerateSpecFile extends AbstractViewAction<TramoSeatsTopComponent> {

    public GenerateSpecFile() {
        super(TramoSeatsTopComponent.class);
        putValue(NAME, Bundle.CTL_GenerateSpecFile());
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
            TramoSeatsDocument element = top.getDocument().getElement();
            if (element != null) {
                generateFile(element);
            }
        }
    }

    private void generateFile(TramoSeatsDocument doc) {
        final JTextPane panel = new JTextPane();
        panel.setPreferredSize(new Dimension(300, 100));
        final DialogDescriptor dd = new DialogDescriptor(NbComponents.newJScrollPane(panel), "TramoSeats Specs");
        TsData s = doc.getSeries();
        LegacyEncoder encoder = new LegacyEncoder(s == null ? null : s.getDomain(), ProcessingContext.getActiveContext());
        String report = encoder.encode(doc.getSpecification());
        panel.setText(report);
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            java.io.File file = SpecFiles.fileChooserBuilder.showSaveDialog();
            if (file != null) {
                try {
                    String sfile = file.getAbsolutePath();
                    try (FileWriter writer = new FileWriter(sfile)) {
                        writer.append(report);
                    }
                } catch (Exception ex) {
                }
            }
        }
    }
}
