/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.documents;

import jdplus.tramo.TramoDocument;
import demetra.desktop.workspace.AbstractWorkspaceTsItemManager;
import demetra.desktop.workspace.WorkspaceItemManager;
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
    public TramoDocument createNewObject() {
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

}
