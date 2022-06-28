/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.highfreq;

import demetra.desktop.workspace.AbstractWorkspaceTsItemManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.highfreq.ExtendedAirlineDecompositionSpec;
import demetra.util.Id;
import demetra.util.LinearId;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemManager.class,
        position = 500)
public class FractionalAirlineDecompositionDocumentManager extends AbstractWorkspaceTsItemManager<ExtendedAirlineDecompositionSpec, FractionalAirlineDecompositionDocument> {


    public static final LinearId ID = new LinearId(ExtendedAirlineDecompositionSpec.FAMILY, "documents", ExtendedAirlineDecompositionSpec.METHOD);
    public static final String PATH = "fractionalairlinedecomposition.doc";
    public static final String ITEMPATH = "fractionalairlinedecomposition.doc.item";
    public static final String CONTEXTPATH = "fractionalairlinedecomposition.doc.context";

    @Override
    protected String getItemPrefix() {
        return "FractionalAirlineDecompositionDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public FractionalAirlineDecompositionDocument createNewObject() {
        return new FractionalAirlineDecompositionDocument();
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
    public Class<FractionalAirlineDecompositionDocument> getItemClass() {
        return FractionalAirlineDecompositionDocument.class;
    }

}