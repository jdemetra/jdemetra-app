/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws;

import ec.tstoolkit.utilities.IModifiable;
import ec.tstoolkit.utilities.Id;
import java.util.Comparator;
import org.openide.windows.TopComponent;

/**
 *
 * @author Kristof Bayens
 */
public class WorkspaceItem<T> implements IModifiable, Comparable<WorkspaceItem> {

    @Override
    public int compareTo(WorkspaceItem o) {
        int cmp = this.family_.compareTo(o.family_);
        if (cmp != 0) {
            return cmp;
        }
        cmp = this.status_.compareTo(o.status_);
        if (cmp != 0) {
            return cmp;
        }
        return this.name_.compareTo(o.name_);
    }

    public static enum Status {

        System, Temporary, New, Undefined, Valid, Invalid;

        public boolean isVolatile() {
            return this == System || this == Temporary;
        }

        public boolean canBeLoaded() {
            return this == Undefined;
        }

        public boolean canBeSaved() {
            return this == New || this == Valid;
        }

        public boolean hasStorage() {
            return this == Valid || this == Undefined || this == Invalid;
        }
    }
    
    private Workspace owner_;
    private final Id family_;
    private T element_;
    private String name_;
    private boolean dirty_;
    private String id_;
    private Status status_ = Status.Undefined;
    private TopComponent view_;

    public static <T> WorkspaceItem<T> system(Id family, String name, T element) {
        WorkspaceItem<T> item = new WorkspaceItem(family, name, element);
        item.status_ = Status.System;
        return item;
    }

    public static <T> WorkspaceItem<T> temporary(Id family, String name, T element) {
        WorkspaceItem<T> item = new WorkspaceItem(family, name, element);
        item.status_ = Status.Temporary;
        return item;
    }

    public static <T> WorkspaceItem<T> newItem(Id family, String name, T element) {
        WorkspaceItem<T> item = new WorkspaceItem(family, name, element);
        item.status_ = Status.New;
        item.dirty_ = true;
        return item;
    }

    public static <T> WorkspaceItem<T> item(Id family, String name, String id) {
        WorkspaceItem<T> item = new WorkspaceItem(family, name, id);
        item.status_ = Status.Undefined;
        return item;
    }

    private WorkspaceItem(Id family, String name, T element) {
        family_ = family;
        name_ = name;
        id_ = name;
        element_ = element;
    }

    private WorkspaceItem(Id family, String name, String id) {
        family_ = family;
        name_ = name;
        id_ = id;
        if (id == null) {
            id_ = name;
        }
    }

    public Id getFamily() {
        return family_;
    }

    public Id getId() {
        return family_.extend(id_);
    }

    public Workspace getOwner() {
        return owner_;
    }

    public Status getStatus() {
        return status_;
    }

    void setStatus(Status status) {
        status_ = status;
    }

    public T getElement() {
        load();
        return element_;
    }

    public boolean load() {
        if (!status_.canBeLoaded()) {
            return false;
        }
        if (owner_.getRepository().loadItem(this)) {
            status_ = Status.Valid;
            return true;
        } else {
            status_ = Status.Invalid;
            return false;
        }
    }

    public boolean close() {
        if (!status_.canBeSaved()) {
            return false;
        }
        if (owner_.getRepository().saveItem(this)) {
            closeView();
            this.element_ = null;
            status_ = Status.Undefined;
            return true;
        } else {
            return false;
        }
    }
    
    public boolean reload() {
        if (!status_.hasStorage()) {
            return false;
        }
        element_ = null;
        status_ = Status.Undefined;
        return load();
    }

    void setOwner(Workspace owner) {
        owner_ = owner;
    }

    public void setElement(T element) {
        element_ = element;
        dirty_ = true;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    //IWorkspaceItem Members
    public String getDisplayName() {
        return name_;
    }

    public void setDisplayName(String value) {
        if (!name_.equals(value)) {
            name_ = value;
            if (view_ != null) {
                view_.setName(value);
            }
            dirty_ = true;
        }
    }

    public String getIdentifier() {
        return id_;
    }

    public void setIdentifier(String value) {
        if (!id_.equals(value)) {
            id_ = value;
            dirty_ = true;
        }
    }

    public boolean isReadOnly() {
        return status_ == Status.System;
    }

    @Override
    public boolean isDirty() {
        if (dirty_) {
            return true;
        }
        if (element_ != null && element_ instanceof IModifiable) {
            return ((IModifiable) element_).isDirty();
        } else {
            return false;
        }
    }

    @Override
    public void resetDirty() {
        if (element_ != null && element_ instanceof IModifiable) {
            ((IModifiable) element_).resetDirty();
        }
        dirty_ = false;
    }

    public boolean isOpen() {
        return view_ != null;
    }

    public void setView(TopComponent view) {
        view_ = view;
    }

    public TopComponent getView() {
        return view_;
    }

    public boolean closeView() {
        try {
            if (view_ == null) {
                return true;
            }
            if (!view_.canClose()) {
                return false;
            }
            view_.close();
            return true;
        } catch (Exception err) {
            // shut down. Windows API could be not available
            return true;
        }
    }
    
    public void notify(int event, Object source){
        WorkspaceFactory.getInstance().notifyEvent(new WorkspaceFactory.Event(owner_, getId(), event, source));
    }

    public static class InnerComparator implements Comparator<WorkspaceItem<?>> {

        private final Id family_;
        
        public InnerComparator(Id family){
            family_=family;            
        }
        
        @Override
        public int compare(WorkspaceItem<?> o1, WorkspaceItem<?> o2) {
            if (o1.family_.equals(family_) 
                    && o1.family_.equals(family_)) {
                return o1.compareTo(o2);
            } else {
                return 0;
            }
        }
    }
}
