/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.disaggregation.documents;

import demetra.desktop.workspace.AbstractWorkspaceItemManager;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.tempdisagg.univariate.TemporalDisaggregationSpec;
import demetra.util.Id;
import demetra.util.LinearId;
import jdplus.tempdisagg.univariate.TemporalDisaggregationDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemManager.class,
        position = 500)
public class TemporalDisaggregationDocumentManager extends AbstractWorkspaceItemManager<TemporalDisaggregationDocument> {


    public static final LinearId ID = new LinearId(TemporalDisaggregationSpec.FAMILY, "documents", TemporalDisaggregationSpec.METHOD);
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
    public TemporalDisaggregationDocument createNewObject() {
        return new TemporalDisaggregationDocument();
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
    public Class<TemporalDisaggregationDocument> getItemClass() {
        return TemporalDisaggregationDocument.class;
    }

}
