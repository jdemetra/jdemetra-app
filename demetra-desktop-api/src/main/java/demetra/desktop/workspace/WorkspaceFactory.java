/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace;

import demetra.DemetraVersion;
import demetra.timeseries.regression.ModellingContext;
import demetra.util.Id;
import demetra.util.TreeOfIds;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Jean Palate
 */
public class WorkspaceFactory implements LookupListener {

    private static final String CLOSE_MSG = "Do you want to save the changes you made to the workspace?";
    public static final String WSCONTEXTPATH = "ws.common.context";
    public static final String TSCONTEXTPATH = "ws.common.context.ts";
    public static final String SPECIFICATIONS = "specifications", DOCUMENTS = "documents", TOOLS = "tools", MULTIDOCUMENTS = "multi-documents";

    public void notifyEvent(Event ev) {
        content.set(Collections.singleton(ev), null);
    }

    public void notifyEvents(Event[] ev) {
        content.set(Arrays.asList(ev), null);
    }

    public static final class Event {

        public Event(final Workspace ws, final Id id, final int info) {
            workspace = ws;
            this.id = id;
            this.info = info;
            this.source=null;
        }
        
        /**
         * 
         * @param ws The workspace concerned by the event
         * @param id The id of the object concerned by the info. May be null
         * @param info The code identifying the event
         * @param source The object that generated the event. May be null (unused)
         */
        public Event(final Workspace ws, final Id id, final int info, final Object source) {
            workspace = ws;
            this.id = id;
            this.info = info;
            this.source=source;
        }

        public static final int UNDEFINED = 0, NEW = 1, OPEN = 2, SAVE = 3, SAVEAS = 4, CLOSE = 5, SORT = 6,
                ADDINGITEM = 10, ITEMADDED = 11, OPENITEM = 12, REMOVINGITEM = 13, ITEMREMOVED = 14, CLOSEITEM = 15, ITEMCHANGED = 16, ITEMRENAMED = 17,
                ITEMCOMMENTS = 18,
                MANAGERS_CHANGED = 99;
        
        public final Workspace workspace;
        public final Id id;
        public final int info;
        public final Object source;
        
        public boolean cancelled;
    }
    
    private static WorkspaceFactory instance_;
    private final Lookup.Result<WorkspaceItemManager> workspaceManagersLookup;
    private final Lookup.Result<WorkspaceRepository> repositoryLookup;
    private final InstanceContent content = new InstanceContent();
    private final Lookup wsLookup;

    public static synchronized WorkspaceFactory getInstance() {
        if (instance_ == null) {
            instance_ = new WorkspaceFactory();
            instance_.register();
        }
        return instance_;
    }
    private final ArrayList<WorkspaceRepository> repositories_ = new ArrayList<>();
    private final List<WorkspaceItemManager> modules_ = new ArrayList<>();
//    private List<WorkspaceItem<?>> tmp_ = new ArrayList<WorkspaceItem<?>>();
    private int activeRepository;
    private int id_ = 1;
    private Workspace ws_;
    private TreeOfIds wsTree_;

    private WorkspaceFactory() {
        workspaceManagersLookup = Lookup.getDefault().lookupResult(WorkspaceItemManager.class);
        modules_.addAll(workspaceManagersLookup.allInstances());
        repositoryLookup = Lookup.getDefault().lookupResult(WorkspaceRepository.class);
        for (WorkspaceRepository fac : repositoryLookup.allInstances()) {
            fac.initialize();
            repositories_.add(fac);
        }
        // connect this to the global lookup
        wsLookup = new AbstractLookup(content);
    }

    private void register() {
        workspaceManagersLookup.addLookupListener(this);
        repositoryLookup.addLookupListener(this);
    }

    public Lookup getLookup() {
        return wsLookup;
    }

    public WorkspaceItemManager<?> getManager(Id family) {
        for (WorkspaceItemManager mgr : modules_) {
            if (mgr.getId().compareTo(family) == 0) {
                return mgr;
            }
        }
        return null;
    }

    public <T extends WorkspaceItemManager> T getManager(Class<T> tclass) {
        for (WorkspaceItemManager mgr : modules_) {
            if (mgr.getClass().equals(tclass)) {
                return (T) mgr;
            }
        }
        return null;
    }

    public String getActionsPath(Id id) {
        if (id == null) {
            return null;
        }
        WorkspaceItemManager mgr = getManager(id);
        if (mgr != null) {
            String s = mgr.getActionsPath();
            if (s != null) {
                return s;
            } else {
                return id.toString();
            }
        }
        mgr = getManager(id.parent());
        if (mgr != null) {
            String s = mgr.getActionsPath();
            if (s != null) {
                return s + ".item";
            } else {
                return id + ".item";
            }
        }
        return id.toString();
    }

    private String getNextWKSName() {
        return "Workspace_" + id_++;
    }

    public Workspace getActiveWorkspace() {
        if (ws_ == null) {
            newWorkspace();
        }
        return ws_;
        //return (ActiveWorkspace)Facade.getInstance("jdemetra").retrieveProxy(DemetraDictionary.ACTIVE_WS);
    }

//    public List<WorkspaceItem<?>> getTemporaryDocuments() {
//        return tmp_;
//    }
//
//    public <T> List<WorkspaceItem<T>> getTemporaryDocuments(Class<T> tclass) {
//        ArrayList<WorkspaceItem<T>> list = new ArrayList<WorkspaceItem<T>>();
//        for (WorkspaceItem<?> item : tmp_) {
//            if (tclass.isInstance(item.getElement())) {
//                list.add((WorkspaceItem<T>) item);
//            }
//        }
//        return list;
//    }
//
//    public void addTemporaryDocument(WorkspaceItem<?> item) {
//        tmp_.add(item);
//    }
//
//    public void removeTemporaryDocument(WorkspaceItem<?> item) {
//        tmp_.remove(item);
//    }
//
//    public void removeAllTemporaryDocuments() {
//        tmp_.clear();
//    }
//
//    public WorkspaceItem<?> searchTemporaryDocument(Id family, String name) {
//        for (WorkspaceItem<?> item : tmp_) {
//            if (name.equals(item.getDisplayName()) && family.equals(item.getFamily())) {
//                return item;
//            }
//        }
//        return null;
//    }
//
//    public List<WorkspaceItem<?>> searchTemporaryDocuments(Id family) {
//        ArrayList<WorkspaceItem<?>> sel = new ArrayList<WorkspaceItem<?>>();
//        for (WorkspaceItem item : tmp_) {
//            if (family.equals(item.getFamily())) {
//                sel.add(item);
//            }
//        }
//        return sel;
//    }
    public void setActiveWorkspace(Workspace wks, int event) {
        if (ws_ != null) {
            closeWorkspace(false);
        }
        ws_ = wks;
        ModellingContext.setActiveContext(ws_.getContext());
        if (event > 0) {
            content.set(Collections.singleton(new Event(ws_, null, event)), null);
        }
    }

    public void newWorkspace() {
        newWorkspace(getActiveRepository());
    }

    public void newWorkspace(WorkspaceRepository repo) {
        Workspace nws = new Workspace(repo.getDefaultDataSource(), getNextWKSName());
        nws.resetDirty();
        setActiveWorkspace(nws, Event.NEW);
    }

    public boolean closeWorkspace(boolean interactive) {
        if (ws_ == null) {
            return true;
        }
        if (!ws_.closeOpenDocuments()) {
            return false;
        }
        if (interactive && ws_.isDirty()) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(CLOSE_MSG, NotifyDescriptor.YES_NO_CANCEL_OPTION);
            Object notify = DialogDisplayer.getDefault().notify(nd);
            if (notify == NotifyDescriptor.YES_OPTION) {
                ws_.save(DemetraVersion.JD3);
            } else if (notify == NotifyDescriptor.CANCEL_OPTION) {
                return false;
            }
        }
        getActiveRepository().close(ws_);
        ws_.dispose();
        ws_ = null;
        return true;
    }

    public boolean openWorkspace() {
        return openWorkspace(getActiveRepository());
    }

    public boolean openWorkspace(WorkspaceRepository repo) {
        try {
            Workspace ws = repo.open();
            if (ws == null) {
                return false;
            }
            if (!closeWorkspace(true)) {
                return false;
            }
            setActiveWorkspace(ws, Event.OPEN);
            return true;
        } catch (Exception ex) {
            newWorkspace();
            return true;
        }
    }

    public void saveAsWorkspace() {
//            SaveFileDialog svd = new SaveFileDialog();
//            svd.Filter = "XML files|*.xml|All files|*.*";
//            svd.InitialDirectory = Settings.Default.DefaultDirectories.m_Workspaces;
//            if (svd.ShowDialog() == DialogResult.OK)
//            {
//                string name= System.IO.Path.GetFileNameWithoutExtension(svd.FileName);
//                string path = System.IO.Path.GetFullPath(svd.FileName).Replace("\\" + System.IO.Path.GetFileName(svd.FileName), "");
//                string file = svd.FileName;
//                ActiveWorkspace.SaveAs(name, path, file);
//
//                LOGGER.Info(svd.FileName + " Saved");
//            }
//
//            g_ws.ResetDirty();
    }

    public void getManagerIds(List<Id> ids) {
        for (WorkspaceItemManager mgr : this.modules_) {
            ids.add(mgr.getId());
        }
    }

    public List<WorkspaceItemManager> getManagers() {
        return Collections.unmodifiableList(modules_);
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(workspaceManagersLookup)) {
            modules_.clear();
            modules_.addAll(workspaceManagersLookup.allInstances());
            wsTree_ = null;
            Event ev = new Event(null, null, Event.MANAGERS_CHANGED);
            notifyEvent(ev);
        } else if (le.getSource().equals(repositoryLookup)) {
            repositories_.clear();
            for (WorkspaceRepository fac : repositoryLookup.allInstances()) {
                fac.initialize();
                repositories_.add(fac);
            }
            activeRepository = 0;
        }
    }

    public TreeOfIds getTree() {
        if (this.wsTree_ == null) {
            ArrayList<Id> items = new ArrayList<>();
            for (WorkspaceItemManager mgr : modules_) {
                items.add(mgr.getId());
            }
            wsTree_ = new TreeOfIds(items);
        }
        return wsTree_;
    }

    public List<WorkspaceRepository> getRepositories() {
        return Collections.unmodifiableList(repositories_);
    }

    public WorkspaceRepository getActiveRepository() {
        if (activeRepository < repositories_.size()) {
            return repositories_.get(activeRepository);
        } else {
            return null;
        }
    }

    public <D extends WorkspaceRepository> D getRepository(Class<D> dclass) {
        for (WorkspaceRepository repo : repositories_) {
            if (dclass.isInstance(repo)) {
                return (D) repo;
            }
        }
        return null;
    }

    public WorkspaceRepository getRepository(String name) {
        for (WorkspaceRepository repo : repositories_) {
            if (repo.getName().equals(name)) {
                return repo;
            }
        }
        return null;
    }

    public void setActiveRepository(int pos) {
        activeRepository = pos;
    }
}
