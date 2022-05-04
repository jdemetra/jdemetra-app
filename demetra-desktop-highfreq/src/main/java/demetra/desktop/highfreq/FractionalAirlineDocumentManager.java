/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.highfreq;

import demetra.desktop.workspace.AbstractWorkspaceTsItemManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.highfreq.ExtendedAirlineModellingSpec;
import demetra.util.Id;
import demetra.util.LinearId;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemManager.class,
        position = 500)
public class FractionalAirlineDocumentManager extends AbstractWorkspaceTsItemManager<ExtendedAirlineModellingSpec, FractionalAirlineDocument> {


    public static final LinearId ID = new LinearId(ExtendedAirlineModellingSpec.FAMILY, "documents", ExtendedAirlineModellingSpec.METHOD);
    public static final String PATH = "fractionalairline.doc";
    public static final String ITEMPATH = "fractionalairline.doc.item";
    public static final String CONTEXTPATH = "fractionalairline.doc.context";

    @Override
    protected String getItemPrefix() {
        return "FractionalAirlineDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public FractionalAirlineDocument createNewObject() {
        return new FractionalAirlineDocument();
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
    public Class<FractionalAirlineDocument> getItemClass() {
        return FractionalAirlineDocument.class;
    }

}
