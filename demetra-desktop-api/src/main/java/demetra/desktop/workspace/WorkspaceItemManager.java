/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace;

import demetra.util.Id;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Optional;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import nbbrd.service.ServiceDefinition;
import org.openide.util.Lookup;

/**
 *
 * @author Jean Palate
 * @param <D>
 */
@ServiceDefinition
public interface WorkspaceItemManager<D> {

    public static <K> WorkspaceItemManager<K> forItem(Class<K> itemclass) {
        Optional<? extends WorkspaceItemManager> s = Lookup.getDefault().lookupAll(WorkspaceItemManager.class).stream()
                .filter(mgr -> mgr.getItemClass().equals(itemclass)).findFirst();

        return s.isPresent() ? s.get() : null;
    }

    enum ItemType {
        Undefined,
        Spec,
        Doc,
        MultiDoc,
        Tool
    }

    enum Status {
        Certified,
        Acceptable,
        Legacy,
        Experimental,
        User;

    }

    Status getStatus();

    ItemType getItemType();

    Class<D> getItemClass();

    D createNewObject();

    Id getId();

    String getActionsPath();

    default Action getPreferredItemAction(final Id child) {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                WorkspaceItem<D> doc = (WorkspaceItem<D>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    DocumentUIServices ui = DocumentUIServices.forDocument(doc.getElement().getClass());
                    if (ui != null)
                        ui.showDocument(doc);
                }
            }
        };
    }

    String getNextItemName(String name);

    List<WorkspaceItem<D>> getDefaultItems();

    WorkspaceItem<D> create(Workspace ws);

    boolean isAutoLoad();

    default Icon getManagerIcon() {
        DocumentUIServices ui = DocumentUIServices.forDocument(getItemClass());
        if (ui != null) {
            return ui.getIcon();
        } else {
            return null;
        }
    }
    
    default Icon getItemIcon(WorkspaceItem<D> doc) {
        DocumentUIServices ui = DocumentUIServices.forDocument(getItemClass());
        if (ui != null) {
            return ui.getItemIcon(doc);
        } else {
            return null;
        }
    }
    
}
