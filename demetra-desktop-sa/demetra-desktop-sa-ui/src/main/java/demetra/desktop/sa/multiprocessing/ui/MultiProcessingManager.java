/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.ui;

import demetra.desktop.workspace.AbstractWorkspaceItemManager;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.sa.SaSpecification;
import demetra.util.Id;
import demetra.util.LinearId;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemManager.class,
        position = 1900)
public class MultiProcessingManager extends AbstractWorkspaceItemManager<MultiProcessingDocument> {

    public static final LinearId ID = new LinearId(SaSpecification.FAMILY, WorkspaceFactory.MULTIDOCUMENTS);
    public static final String PATH = "sa.mdoc";
    public static final String ITEMPATH = "sa.mdoc.item";
//    public static final String DOCUMENTPATH = "sa.mdoc.document";
    public static final String CONTEXTPATH = "sa.mdoc.context";
    public static final String LOCALPATH = "sa.mdoc.local";
    public static final String REPOSITORY = "SAProcessing";
    public static final String PREFIX = "SAProcessing";
    public static SaSpecification defSpec;

    public static void setDefaultSpecification(final SaSpecification spec) {
        synchronized (ID) {
            defSpec = spec;
        }
    }

    public static SaSpecification getDefaultSpecification() {
        synchronized (ID) {
            return defSpec;
        }
    }

    @Override
    public Status getStatus() {
        return Status.Certified;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.MultiDoc;
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public String getActionsPath() {
        return PATH;
    }

    @Override
    protected String getItemPrefix() {
        return REPOSITORY;
    }

    @Override
    public MultiProcessingDocument createNewObject() {
        MultiProcessingDocument ndoc = MultiProcessingDocument.createNew();
        return ndoc;
    }

    @Override
    public Action getPreferredItemAction(final Id child) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorkspaceItem<MultiProcessingDocument> doc = (WorkspaceItem<MultiProcessingDocument>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    openDocument(doc);
                }
            }
        };
    }

    public void openDocument(final WorkspaceItem<MultiProcessingDocument> doc) {

        if (doc == null || doc.getElement() == null) {
            return;
        }
        if (doc.isOpen()) {
            doc.getView().requestActive();
        } else {
            TopComponent view = MultiAnalysisAction.createView(doc);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public Class<MultiProcessingDocument> getItemClass() {
        return MultiProcessingDocument.class;
    }

    @Override
    public Icon getItemIcon(WorkspaceItem<MultiProcessingDocument> doc) {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/sa/documents_16x16.png", false);
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/sa/folder-open-document_16x16.png", false);
    }
}
