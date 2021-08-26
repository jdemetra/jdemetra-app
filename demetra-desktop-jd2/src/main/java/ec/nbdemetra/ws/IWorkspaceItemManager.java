/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws;

import ec.tstoolkit.design.IntValue;
import ec.tstoolkit.design.ServiceDefinition;
import ec.tstoolkit.utilities.Id;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;

/**
 *
 * @author Jean Palate
 */
@ServiceDefinition
public interface IWorkspaceItemManager<D> {

    enum ItemType {
        Undefined,
        Spec,
        Doc,
        MultiDoc,
        Tool
    }

    enum Status implements IntValue {
        Certified(1),
        Acceptable(2),
        Legacy(3),
        Experimental(5),
        User(10);

        Status(int value) {
            this.value = value;
        }

        private final int value;

        @Override
        public int intValue() {
            return value;
        }
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
