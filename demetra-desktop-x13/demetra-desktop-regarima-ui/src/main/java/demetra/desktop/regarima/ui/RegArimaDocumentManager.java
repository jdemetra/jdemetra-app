/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.ui;

import demetra.desktop.workspace.AbstractWorkspaceTsItemManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.regarima.RegArimaSpec;
import demetra.util.Id;
import demetra.util.LinearId;
import jdplus.x13.regarima.RegArimaDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemManager.class,
        position = 500)
public class RegArimaDocumentManager extends AbstractWorkspaceTsItemManager<RegArimaSpec, RegArimaDocument> {

    public static final LinearId ID = new LinearId(RegArimaSpec.FAMILY, "documents", RegArimaSpec.METHOD);
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
    public Class<RegArimaDocument> getItemClass() {
        return RegArimaDocument.class;
    }

    @Override
    public void openDocument(WorkspaceItem<RegArimaDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            RegArimaTopComponent view = new RegArimaTopComponent(item);
            view.open();
            view.requestActive();
        }
    }
}
