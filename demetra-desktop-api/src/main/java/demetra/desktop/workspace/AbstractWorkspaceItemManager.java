/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace;

import java.util.Collections;
import java.util.List;
import javax.swing.Icon;

/**
 *
 * @author Jean Palate
 * @param <D>
 */
public abstract class AbstractWorkspaceItemManager<D> implements WorkspaceItemManager<D> {

    protected abstract String getItemPrefix();

    protected boolean isUsed(String name) {
        if (null != WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(getId(), name)) {
            return true;
        }
        if (null != WorkspaceFactory.getInstance().getActiveWorkspace().searchDocumentByName(getId(), name)) {
            return true;
        }
        return false;
    }

    public String getNextItemName() {
        return getNextItemName(null);
    }

    @Override
    public String getNextItemName(final String pname) {
        String name = pname;
        int id = 1;
        while (name == null || isUsed(name)) {
            StringBuilder builder = new StringBuilder();
            builder.append(getItemPrefix());
            builder.append("-").append(id++);
            name = builder.toString();
        }
        return name;
    }

    protected abstract D createNewObject();

    @Override
    public WorkspaceItem<D> create(Workspace ws) {
        D newObject = createNewObject();
        if (newObject == null)
            return null;
        WorkspaceItem<D> item = WorkspaceItem.newItem(getId(), getNextItemName(), newObject);
        if (ws != null) {
            ws.add(item);
        }
        return item;
    }

    @Override
    public List<WorkspaceItem<D>> getDefaultItems() {
        return Collections.emptyList();
    }

    @Override
    public Icon getItemIcon(WorkspaceItem<D> doc) {
        return getManagerIcon();
    }

    @Override
    public Icon getManagerIcon() {
        return null;
    }

    @Override
    public boolean isAutoLoad(){
        return false;
    }
    
    protected void cloneItem(WorkspaceItem<D> doc) {
    }
    
}
