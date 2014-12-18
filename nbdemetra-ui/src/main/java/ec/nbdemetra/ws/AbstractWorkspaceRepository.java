/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author pcuser
 */
public abstract class AbstractWorkspaceRepository implements IWorkspaceRepository {
    
    private HashMap<Class, ArrayList<IWorkspaceItemRepository>> map_ =
            new HashMap<>();
    
    public <D> void register(Class<D> dclass, IWorkspaceItemRepository<D> repo) {
        ArrayList<IWorkspaceItemRepository> list = map_.get(dclass);
        if (list == null) {
            list = new ArrayList<>();
            map_.put(dclass, list);
        }
        list.add(repo);
    }
    
    public <D> void unregister(Class<D> dclass) {
        map_.remove(dclass);
    }
    
    public List<IWorkspaceItemRepository> getRepositories(WorkspaceItem<?> item) {
        Class<?> mclass=WorkspaceFactory.getInstance().getManager(item.getFamily()).getItemClass();
        return getRepositories(mclass);
    }
    
    public List<IWorkspaceItemRepository> getRepositories(Class dclass) {
        return (List) map_.get(dclass);
    }
    
    @Override
    public Object getProperties() {
        return null;
    }
    
    @Override
    public void setProperties() {
    }
    
    @Override
    public boolean save(Workspace ws, boolean force) {
        if (ws.getDataSource() == null)
            return false;
        if (! saveWorkspace(ws))
            return false;
        for (WorkspaceItem<?> item : ws.getItems()) {
            if (item.isDirty() || (force && ! item.getStatus().isVolatile())) {
                if (!saveItem(item)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    protected abstract boolean saveWorkspace(Workspace ws);
    
    @Override
    public boolean delete(Workspace ws) {
        for (WorkspaceItem<?> item : ws.getItems()) {
            if (!deleteItem(item)) {
                return false;
            }
        }
        return deleteWorkspace(ws);
    }
    
    protected abstract boolean deleteWorkspace(Workspace ws);
    
    @Override
    public void close(Workspace ws_) {
    }
    
    @Override
    public boolean loadItem(WorkspaceItem<?> item) {
        IWorkspaceItemManager<?> manager = WorkspaceFactory.getInstance().getManager(item.getFamily());
        if (manager == null) {
            return false;
        }
        List<IWorkspaceItemRepository> repos = getRepositories(manager.getItemClass());
        if (repos == null) {
            return false;
        }
        for (IWorkspaceItemRepository repo : repos) {
            if (repo.load(item)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean saveItem(WorkspaceItem<?> item) {
        if (! item.getStatus().canBeSaved()){
            return true;
        }
        List<IWorkspaceItemRepository> repos = getRepositories(item);
        if (repos == null) {
            return true;
        }
        for (IWorkspaceItemRepository repo : repos) {
            if (repo.save(item)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean deleteItem(WorkspaceItem<?> item) {
        if (! item.getStatus().hasStorage()){
            return true;
        }
        List<IWorkspaceItemRepository> repos = getRepositories(item);
        if (repos == null) {
            return true;
        }
        for (IWorkspaceItemRepository repo : repos) {
            if (repo.delete(item)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public <D> boolean canHandleItem(Class<D> dclass) {
        return map_.containsKey(dclass);
    }
}
