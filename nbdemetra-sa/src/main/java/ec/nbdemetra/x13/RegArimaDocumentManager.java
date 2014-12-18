/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13;

import ec.nbdemetra.ui.DocumentUIServices;
import ec.nbdemetra.ws.AbstractWorkspaceTsItemManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.x13.descriptors.RegArimaSpecUI;
import ec.tss.modelling.documents.RegArimaDocument;
import ec.tstoolkit.algorithm.implementation.RegArimaProcessingFactory;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.nbdemetra.x13.ui.RegArimaViewFactory;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IWorkspaceItemManager.class,
position = 520)
public class RegArimaDocumentManager extends AbstractWorkspaceTsItemManager<RegArimaSpecification, RegArimaDocument> {

    static {
        DocumentUIServices.getDefault().register(RegArimaDocument.class, new DocumentUIServices.AbstractUIFactory<RegArimaSpecification, RegArimaDocument>() {

            @Override
            public IProcDocumentView<RegArimaDocument> getDocumentView(RegArimaDocument document) {
                return RegArimaViewFactory.getDefault().create(document);
            }

            @Override
            public IObjectDescriptor<RegArimaSpecification> getSpecificationDescriptor(RegArimaDocument doc) {
                TsData data = doc.getSeries();
                return new RegArimaSpecUI(doc.getSpecification().clone(), data == null ? null : data.getDomain(), false);
            }
        });
    }
    public static final LinearId ID = new LinearId(RegArimaProcessingFactory.DESCRIPTOR.family, "documents", RegArimaProcessingFactory.DESCRIPTOR.name);
    public static final String PATH = "regarima.doc";
    public static final String ITEMPATH = "regarima.doc.item";
    public static final String CONTEXTPATH = "regarima.doc.context";

    @Override
    protected String getItemPrefix() {
        return "RegArimaDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected RegArimaDocument createNewObject() {
        return new RegArimaDocument();
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
    public void openDocument(WorkspaceItem<RegArimaDocument> doc) {
        if (doc.isOpen()) {
            doc.getView().requestActive();
        } else {
            RegArimaTopComponent view = new RegArimaTopComponent(doc);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public Class<RegArimaDocument> getItemClass() {
        return RegArimaDocument.class;
    }
}
