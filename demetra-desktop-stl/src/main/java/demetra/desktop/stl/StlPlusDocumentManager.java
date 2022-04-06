/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.stl;

import demetra.desktop.workspace.AbstractWorkspaceItemManager;
import demetra.desktop.workspace.AbstractWorkspaceTsItemManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.util.Id;
import demetra.util.LinearId;
import jdplus.stl.StlPlusSpecification;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemManager.class,
        position = 500)
public class StlPlusDocumentManager extends AbstractWorkspaceTsItemManager<StlPlusSpecification, StlPlusDocument> {


    public static final LinearId ID = new LinearId(StlPlusSpecification.FAMILY, "documents", StlPlusSpecification.METHOD);
    public static final String PATH = "stlplus.doc";
    public static final String ITEMPATH = "stlplus.doc.item";
    public static final String CONTEXTPATH = "stlplus.doc.context";

    @Override
    protected String getItemPrefix() {
        return "StlPlusDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public StlPlusDocument createNewObject() {
        return new StlPlusDocument();
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
    public Class<StlPlusDocument> getItemClass() {
        return StlPlusDocument.class;
    }

    @Override
    public void openDocument(WorkspaceItem<StlPlusDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            StlPlusTopComponent view = new StlPlusTopComponent(item);
            view.open();
            view.requestActive();
        }
    }
}
