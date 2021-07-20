/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats;

import ec.nbdemetra.tramoseats.descriptors.TramoSpecUI;
import ec.nbdemetra.ui.DocumentUIServices;
import ec.nbdemetra.ws.AbstractWorkspaceTsItemManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.modelling.documents.TramoDocument;
import ec.tstoolkit.algorithm.implementation.TramoProcessingFactory;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.nbdemetra.tramoseats.ui.TramoViewFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemManager.class,
position = 500)
public class TramoDocumentManager extends AbstractWorkspaceTsItemManager<TramoSpecification, TramoDocument> {

    static {
        DocumentUIServices.getDefault().register(TramoDocument.class, new DocumentUIServices.AbstractUIFactory<TramoSpecification, TramoDocument>() {
            @Override
            public IProcDocumentView<TramoDocument> getDocumentView(TramoDocument document) {
                return TramoViewFactory.getDefault().create(document);
            }

            @Override
            public IObjectDescriptor<TramoSpecification> getSpecificationDescriptor(TramoDocument doc) {
                return new TramoSpecUI(doc.getSpecification().clone(), false);
            }
        });
    }
    public static final LinearId ID = new LinearId(TramoProcessingFactory.DESCRIPTOR.family, "documents", TramoProcessingFactory.DESCRIPTOR.name);
    public static final String PATH = "tramo.doc";
    public static final String ITEMPATH = "tramo.doc.item";
    public static final String CONTEXTPATH = "tramo.doc.context";

    @Override
    protected String getItemPrefix() {
        return "TramoDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected TramoDocument createNewObject() {
        return new TramoDocument();
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
    public Class<TramoDocument> getItemClass() {
        return TramoDocument.class;
    }

    @Override
    public void openDocument(WorkspaceItem<TramoDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            TramoTopComponent view = new TramoTopComponent(item);
            view.open();
            view.requestActive();
        }
    }
}