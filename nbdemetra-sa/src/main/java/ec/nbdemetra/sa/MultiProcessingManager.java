/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import ec.nbdemetra.ws.AbstractWorkspaceItemManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.satoolkit.GenericSaProcessingFactory;
import ec.satoolkit.ISaSpecification;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
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
@ServiceProvider(service = IWorkspaceItemManager.class,
position = 1900)
public class MultiProcessingManager extends AbstractWorkspaceItemManager<MultiProcessingDocument> {

    public static final LinearId ID = new LinearId(GenericSaProcessingFactory.FAMILY, WorkspaceFactory.MULTIDOCUMENTS);
    public static final String PATH = "sa.mdoc";
    public static final String ITEMPATH = "sa.mdoc.item";
//    public static final String DOCUMENTPATH = "sa.mdoc.document";
    public static final String CONTEXTPATH = "sa.mdoc.context";
    public static final String LOCALPATH = "sa.mdoc.local";
    public static final String REPOSITORY = "SAProcessing";
    public static final String PREFIX = "SAProcessing";
    private static ISaSpecification def_ = TramoSeatsSpecification.RSAfull;

    public static void setDefaultSpecification(final ISaSpecification spec) {
        synchronized (ID) {
            def_ = spec;
        }
    }

    public static ISaSpecification getDefaultSpecification() {
        synchronized (ID) {
            return def_;
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
    protected MultiProcessingDocument createNewObject() {
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

        if (doc == null||doc.getElement() == null) {
            return;
        }

        TopComponent view = MultiAnalysisAction.createView(doc);
        view.open();
        view.requestActive();
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