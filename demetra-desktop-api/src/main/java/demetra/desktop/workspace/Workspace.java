/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace;

import demetra.timeseries.regression.ModellingContext;
import demetra.tsprovider.DataSource;
import demetra.desktop.interfaces.Disposable;
import demetra.desktop.ui.mru.SourceId;
import demetra.desktop.workspace.WorkspaceItemManager.ItemType;
import demetra.util.Id;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;

/**
 *
 * @author Kristof Bayens & PALATEJ Most is taken over from the existing .Net
 * version of JDemetra. Stuff marked "New by Jean" is well... new by Jean.
 */
public class Workspace implements Disposable {

    static final AtomicLong wsId = new AtomicLong(0);
    static final AtomicLong curId = new AtomicLong(0);
    private SourceId id;
    private final ModellingContext context_ = new ModellingContext();
    private boolean dirty_ = false;
    private final List<WorkspaceItem<?>> items_ = new ArrayList<>();
    private final HashMap<Id, Id> defaultSpecs_ = new HashMap<>();

    public Workspace(DataSource source) {
        id = new SourceId(source, "Workspace-" + wsId.incrementAndGet());
        addDefaultItems();
    }

    public Workspace(DataSource source, String name) {
        id = new SourceId(source, name);
        addDefaultItems();
    }

    public WorkspaceRepository getRepository() {
        return WorkspaceFactory.getInstance().getRepository(id.getDataSource().getProviderName());
    }

    public void add(WorkspaceItem<?> item) {
        item.setOwner(this);
        items_.add(item);
        if (!item.getStatus().isVolatile()) {
            dirty_ = true;
        }

        WorkspaceFactory.Event ev = new WorkspaceFactory.Event(this, item.getId(), WorkspaceFactory.Event.ITEMADDED);
        WorkspaceFactory.getInstance().notifyEvent(ev);
    }

    public void sort() {
        Collections.sort(items_);
        dirty_ = true;
        WorkspaceFactory.Event ev = new WorkspaceFactory.Event(this, null, WorkspaceFactory.Event.SORT);
        WorkspaceFactory.getInstance().notifyEvent(ev);
    }

    public void sortFamily(Id family) {
        items_.sort(new WorkspaceItem.InnerComparator(family));
        dirty_ = true;
        WorkspaceFactory.Event ev = new WorkspaceFactory.Event(this, null, WorkspaceFactory.Event.SORT);
        WorkspaceFactory.getInstance().notifyEvent(ev);
    }

    public void quietAdd(WorkspaceItem<?> item) {
        item.setOwner(this);
        items_.add(item);
    }

    public void quietRemove(WorkspaceItem<?> item) {
        item.setOwner(null);
        items_.remove(item);
    }

    public void remove(WorkspaceItem<?> item) {
        WorkspaceFactory.Event ev = new WorkspaceFactory.Event(this, item.getId(), WorkspaceFactory.Event.REMOVINGITEM);
        WorkspaceFactory.getInstance().notifyEvent(ev);
        item.setOwner(null);
        items_.remove(item);
        dirty_ = true;
    }

    public void rename(WorkspaceItem<?> item, String newName) {
        item.setDisplayName(newName);
        WorkspaceFactory.Event ev = new WorkspaceFactory.Event(this, item.getId(), WorkspaceFactory.Event.ITEMRENAMED);
        WorkspaceFactory.getInstance().notifyEvent(ev);
    }

    public <T> WorkspaceItem<T> searchDocumentByElement(T element) {
        for (WorkspaceItem<?> item : items_) {
            if (item.getElement() == element) {
                return (WorkspaceItem<T>) item;
            }
        }
        return null;
    }

    public WorkspaceItem<?> searchDocument(Id family, String name) {
        for (WorkspaceItem<?> item : items_) {
            if (name.equals(item.getIdentifier()) && family.equals(item.getFamily())) {
                return item;
            }
        }
        return null;
    }

    public WorkspaceItem<?> searchDocument(Id id) {
        for (WorkspaceItem<?> item : items_) {
            if (id.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }

    public <T> WorkspaceItem<T> searchDocument(Id id, Class<T> tclass) {
        WorkspaceItemManager<?> manager = WorkspaceFactory.getInstance().getManager(id.parent());
        if (manager == null) {
            return null;
        }
        Class<?> mclass = manager.getItemClass();
        if (!tclass.isAssignableFrom(mclass)) {
            return null;
        }
        for (WorkspaceItem<?> item : items_) {
            if (id.equals(item.getId())) {
                return (WorkspaceItem<T>) item;
            }
        }
        return null;
    }

    public List<WorkspaceItem<?>> searchDocuments(Id family) {
        ArrayList<WorkspaceItem<?>> sel = new ArrayList<>();
        for (WorkspaceItem item : items_) {
            if (family.equals(item.getFamily())) {
                sel.add(item);
            }
        }
        return sel;
    }

    public <T> List<WorkspaceItem<T>> searchDocuments(Class<T> tclass) {
        ArrayList<WorkspaceItem<T>> sel = new ArrayList<>();
        for (WorkspaceItem item : items_) {
            WorkspaceItemManager<?> manager = WorkspaceFactory.getInstance().getManager(item.getFamily());
            if (manager == null) {
                continue;
            }
            Class<?> mclass = manager.getItemClass();
            if (tclass.isAssignableFrom(mclass)) {
                sel.add(item);
            }
        }
        return sel;
    }

    public List<WorkspaceItem<?>> searchCompatibleDocuments(Id family) {
        ArrayList<WorkspaceItem<?>> sel = new ArrayList<>();
        for (WorkspaceItem item : items_) {
            if (item.getFamily().startsWith(family)) {
                sel.add(item);
            }
        }
        return sel;
    }

    public WorkspaceItem<?> searchDocumentByName(Id family, String displayName) {
        for (WorkspaceItem<?> item : items_) {
            if (family.equals(item.getFamily()) && item.getDisplayName().equals(displayName)) {
                return item;
            }
        }
        return null;
    }

    public List<WorkspaceItem<?>> getItems() {
        return Collections.unmodifiableList(items_);
    }

    public String getName() {
        return id.getLabel();
    }

    public ModellingContext getContext() {
        return context_;
    }

    public void setName(String value) {
        id = new SourceId(id.getDataSource(), value);
        dirty_ = true;
    }

    public DataSource getDataSource() {
        return id.getDataSource();
    }

    public void setDataSource(DataSource value) {
        id = new SourceId(value, id.getLabel());
        dirty_ = true;
    }

    public boolean isDirty() {
        if (dirty_) {
            return true;
        }
        if (context_.isDirty()) {
            return true;
        }
        for (WorkspaceItem<?> item : items_) {
            if (item.isDirty()) {
                return true;
            }
        }
        return false;
    }

    public void resetDirty() {
        dirty_ = false;
    }

    private void addDefaultItems() {
        for (WorkspaceItemManager mgr : WorkspaceFactory.getInstance().getManagers()) {
            List<WorkspaceItem<?>> defaultItems = mgr.getDefaultItems();
            if (defaultItems != null) {
                items_.addAll(defaultItems);
            }
        }
    }

    public boolean setDefaultSpec(Id family, Id id) {
        if (id == null) {
            defaultSpecs_.remove(family);
            return true;
        } else {
            if (!id.startsWith(family)) {
                return false;
            }
            WorkspaceItem<?> spec = searchDocument(id);
            if (spec == null) {
                return false;
            }
            if (WorkspaceFactory.getInstance().getManager(spec.getFamily()).getItemType() != ItemType.Spec) {
                return false;
            }
            defaultSpecs_.put(family, id);
            return true;
        }
    }

    public Id getDefaultSpec(Id family) {
        return defaultSpecs_.get(family);
    }

    public void loadAll() {
        for (WorkspaceItem<?> item : items_) {
            item.load();
        }
    }

    public void save() {
        if (!isDirty()) {
            return;
        }
        getRepository().save(this, false);
        WorkspaceFactory.Event ev = new WorkspaceFactory.Event(this, null, WorkspaceFactory.Event.SAVE);
        WorkspaceFactory.getInstance().notifyEvent(ev);
    }

//    public void forceSave() {
//        getRepository().save(this, true);
//    }
    public void saveAs() {
        getRepository().saveAs(this);
        WorkspaceFactory.Event ev = new WorkspaceFactory.Event(this, null, WorkspaceFactory.Event.SAVEAS);
        WorkspaceFactory.getInstance().notifyEvent(ev);
    }

    public boolean hasOpenItems() {
        for (WorkspaceItem<?> item : items_) {
            if (item.isOpen()) {
                return true;
            }
        }
        return false;
    }

    public boolean closeOpenDocuments() {
        for (final WorkspaceItem<?> item : items_) {
            if (!SwingUtilities.isEventDispatchThread()) {
                try {
                    // could happen during the shut down...
                    SwingUtilities.invokeAndWait(item::closeView);
                } catch (InterruptedException | InvocationTargetException ex) {
                    Thread.currentThread().interrupt();
                    Exceptions.printStackTrace(ex);
                }
            } else if (!item.closeView()) {
                return false;
            }
        }
        return true;
    }

    public SourceId getSourceId() {
        return id;
    }

    @Override
    public void dispose() {
        for (WorkspaceItem<?> item : items_) {
            item.setOwner(null);
        }
        items_.clear();
    }
}
