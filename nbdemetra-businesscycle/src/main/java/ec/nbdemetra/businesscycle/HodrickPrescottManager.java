/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.businesscycle;

import ec.nbdemetra.businesscycle.descriptors.HodrickPrescottSpecUI;
import ec.nbdemetra.businesscycle.ui.HodrickPrescottViewFactory;
import ec.nbdemetra.ui.DocumentUIServices;
import ec.nbdemetra.ws.AbstractWorkspaceTsItemManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.businesscycle.documents.HodrickPrescottDocument;
import ec.tss.businesscycle.documents.HodrickPrescottSpecification;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.IProcDocumentView;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IWorkspaceItemManager.class,
position = 3000)
public class HodrickPrescottManager extends AbstractWorkspaceTsItemManager<HodrickPrescottSpecification, HodrickPrescottDocument> {

    static {
        DocumentUIServices.getDefault().register(HodrickPrescottDocument.class, new DocumentUIServices.AbstractUIFactory<HodrickPrescottSpecification, HodrickPrescottDocument>() {

            @Override
            public IProcDocumentView<HodrickPrescottDocument> getDocumentView(HodrickPrescottDocument document) {
                return HodrickPrescottViewFactory.getDefault().create(document);
            }

            @Override
            public IObjectDescriptor<HodrickPrescottSpecification> getSpecificationDescriptor(HodrickPrescottDocument doc) {
                return new HodrickPrescottSpecUI(doc.getSpecification().clone());
            }
        });
    }
    public static final LinearId ID = new LinearId("Business cycle", "documents", "hodrick-prescott");
    public static final String PATH = "hp.doc";
    public static final String CONTEXT_PATH = "hp.doc.context";

    @Override
    protected String getItemPrefix() {
        return "HodrickPrescottDoc";
    }

    @Override
    public Id getId() {
        return ID;
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
    public void openDocument(WorkspaceItem<HodrickPrescottDocument> doc) {
        HodrickPrescottTopComponent view = new HodrickPrescottTopComponent(doc);
        view.open();
        view.requestActive();
    }

    @Override
    protected HodrickPrescottDocument createNewObject() {
        return new HodrickPrescottDocument();
    }

    @Override
    public Class<HodrickPrescottDocument> getItemClass() {
        return HodrickPrescottDocument.class;
    }
    
    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/businesscycle/arrow-repeat_16x16.png", false);
    }
}
