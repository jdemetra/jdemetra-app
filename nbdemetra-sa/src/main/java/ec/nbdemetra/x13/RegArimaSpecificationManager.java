/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13;

import ec.nbdemetra.ws.*;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.nbdemetra.x13.actions.EditRegArimaSpec;
import ec.tss.modelling.documents.RegArimaDocument;
import ec.tstoolkit.algorithm.implementation.RegArimaProcessingFactory;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemManager.class,
position = 20)
public class RegArimaSpecificationManager extends AbstractWorkspaceItemManager<RegArimaSpecification> {

    public static final LinearId ID = new LinearId(RegArimaProcessingFactory.DESCRIPTOR.family, "specifications", RegArimaProcessingFactory.DESCRIPTOR.name);
    public static final String PATH = "regarima.spec";
    public static final String ITEMPATH = "regarima.spec.item";

    @Override
    protected String getItemPrefix() {
        return "RegArimaSpec";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected RegArimaSpecification createNewObject() {
        return RegArimaSpecification.RG5.clone();
    }

    @Override
    public ItemType getItemType() {
        return ItemType.Spec;
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
    public Action getPreferredItemAction(Id child) {
        ItemWsNode tmp = new ItemWsNode(WorkspaceFactory.getInstance().getActiveWorkspace(), child);
        final EditRegArimaSpec obj = new EditRegArimaSpec(tmp);
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obj.actionPerformed(e);
            }
        };
    }

    @Override
    public List<WorkspaceItem<RegArimaSpecification>> getDefaultItems() {
        ArrayList<WorkspaceItem<RegArimaSpecification>> defspecs = new ArrayList<>();
        defspecs.add(WorkspaceItem.system(ID, "RG0", RegArimaSpecification.RG0));
        defspecs.add(WorkspaceItem.system(ID, "RG1", RegArimaSpecification.RG1));
        defspecs.add(WorkspaceItem.system(ID, "RG2c", RegArimaSpecification.RG2));
        defspecs.add(WorkspaceItem.system(ID, "RG3", RegArimaSpecification.RG3));
        defspecs.add(WorkspaceItem.system(ID, "RG4c", RegArimaSpecification.RG4));
        defspecs.add(WorkspaceItem.system(ID, "RG5c", RegArimaSpecification.RG5));
        return defspecs;
    }

    public void createDocument(final Workspace ws, final WorkspaceItem<RegArimaSpecification> xdoc) {
        RegArimaDocumentManager dmgr = (RegArimaDocumentManager) WorkspaceFactory.getInstance().getManager(RegArimaDocumentManager.ID);
        WorkspaceItem<RegArimaDocument> doc = (WorkspaceItem<RegArimaDocument>) dmgr.create(ws);
        doc.getElement().setSpecification(xdoc.getElement());
        doc.setComments(xdoc.getComments());
        RegArimaTopComponent view = new RegArimaTopComponent(doc);
        view.open();
        view.requestActive();
    }

    @Override
    public Class<RegArimaSpecification> getItemClass() {
        return RegArimaSpecification.class;
    }
}
