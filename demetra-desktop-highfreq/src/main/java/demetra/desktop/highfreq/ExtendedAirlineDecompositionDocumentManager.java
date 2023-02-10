/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.highfreq;

import jdplus.highfreq.extendedairline.decomposiiton.ExtendedAirlineDecompositionDocument;
import demetra.desktop.workspace.AbstractWorkspaceTsItemManager;
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
        position = 2000)
public class ExtendedAirlineDecompositionDocumentManager extends AbstractWorkspaceTsItemManager<ExtendedAirlineDecompositionSpec, ExtendedAirlineDecompositionDocument> {


    public static final LinearId ID = new LinearId(ExtendedAirlineDecompositionSpec.FAMILY, "documents", ExtendedAirlineDecompositionSpec.METHOD);
    public static final String PATH = "extendedairlinedecomposition.doc";
    public static final String ITEMPATH = "extendedairlinedecomposition.doc.item";
    public static final String CONTEXTPATH = "extendedairlinedecomposition.doc.context";

    @Override
    protected String getItemPrefix() {
        return "ExtendedAirlineDecompositionDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public ExtendedAirlineDecompositionDocument createNewObject() {
        return new ExtendedAirlineDecompositionDocument();
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
    public Class<ExtendedAirlineDecompositionDocument> getItemClass() {
        return ExtendedAirlineDecompositionDocument.class;
    }

}
