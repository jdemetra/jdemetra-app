/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats;

import ec.nbdemetra.tramoseats.descriptors.TramoSeatsSpecUI;
import ec.nbdemetra.tramoseats.ui.TramoSeatsViewFactory;
import ec.nbdemetra.ui.DocumentUIServices;
import ec.nbdemetra.ws.AbstractWorkspaceTsItemManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.satoolkit.algorithm.implementation.TramoSeatsProcessingFactory;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tss.sa.documents.TramoSeatsDocument;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.IProcDocumentView;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IWorkspaceItemManager.class,
position = 1500)
public class TramoSeatsDocumentManager extends AbstractWorkspaceTsItemManager<TramoSeatsSpecification, TramoSeatsDocument> {

    static {
//        FileRepository repo=WorkspaceFactory.getInstance().getRepository(FileRepository.class);
//        if (repo != null)
//            repo.register(TramoSeatsDocument.class, new TramoSeatsDocFileRepository());
        DocumentUIServices.getDefault().register(TramoSeatsDocument.class, new DocumentUIServices.AbstractUIFactory<TramoSeatsSpecification, TramoSeatsDocument>() {

            @Override
            public IProcDocumentView<TramoSeatsDocument> getDocumentView(TramoSeatsDocument document) {
                return TramoSeatsViewFactory.getDefault().create(document);
            }

            @Override
            public IObjectDescriptor<TramoSeatsSpecification> getSpecificationDescriptor(TramoSeatsDocument doc) {
                return new TramoSeatsSpecUI(doc.getSpecification().clone(), false);
            }
        });
    }
    public static final LinearId ID = new LinearId(TramoSeatsProcessingFactory.DESCRIPTOR.family, WorkspaceFactory.DOCUMENTS, TramoSeatsProcessingFactory.DESCRIPTOR.name);
    public static final String PATH = "tramoseats.doc";
    public static final String ITEMPATH = "tramoseats.doc.item";
    public static final String CONTEXTPATH = "tramoseats.doc.context";

    @Override
    protected String getItemPrefix() {
        return "TramoSeatsDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected TramoSeatsDocument createNewObject() {
        TramoSeatsDocument doc= new TramoSeatsDocument();
        doc.setSpecification(TramoSeatsSpecification.RSAfull);
        return doc;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.Doc;
    }

    @Override
    public String getActionsPath() {
        return PATH;
    }

    @Override
    public Status getStatus() {
        return Status.Certified;
    }

    @Override
    public Class<TramoSeatsDocument> getItemClass() {
        return TramoSeatsDocument.class;
    }

    @Override
    public void openDocument(WorkspaceItem<TramoSeatsDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            TramoSeatsTopComponent view = new TramoSeatsTopComponent(item);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/sa/blog-blue_16x16.png", false);
    }
}
