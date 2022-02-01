/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace;

import demetra.util.Id;
import java.util.List;
import java.util.Optional;
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
    
        public  static <K> WorkspaceItemManager<K> forItem(Class<K> itemclass) {
        Optional<? extends WorkspaceItemManager > s = Lookup.getDefault().lookupAll(WorkspaceItemManager.class).stream()
                .filter(mgr->mgr.getItemClass().equals(itemclass)).findFirst();
      
        return s.isPresent() ? s.get() : null;
    }


    enum ItemType {
        Undefined,
        Spec,
        Doc,
        MultiDoc,
        Tool
    }

    enum Status  {
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

    Action getPreferredItemAction(Id child);

    String getNextItemName(String name);

    List<WorkspaceItem<D>> getDefaultItems();

    WorkspaceItem<D> create(Workspace ws);

    boolean isAutoLoad();

    Icon getItemIcon(WorkspaceItem<D> doc);

    Icon getManagerIcon();
}
