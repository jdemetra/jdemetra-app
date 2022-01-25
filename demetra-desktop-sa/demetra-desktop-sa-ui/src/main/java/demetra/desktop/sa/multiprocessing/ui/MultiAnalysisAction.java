/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.ui;

import demetra.desktop.ui.DemetraUI;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@ActionID(category = "Seasonal Adjustment", id = "ec.nbdemetra.sa.MultiAnalysisAction")
@ActionRegistration(displayName = "#CTL_MultiAnalysisAction")
@ActionReferences({
    @ActionReference(path = "Menu/Statistical methods/Seasonal Adjustment/Multi Processing", position = 10000)
})
@Messages("CTL_MultiAnalysisAction=New")
public final class MultiAnalysisAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        MultiProcessingManager mgr = WorkspaceFactory.getInstance().getManager(MultiProcessingManager.class);
        WorkspaceItem<MultiProcessingDocument> doc = mgr.create(WorkspaceFactory.getInstance().getActiveWorkspace());
        TopComponent c = createView(doc);
        c.open();
        c.requestActive();
    }

    public static TopComponent createView(final WorkspaceItem<MultiProcessingDocument> doc) {
        if (doc.isOpen()) {
            return doc.getView();
        }
        final MultiProcessingController controller = new MultiProcessingController();
        SaBatchUI processingView = new SaBatchUI(doc, controller);
        SummaryView summaryView = new SummaryView(doc, controller);
        MatrixView matrixView = new MatrixView(doc, controller);

        MultiViewDescription[] descriptions = {
            new QuickAndDirtyDescription("Processing", processingView),
            new QuickAndDirtyDescription("Summary", summaryView),
            new QuickAndDirtyDescription("Matrix", matrixView),};

        final TopComponent result = MultiViewFactory.createMultiView(descriptions, descriptions[0]);
        result.setName(doc.getDisplayName());
        doc.setView(result);

        DemetraUI demetraUI = DemetraUI.getDefault();

//        processingView.setDefaultSpecification(demetraUI.getDefaultSASpecInstance());

        controller.addPropertyChangeListener(evt -> {
            switch (controller.getSaProcessingState()) {
                case DONE:
                    result.makeBusy(false);
                    result.setAttentionHighlight(true);
                    break;
                case STARTED:
                    result.makeBusy(true);
                    break;
                case CANCELLED:
                    result.makeBusy(false);
                    break;
                case READY:
                    result.makeBusy(false);
                    result.setAttentionHighlight(false);
                    break;
                case PENDING:
                    result.makeBusy(false);
                    break;
            }
        });
        return result;
    }

    static class QuickAndDirtyDescription implements MultiViewDescription {

        final String name;
        final MultiViewElement multiViewElement;

        public QuickAndDirtyDescription(String name, MultiViewElement multiViewElement) {
            this.name = name;
            this.multiViewElement = multiViewElement;
        }

        @Override
        public int getPersistenceType() {
            return TopComponent.PERSISTENCE_NEVER;
        }

        @Override
        public String getDisplayName() {
            return name;
        }

        @Override
        public Image getIcon() {
            return null;
        }

        @Override
        public HelpCtx getHelpCtx() {
            return null;
        }

        @Override
        public String preferredID() {
            return name;
        }

        @Override
        public MultiViewElement createElement() {
            return multiViewElement;
        }
    }
}
