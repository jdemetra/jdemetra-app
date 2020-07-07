/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 * @author Jean Palate
 * @since 1.0.0
 */
public abstract class AbstractWorkspaceRepository implements IWorkspaceRepository {

    private final Map<Class, List<IWorkspaceItemRepository>> map_ = new HashMap<>();

    public <D> void register(Class<D> dclass, IWorkspaceItemRepository<D> repo) {
        map_.computeIfAbsent(dclass, o -> new ArrayList<>()).add(repo);
    }

    public <D> void unregister(Class<D> dclass) {
        map_.remove(dclass);
    }

    @Nullable
    public List<IWorkspaceItemRepository> getRepositories(WorkspaceItem<?> item) {
        Class<?> mclass = WorkspaceFactory.getInstance().getManager(item.getFamily()).getItemClass();
        return getRepositories(mclass);
    }

    @Nullable
    public List<IWorkspaceItemRepository> getRepositories(Class dclass) {
        return map_.get(dclass);
    }

    @Override
    public boolean save(Workspace ws, boolean force) {
        if (ws.getDataSource() == null) {
            return false;
        }
        if (!saveWorkspace(ws)) {
            return false;
        }
        return ws.getItems().stream()
                .filter(o -> o.isDirty() || (force && !o.getStatus().isVolatile()))
                .noneMatch(o -> !saveItem(o));
    }

    protected abstract boolean saveWorkspace(Workspace ws);

    @Override
    public boolean delete(Workspace ws) {
        if (!ws.getItems().stream().noneMatch(o -> !deleteItem(o))) {
            return false;
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
        return repos.stream().anyMatch(o -> o.load(item));
    }

    @Override
    public boolean saveItem(WorkspaceItem<?> item) {
        if (!item.getStatus().canBeSaved()) {
            return true;
        }
        List<IWorkspaceItemRepository> repos = getRepositories(item);
        if (repos == null) {
            return true;
        }
        return repos.stream().anyMatch(o -> o.save(item));
    }

    @Override
    public boolean deleteItem(WorkspaceItem<?> item) {
        if (!item.getStatus().hasStorage()) {
            return true;
        }
        List<IWorkspaceItemRepository> repos = getRepositories(item);
        if (repos == null) {
            return true;
        }
        return repos.stream().anyMatch(o -> o.delete(item));
    }

    @Override
    public <D> boolean canHandleItem(Class<D> dclass) {
        return map_.containsKey(dclass);
    }
}
