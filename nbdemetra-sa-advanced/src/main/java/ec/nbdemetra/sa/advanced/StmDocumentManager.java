/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced;

import ec.nbdemetra.ui.DocumentUIServices;
import ec.nbdemetra.ws.AbstractWorkspaceTsItemManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.satoolkit.special.StmSpecification;
import ec.tss.sa.documents.StmDocument;
import ec.tss.sa.processors.StmProcessor;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.nbdemetra.sa.advanced.descriptors.StmSpecUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.nbdemetra.sa.advanced.ui.StructuralModelViewFactory;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IWorkspaceItemManager.class,
position = 1800)
public class StmDocumentManager extends AbstractWorkspaceTsItemManager<StmSpecification, StmDocument> {

    static {
        DocumentUIServices.getDefault().register(StmDocument.class, new DocumentUIServices.AbstractUIFactory<StmSpecification, StmDocument>() {

            @Override
            public IProcDocumentView<StmDocument> getDocumentView(StmDocument document) {
                return StructuralModelViewFactory.getDefault().create(document);
            }

            @Override
            public IObjectDescriptor<StmSpecification> getSpecificationDescriptor(StmDocument doc) {
                return new StmSpecUI(doc.getSpecification().clone());
            }
        });
    }
    public static final LinearId ID = new LinearId(StmProcessor.DESCRIPTOR.family, "documents", StmProcessor.DESCRIPTOR.name);
    public static final String PATH = "stm.doc";
    public static final String ITEMPATH = "stm.doc.item";
    public static final String CONTEXTPATH = "stm.doc.context";

    @Override
    protected String getItemPrefix() {
        return "StmDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected StmDocument createNewObject() {
        return new StmDocument();
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
        return Status.Acceptable;
    }

    @Override
    public void openDocument(WorkspaceItem<StmDocument> doc) {
        if (doc.isOpen()) {
            doc.getView().requestActive();
        } else {
            StmTopComponent view = new StmTopComponent(doc);
            doc.setView(view);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public Class<StmDocument> getItemClass() {
        return StmDocument.class;
    }
}
