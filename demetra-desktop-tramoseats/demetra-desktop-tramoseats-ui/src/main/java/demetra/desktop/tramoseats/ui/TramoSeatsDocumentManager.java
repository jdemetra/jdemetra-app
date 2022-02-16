/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramoseats.ui;

import demetra.desktop.workspace.AbstractWorkspaceTsItemManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.tramoseats.TramoSeatsSpec;
import demetra.util.Id;
import demetra.util.LinearId;
import jdplus.tramoseats.TramoSeatsDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemManager.class,
        position = 500)
public class TramoSeatsDocumentManager extends AbstractWorkspaceTsItemManager<TramoSeatsSpec, TramoSeatsDocument> {


    public static final LinearId ID = new LinearId(TramoSeatsSpec.FAMILY, "documents", TramoSeatsSpec.METHOD);
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
    public TramoSeatsDocument createNewObject() {
        return new TramoSeatsDocument();
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
}
