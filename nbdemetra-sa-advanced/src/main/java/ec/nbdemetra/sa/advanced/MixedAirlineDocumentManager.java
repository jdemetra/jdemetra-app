/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced;

import ec.nbdemetra.ui.DocumentUIServices;
import ec.nbdemetra.ws.AbstractWorkspaceTsItemManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.satoolkit.special.MixedAirlineSpecification;
import ec.tss.sa.documents.MixedAirlineDocument;
import ec.tss.sa.processors.MixedAirlineProcessor;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.nbdemetra.sa.advanced.descriptors.MixedAirlineSpecUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.nbdemetra.sa.advanced.ui.MixedAirlineViewFactory;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IWorkspaceItemManager.class,
position = 1830)
public class MixedAirlineDocumentManager extends AbstractWorkspaceTsItemManager<MixedAirlineSpecification, MixedAirlineDocument> {

    static {
        DocumentUIServices.getDefault().register(MixedAirlineDocument.class, new DocumentUIServices.AbstractUIFactory<MixedAirlineSpecification, MixedAirlineDocument>() {

            @Override
            public IProcDocumentView<MixedAirlineDocument> getDocumentView(MixedAirlineDocument document) {
                return MixedAirlineViewFactory.getDefault().create(document);
            }

            @Override
            public IObjectDescriptor<MixedAirlineSpecification> getSpecificationDescriptor(MixedAirlineDocument doc) {
                return new MixedAirlineSpecUI(doc.getSpecification().clone());
            }
        });
    }
    public static final LinearId ID = new LinearId(MixedAirlineProcessor.DESCRIPTOR.family, "documents", MixedAirlineProcessor.DESCRIPTOR.name);
    public static final String PATH = "mairline.doc";
    public static final String ITEMPATH = "mairline.doc.item";
    public static final String CONTEXTPATH = "mairline.doc.context";

    @Override
    protected String getItemPrefix() {
        return "MixedAirlineDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected MixedAirlineDocument createNewObject() {
        return new MixedAirlineDocument();
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
        return Status.Experimental;
    }

    @Override
    public void openDocument(WorkspaceItem<MixedAirlineDocument> doc) {
        if (doc.isOpen()) {
            doc.getView().requestActive();
        } else {
            MixedAirlineTopComponent view = new MixedAirlineTopComponent(doc);
            doc.setView(view);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public Class<MixedAirlineDocument> getItemClass() {
        return MixedAirlineDocument.class;
    }
}
