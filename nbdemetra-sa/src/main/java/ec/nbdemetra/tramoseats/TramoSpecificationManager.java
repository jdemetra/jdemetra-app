/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats;

import ec.nbdemetra.tramoseats.actions.EditTramoSpec;
import ec.nbdemetra.ui.properties.l2fprod.PropertiesDialog;
import ec.nbdemetra.ws.*;
import ec.tss.modelling.documents.TramoDocument;
import ec.tstoolkit.algorithm.implementation.TramoProcessingFactory;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.nbdemetra.tramoseats.descriptors.TramoSpecUI;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

/**
 *
 * @author pcuser
 */
@ServiceProvider(service = IWorkspaceItemManager.class,
        position = 10)
public class TramoSpecificationManager extends AbstractWorkspaceItemManager<TramoSpecification> {

    public static final LinearId ID = new LinearId(TramoProcessingFactory.DESCRIPTOR.family, "specifications", TramoProcessingFactory.DESCRIPTOR.name);
    public static final String PATH = "tramo.spec";
    public static final String ITEMPATH = "tramo.spec.item";

//    static {
//        FileRepository repo = WorkspaceFactory.getInstance().getRepository(FileRepository.class);
//        if (repo != null) {
//            repo.register(TramoSpecification.class, new TramoSpecFileRepository());
//        }
//    }
    @Override
    protected String getItemPrefix() {
        return "TramoSpec";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected TramoSpecification createNewObject() {
        return TramoSpecification.TR5.clone();
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
        final EditTramoSpec obj = new EditTramoSpec(tmp);
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obj.actionPerformed(e);
            }
        };
    }

    @Override
    public List<WorkspaceItem<TramoSpecification>> getDefaultItems() {
        ArrayList<WorkspaceItem<TramoSpecification>> defspecs = new ArrayList<>();
        WorkspaceItem<TramoSpecification> rsa0 = WorkspaceItem.system(ID, "TR0", TramoSpecification.TR0);
        defspecs.add(rsa0);
        WorkspaceItem<TramoSpecification> rsa1 = WorkspaceItem.system(ID, "TR1", TramoSpecification.TR1);
        defspecs.add(rsa1);
        WorkspaceItem<TramoSpecification> rsa2 = WorkspaceItem.system(ID, "TR2", TramoSpecification.TR2);
        defspecs.add(rsa2);
        WorkspaceItem<TramoSpecification> rsa3 = WorkspaceItem.system(ID, "TR3", TramoSpecification.TR3);
        defspecs.add(rsa3);
        WorkspaceItem<TramoSpecification> rsa4 = WorkspaceItem.system(ID, "TR4", TramoSpecification.TR4);
        defspecs.add(rsa4);
        WorkspaceItem<TramoSpecification> rsa5 = WorkspaceItem.system(ID, "TR5", TramoSpecification.TR5);
        defspecs.add(rsa5);
        WorkspaceItem<TramoSpecification> rsafull = WorkspaceItem.system(ID, "TRfull", TramoSpecification.TRfull);
        defspecs.add(rsafull);
        return defspecs;
    }

    public void createDocument(final Workspace ws, final WorkspaceItem<TramoSpecification> xdoc) {
        TramoDocumentManager dmgr = (TramoDocumentManager) WorkspaceFactory.getInstance().getManager(TramoDocumentManager.ID);
        WorkspaceItem<TramoDocument> doc = (WorkspaceItem<TramoDocument>) dmgr.create(ws);
        doc.getElement().setSpecification(xdoc.getElement());
        TramoTopComponent view = new TramoTopComponent(doc);
        view.open();
        view.requestActive();
    }

    public void edit(final WorkspaceItem<TramoSpecification> xdoc) {
        if (xdoc == null || xdoc.getElement() == null) {
            return;

        }
        final TramoSpecUI ui = new TramoSpecUI(xdoc.getElement().clone(), xdoc.isReadOnly());
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
    public Class getItemClass() {
        return TramoSpecification.class;
    }
}
