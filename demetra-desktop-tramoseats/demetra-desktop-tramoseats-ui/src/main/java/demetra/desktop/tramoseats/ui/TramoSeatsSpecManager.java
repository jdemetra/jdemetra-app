/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramoseats.ui;

import demetra.desktop.tramoseats.descriptors.TramoSeatsSpecUI;
import demetra.desktop.tramoseats.ui.actions.EditTramoSeatsSpec;
import demetra.desktop.ui.properties.l2fprod.PropertiesDialog;
import demetra.desktop.workspace.AbstractWorkspaceItemManager;
import demetra.desktop.workspace.Workspace;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.tramoseats.TramoSeatsSpec;
import demetra.util.Id;
import demetra.util.LinearId;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import jdplus.tramoseats.TramoSeatsDocument;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemManager.class, position = 1000)
public class TramoSeatsSpecManager extends AbstractWorkspaceItemManager<TramoSeatsSpec> {

    public static final LinearId ID = new LinearId(TramoSeatsSpec.FAMILY, WorkspaceFactory.SPECIFICATIONS, TramoSeatsSpec.METHOD);
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
    public TramoSeatsSpec createNewObject() {
        return TramoSeatsSpec.RSAfull;
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
    public List<WorkspaceItem<TramoSeatsSpec>> getDefaultItems() {
        List<WorkspaceItem<TramoSeatsSpec>> result = new ArrayList<>();
        result.add(WorkspaceItem.system(ID, "RSA0", TramoSeatsSpec.RSA0));
        result.add(WorkspaceItem.system(ID, "RSA1", TramoSeatsSpec.RSA1));
        result.add(WorkspaceItem.system(ID, "RSA2", TramoSeatsSpec.RSA2));
        result.add(WorkspaceItem.system(ID, "RSA3", TramoSeatsSpec.RSA3));
        result.add(WorkspaceItem.system(ID, "RSA4", TramoSeatsSpec.RSA4));
        result.add(WorkspaceItem.system(ID, "RSA5", TramoSeatsSpec.RSA5));
        result.add(WorkspaceItem.system(ID, "RSAfull", TramoSeatsSpec.RSAfull));
        return result;
    }

    public void createDocument(final Workspace ws, final WorkspaceItem<TramoSeatsSpec> xdoc) {
        TramoSeatsDocumentManager dmgr = (TramoSeatsDocumentManager) WorkspaceFactory.getInstance().getManager(TramoSeatsDocumentManager.ID);
        WorkspaceItem<TramoSeatsDocument> doc = dmgr.create(ws);
        doc.getElement().set(xdoc.getElement());
        doc.setComments(xdoc.getComments());
        TramoSeatsTopComponent view = new TramoSeatsTopComponent(doc);
        view.open();
        view.requestActive();
    }

    public void edit(final WorkspaceItem<TramoSeatsSpec> xdoc) {
        if (xdoc == null || xdoc.getElement() == null) {
            return;

        }
        final TramoSeatsSpecUI ui = new TramoSeatsSpecUI(xdoc.getElement(), xdoc.isReadOnly());
        PropertiesDialog propDialog =
                new PropertiesDialog(WindowManager.getDefault().getMainWindow(), true, ui,
                new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                xdoc.setElement(ui.getCore());
            }
        });
        propDialog.setTitle(xdoc.getDisplayName());
        propDialog.setVisible(true);
    }
    
    @Override
    public Class<TramoSeatsSpec> getItemClass() {
        return TramoSeatsSpec.class;
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/sa/blog-blue_16x16.png", false);
    }
}
