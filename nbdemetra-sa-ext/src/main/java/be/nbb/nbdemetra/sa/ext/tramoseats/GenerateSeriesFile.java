/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.nbb.nbdemetra.sa.ext.tramoseats;

import ec.nbdemetra.tramoseats.TramoSeatsDocumentManager;
import ec.nbdemetra.tramoseats.TramoSeatsTopComponent;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import be.nbb.tramoseats.io.LegacyEncoder;
import ec.tss.Ts;
import ec.tss.sa.documents.TramoSeatsDocument;
import ec.tstoolkit.algorithm.ProcessingContext;
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
        id = "be.nbb.nbdemetra.sa.ext.tramoseats.GenerateSeriesFile")
@ActionRegistration(displayName = "#CTL_GenerateSeriesFile", lazy = false)
@ActionReferences({
    @ActionReference(path = TramoSeatsDocumentManager.CONTEXTPATH + SpecFiles.PATH, position = 9110),
    @ActionReference(path = "Shortcuts", name = "T")
})
@Messages("CTL_GenerateSeriesFile=Write time series")
public final class GenerateSeriesFile extends AbstractViewAction<TramoSeatsTopComponent> {

    public GenerateSeriesFile() {
        super(TramoSeatsTopComponent.class);
        putValue(NAME, Bundle.CTL_GenerateSeriesFile());
        refreshAction();
    }

    @Override
    protected void refreshAction() {
        TramoSeatsTopComponent top = context();
        enabled = top != null && top.getDocument().getElement() != null
                && top.getDocument().getElement().getSeries() != null;
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
        Ts s = doc.getInput();
        if (s == null) {
            return;
        }
        final JTextPane panel = new JTextPane();
        panel.setPreferredSize(new Dimension(200, 400));
        final DialogDescriptor dd = new DialogDescriptor(NbComponents.newJScrollPane(panel), "TramoSeats Series");
        LegacyEncoder encoder = new LegacyEncoder(ProcessingContext.getActiveContext());
        String report = encoder.encode(s.getRawName(), s.getTsData());
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
