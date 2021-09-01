/*
 * Copyright 2015 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingDocument;
import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.sa.output.OutputPanel;
import demetra.ui.util.SingleFileExporter;
import demetra.desktop.notification.MessageType;
import demetra.desktop.notification.NotifyUtil;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.satoolkit.ISaSpecification;
import ec.tss.sa.ISaOutputFactory;
import ec.tss.sa.SaItem;
import ec.tss.sa.SaProcessing;
import ec.tss.sa.documents.SaDocument;
import ec.tss.sa.output.CsvMatrixOutputConfiguration;
import ec.tss.sa.output.CsvOutputConfiguration;
import ec.tss.sa.output.TxtOutputConfiguration;
import ec.tstoolkit.algorithm.IOutput;
import ec.tstoolkit.utilities.LinearId;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.SwingWorker;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "ec.nbdemetra.sa.actions.Output")
@ActionRegistration(displayName = "#CTL_Output", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 2000, separatorBefore = 1999),
    @ActionReference(path = "Shortcuts", name = "O")
})
@Messages("CTL_Output=Output...")
public final class Output extends AbstractViewAction<SaBatchUI> {

    public Output() {
        super(SaBatchUI.class);
        putValue(NAME, Bundle.CTL_Output());
        refreshAction();
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = this.context();
        enabled = ui != null && ui.getCurrentProcessing() != null && ui.getCurrentProcessing().isProcessed();
    }

    @Override
    protected void process(SaBatchUI ui) {
        SaProcessing processing = ui.getCurrentProcessing();
        if (processing == null || !processing.isProcessed()) {
            return;
        }
        final OutputPanel panel = new OutputPanel();

        final DialogDescriptor dd = new DialogDescriptor(panel, "Batch output");

        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            WorkspaceItem<MultiProcessingDocument> doc = ui.getDocument();
            List<ISaOutputFactory> outputs = panel.getFactories();
            LinearId id = new LinearId(doc.getOwner().getName(), doc.getDisplayName());
            for (ISaOutputFactory output : outputs) {
                File target = getExportFolder(output);
                if (target != null) {
                    SingleFileExporter
                            .builder()
                            .file(target)
                            .progressLabel("Saving to " + output.getDescription())
                            .onErrorNotify("Saving to " + output.getDescription() + " failed")
                            .onSuccessNotify("Saving to " + output.getDescription() + " done")
                            .build()
                            .execAsync((f, ph) -> store(output, id, processing, ph));
                } else {
                    save(output, id, processing);
                }
            }
        }
    }

    @Nullable
    private File getExportFolder(ISaOutputFactory output) {
        Object result = output.getProperties();
        if (result instanceof CsvMatrixOutputConfiguration) {
            return ((CsvMatrixOutputConfiguration) result).getFolder();
        }
        if (result instanceof CsvOutputConfiguration) {
            return ((CsvOutputConfiguration) result).getFolder();
        }
//        if (result instanceof SpreadsheetOutputConfiguration) {
//            return ((SpreadsheetOutputConfiguration) result).getFolder();
//        }
        if (result instanceof TxtOutputConfiguration) {
            return ((TxtOutputConfiguration) result).getFolder();
        }
        return null;
    }

    private static void store(ISaOutputFactory output, LinearId id, SaProcessing processing, ProgressHandle ph) throws Exception {
        ph.start();
        ph.progress("Initializing");
        IOutput<SaDocument<ISaSpecification>> sadoc = output.create();
        ph.progress("Starting");
        sadoc.start(id);
        ph.progress("Processing");
        for (SaItem cur : processing.toArray()) {
            sadoc.process(cur.toDocument());
        }
        ph.progress("Ending");
        sadoc.end(id);
    }

    private void save(ISaOutputFactory output, LinearId id, SaProcessing processing) {
        new SwingWorker<Void, String>() {
            final ProgressHandle progressHandle = ProgressHandle.createHandle("Saving to " + output.getName());

            @Override
            protected Void doInBackground() throws Exception {
                store(output, id, processing, progressHandle);
                return null;
            }

            @Override
            protected void done() {
                progressHandle.finish();
                try {
                    get();
                    NotifyUtil.show(output.getName() + " successfully generated", "", MessageType.SUCCESS);
                } catch (InterruptedException | ExecutionException ex) {
                    NotifyUtil.error("Can't generate output (" + output.getName() + ")", ex.getMessage(), ex);
                }
            }
        }.execute();
    }
}
