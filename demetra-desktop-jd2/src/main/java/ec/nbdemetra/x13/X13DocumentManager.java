/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13;

import ec.nbdemetra.ui.DocumentUIServices;
import ec.nbdemetra.ws.*;
import ec.nbdemetra.x13.descriptors.X13SpecUI;
import ec.satoolkit.x13.X13Specification;
import ec.tss.sa.documents.X13Document;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.nbdemetra.x13.ui.X13ViewFactory;
import ec.satoolkit.algorithm.implementation.X13ProcessingFactory;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemManager.class,
position = 1520)
public class X13DocumentManager extends AbstractWorkspaceTsItemManager<X13Specification, X13Document> {

    static {
//        FileRepository repo = WorkspaceFactory.getInstance().getRepository(FileRepository.class);
//        if (repo != null) {
//            repo.register(X13Document.class, new X13DocFileRepository());
//        }
        DocumentUIServices.getDefault().register(X13Document.class, new DocumentUIServices.AbstractUIFactory<X13Specification, X13Document>() {

            @Override
            public IProcDocumentView<X13Document> getDocumentView(X13Document document) {
                return X13ViewFactory.getDefault().create(document);
            }

            @Override
            public IObjectDescriptor<X13Specification> getSpecificationDescriptor(X13Document doc) {
                TsData data = doc.getSeries();
                return new X13SpecUI(doc.getSpecification().clone(), data == null ? null : data.getDomain(), false);
            }
        });
    }
    public static final LinearId ID = new LinearId(X13ProcessingFactory.DESCRIPTOR.family, "documents", X13ProcessingFactory.DESCRIPTOR.name);
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
        X13Document doc= new X13Document();
        doc.setSpecification(X13Specification.RSA4);
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
    public void openDocument(WorkspaceItem<X13Document> doc) {
        if (doc.isOpen()) {
            doc.getView().requestActive();
        } else {
            X13TopComponent view = new X13TopComponent(doc);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public Class<X13Document> getItemClass() {
        return X13Document.class;
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/sa/blog_16x16.png", false);
    }
}