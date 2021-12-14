/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.x13.ui;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.ui.processing.DocumentUIServices;
import demetra.desktop.ui.processing.DocumentUIServices.UIFactory;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.workspace.AbstractWorkspaceTsItemManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.desktop.x13.descriptors.X13SpecUI;
import demetra.util.Id;
import demetra.util.LinearId;
import demetra.x13.X13Spec;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemManager.class,
        position = 500)
public class X13DocumentManager extends AbstractWorkspaceTsItemManager<X13Spec, X13Document> {

    public static final UIFactory<X13Spec, X13Document> FACTORY=new DocumentUIServices.UIFactory<X13Spec, X13Document>() {
            @Override
            public IProcDocumentView<X13Document> getDocumentView(X13Document document) {
                return X13ViewFactory.getDefault().create(document);
            }

            @Override
            public IObjectDescriptor<X13Spec> getSpecificationDescriptor(X13Document doc) {
                return new X13SpecUI(doc.getSpecification(), false);
            }
        };
 
    public static final LinearId ID = new LinearId(X13Spec.FAMILY, "documents", X13Spec.METHOD);
    public static final String PATH = "x13.doc";
    public static final String ITEMPATH = "x13.doc.item";
    public static final String CONTEXTPATH = "x13.doc.context";

    @Override
    protected String getItemPrefix() {
        return "X13Doc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected X13Document createNewObject() {
        return new X13Document();
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
    public Class<X13Document> getItemClass() {
        return X13Document.class;
    }

    @Override
    public void openDocument(WorkspaceItem<X13Document> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            X13TopComponent view = new X13TopComponent(item);
            view.open();
            view.requestActive();
        }
    }
}
