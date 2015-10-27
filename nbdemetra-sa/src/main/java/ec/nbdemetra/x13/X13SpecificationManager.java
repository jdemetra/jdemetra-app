/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13;

import ec.nbdemetra.x13.actions.EditX13Spec;
import ec.nbdemetra.ws.*;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.satoolkit.algorithm.implementation.X13ProcessingFactory;
import ec.satoolkit.x13.X13Specification;
import ec.tss.sa.documents.X13Document;
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
@ServiceProvider(service = IWorkspaceItemManager.class,
position = 1020)
public class X13SpecificationManager extends AbstractWorkspaceItemManager<X13Specification> {

    public static final LinearId ID = new LinearId(X13ProcessingFactory.DESCRIPTOR.family, "specifications", X13ProcessingFactory.DESCRIPTOR.name);
    public static final String PATH = "x13.spec";
    public static final String ITEMPATH = "x13.spec.item";

//    static {
//        FileRepository repo = WorkspaceFactory.getInstance().getRepository(FileRepository.class);
//        if (repo != null) {
//            repo.register(X13Specification.class, new X13SpecFileRepository());
//        }
//
//    }
    @Override
    protected String getItemPrefix() {
        return "X13Spec";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected X13Specification createNewObject() {
        return X13Specification.RSA5.clone();
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
        final EditX13Spec obj = new EditX13Spec(tmp);
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obj.actionPerformed(e);
            }
        };
    }

    @Override
    public List<WorkspaceItem<X13Specification>> getDefaultItems() {
        List<WorkspaceItem<X13Specification>> result = new ArrayList<>();
        result.add(WorkspaceItem.system(ID, "X11", X13Specification.RSAX11));
         result.add(WorkspaceItem.system(ID, "RSA0", X13Specification.RSA0));
        result.add(WorkspaceItem.system(ID, "RSA1", X13Specification.RSA1));
        result.add(WorkspaceItem.system(ID, "RSA2c", X13Specification.RSA2));
        result.add(WorkspaceItem.system(ID, "RSA3", X13Specification.RSA3));
        result.add(WorkspaceItem.system(ID, "RSA4c", X13Specification.RSA4));
        result.add(WorkspaceItem.system(ID, "RSA5c", X13Specification.RSA5));
        return result;
    }

    public void createDocument(final Workspace ws, final WorkspaceItem<X13Specification> xdoc) {
        X13DocumentManager dmgr = (X13DocumentManager) WorkspaceFactory.getInstance().getManager(X13DocumentManager.ID);
        WorkspaceItem<X13Document> doc = (WorkspaceItem<X13Document>) dmgr.create(ws);
        doc.getElement().setSpecification(xdoc.getElement());
        X13TopComponent view = new X13TopComponent(doc);
        view.open();
        view.requestActive();
    }

    @Override
    public Class<X13Specification> getItemClass() {
        return X13Specification.class;
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/sa/blog_16x16.png", false);
    }
}
