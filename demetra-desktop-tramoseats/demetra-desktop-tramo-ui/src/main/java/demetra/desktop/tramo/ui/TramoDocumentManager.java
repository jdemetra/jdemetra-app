/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.ui;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.tramo.descriptors.TramoSpecUI;
import demetra.desktop.ui.processing.DocumentUIServices;
import demetra.desktop.ui.processing.DocumentUIServices.UIFactory;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.workspace.AbstractWorkspaceTsItemManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.timeseries.Ts;
import demetra.timeseries.TsDomain;
import demetra.tramo.TramoSpec;
import demetra.util.Id;
import demetra.util.LinearId;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemManager.class,
        position = 500)
public class TramoDocumentManager extends AbstractWorkspaceTsItemManager<TramoSpec, TramoDocument> {

    public static final UIFactory<TramoSpec, TramoDocument> FACTORY=new DocumentUIServices.UIFactory<TramoSpec, TramoDocument>() {
            @Override
            public IProcDocumentView<TramoDocument> getDocumentView(TramoDocument document) {
                return TramoViewFactory.getDefault().create(document);
            }

            @Override
            public IObjectDescriptor<TramoSpec> getSpecificationDescriptor(TramoDocument doc) {
                Ts input = doc.getInput();
                TsDomain domain = null;
                if (input != null){
                    domain=input.getData().getDomain();
                }
                return new TramoSpecUI(doc.getSpecification(), false, domain);
            }
        };
 
    public static final LinearId ID = new LinearId(TramoSpec.FAMILY, "documents", TramoSpec.METHOD);
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
