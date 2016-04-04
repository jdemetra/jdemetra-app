/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats;

import ec.nbdemetra.tramoseats.actions.EditTramoSeatsSpec;
import ec.nbdemetra.ws.*;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.satoolkit.algorithm.implementation.TramoSeatsProcessingFactory;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tss.sa.documents.TramoSeatsDocument;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemManager.class, position = 1000)
public class TramoSeatsSpecificationManager extends AbstractWorkspaceItemManager<TramoSeatsSpecification> {

    public static final LinearId ID = new LinearId(TramoSeatsProcessingFactory.DESCRIPTOR.family, WorkspaceFactory.SPECIFICATIONS, TramoSeatsProcessingFactory.DESCRIPTOR.name);
    public static final String PATH = "tramoseats.spec";
    public static final String ITEMPATH = "tramoseats.spec.item";

    @Override
    protected String getItemPrefix() {
        return "TramoSeatsSpec";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected TramoSeatsSpecification createNewObject() {
        return TramoSeatsSpecification.RSAfull.clone();
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
    public Action getPreferredItemAction(final Id child) {
        ItemWsNode tmp = new ItemWsNode(WorkspaceFactory.getInstance().getActiveWorkspace(), child);
        final EditTramoSeatsSpec obj = new EditTramoSeatsSpec(tmp);
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obj.actionPerformed(e);
            }
        };
    }

    @Override
    public List<WorkspaceItem<TramoSeatsSpecification>> getDefaultItems() {
        List<WorkspaceItem<TramoSeatsSpecification>> result = new ArrayList<>();
        result.add(WorkspaceItem.system(ID, "RSA0", TramoSeatsSpecification.RSA0));
        result.add(WorkspaceItem.system(ID, "RSA1", TramoSeatsSpecification.RSA1));
        result.add(WorkspaceItem.system(ID, "RSA2", TramoSeatsSpecification.RSA2));
        result.add(WorkspaceItem.system(ID, "RSA3", TramoSeatsSpecification.RSA3));
        result.add(WorkspaceItem.system(ID, "RSA4", TramoSeatsSpecification.RSA4));
        result.add(WorkspaceItem.system(ID, "RSA5", TramoSeatsSpecification.RSA5));
        result.add(WorkspaceItem.system(ID, "RSAfull", TramoSeatsSpecification.RSAfull));
        return result;
    }

    public void createDocument(final Workspace ws, final WorkspaceItem<TramoSeatsSpecification> xdoc) {
        TramoSeatsDocumentManager dmgr = (TramoSeatsDocumentManager) WorkspaceFactory.getInstance().getManager(TramoSeatsDocumentManager.ID);
        WorkspaceItem<TramoSeatsDocument> doc = (WorkspaceItem<TramoSeatsDocument>) dmgr.create(ws);
        doc.getElement().setSpecification(xdoc.getElement());
        TramoSeatsTopComponent view = new TramoSeatsTopComponent(doc);
        view.open();
        view.requestActive();
    }

    @Override
    public Class<TramoSeatsSpecification> getItemClass() {
        return TramoSeatsSpecification.class;
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/sa/blog-blue_16x16.png", false);
    }
}
