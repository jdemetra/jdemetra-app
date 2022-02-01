/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.x13.ui;

import demetra.desktop.x13.descriptors.X13SpecUI;
import demetra.desktop.x13.ui.actions.EditX13Spec;
import demetra.desktop.ui.properties.l2fprod.PropertiesDialog;
import demetra.desktop.workspace.AbstractWorkspaceItemManager;
import demetra.desktop.workspace.Workspace;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.x13.X13Spec;
import demetra.util.Id;
import demetra.util.LinearId;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import jdplus.x13.X13Document;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemManager.class, position = 1000)
public class X13SpecManager extends AbstractWorkspaceItemManager<X13Spec> {

    public static final LinearId ID = new LinearId(X13Spec.FAMILY, WorkspaceFactory.SPECIFICATIONS, X13Spec.METHOD);
    public static final String PATH = "x13.spec";
    public static final String ITEMPATH = "x13.spec.item";

    @Override
    protected String getItemPrefix() {
        return "X13Spec";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public X13Spec createNewObject() {
        return X13Spec.RSA4;
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
        final EditX13Spec obj = new EditX13Spec(tmp);
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obj.actionPerformed(e);
            }
        };
    }

    @Override
    public List<WorkspaceItem<X13Spec>> getDefaultItems() {
        List<WorkspaceItem<X13Spec>> result = new ArrayList<>();
        result.add(WorkspaceItem.system(ID, "RSA0", X13Spec.RSA0));
        result.add(WorkspaceItem.system(ID, "RSA1", X13Spec.RSA1));
        result.add(WorkspaceItem.system(ID, "RSA2", X13Spec.RSA2));
        result.add(WorkspaceItem.system(ID, "RSA3", X13Spec.RSA3));
        result.add(WorkspaceItem.system(ID, "RSA4", X13Spec.RSA4));
        result.add(WorkspaceItem.system(ID, "RSA5", X13Spec.RSA5));
        return result;
    }

    public void createDocument(final Workspace ws, final WorkspaceItem<X13Spec> xdoc) {
        X13DocumentManager dmgr = (X13DocumentManager) WorkspaceFactory.getInstance().getManager(X13DocumentManager.ID);
        WorkspaceItem<X13Document> doc = dmgr.create(ws);
        doc.getElement().set(xdoc.getElement());
        doc.setComments(xdoc.getComments());
        X13TopComponent view = new X13TopComponent(doc);
        view.open();
        view.requestActive();
    }

    public void edit(final WorkspaceItem<X13Spec> xdoc) {
        if (xdoc == null || xdoc.getElement() == null) {
            return;

        }
        final X13SpecUI ui = new X13SpecUI(xdoc.getElement(), xdoc.isReadOnly());
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
    public Class<X13Spec> getItemClass() {
        return X13Spec.class;
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/sa/blog-blue_16x16.png", false);
    }
}
