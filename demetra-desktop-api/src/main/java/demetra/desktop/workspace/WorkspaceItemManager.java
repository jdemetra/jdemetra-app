/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace;

import demetra.util.Id;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import nbbrd.service.ServiceDefinition;

/**
 *
 * @author Jean Palate
 */
@ServiceDefinition
public interface WorkspaceItemManager<D> {

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
