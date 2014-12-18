/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

import java.awt.Frame;
import java.beans.PropertyVetoException;
import java.util.Collections;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * A hack simulating MDI documents that should be changed
 *
 * @author Jean Palate
 */
public class ActiveViewManager implements Lookup.Provider, ExplorerManager.Provider {

    public static ActiveViewManager getInstance() {
        return INSTANCE;
    }
    private static final ActiveViewManager INSTANCE = new ActiveViewManager();
    private static final String ACTIVE = "_Active_View";
    private static final int POS = 2;
    private boolean updating = false;
    private final InstanceContent content = new InstanceContent();
    private final Lookup lookup;
    private final ExplorerManager mgr = new ExplorerManager();

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    private ActiveViewManager() {
        lookup = new AbstractLookup(content);
        mgr.setRootContext(Node.EMPTY);
    }

    public void set(IActiveView view) {
        if (view == null) {
            content.set(Collections.EMPTY_SET, null);
            //mgr.setRootContext(Node.EMPTY);
        } else {
            content.set(Collections.singleton(view), null);
            Node node = view.getNode();
            if (node != null) {
                mgr.setRootContext(node);
                try {
                    mgr.setSelectedNodes(new Node[]{node});
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                mgr.setRootContext(Node.EMPTY);
            }
        }
        updateMenu(view);
    }

//    void initialize() {
//        WindowManager.getDefault().getRegistry().addPropertyChangeListener(new PropertyChangeListener() {
//
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                JMenu current = activeMenu();
//                if (current == null) {
//                    return;
//                }
//                if (evt.getPropertyName().equals(TopComponent.Registry.PROP_ACTIVATED)) {
//                    TopComponent activated = WindowManager.getDefault().getRegistry().getActivated();
//                    if (activated != null) {
//                        content.set(Collections.singleton(activated), null);
//                        IActiveView view = currentView();
//                        if (view != null) {
//                            Node node = view.getNode();
//                            if (node != null) {
//                                try {
//                                    mgr.setRootContext(node);
//                                    mgr.setSelectedNodes(new Node[]{node});
//                                }
//                                catch (PropertyVetoException ex) {
//                                    Exceptions.printStackTrace(ex);
//                                }
//                            }
////                            else {
////                                mgr.setRootContext(Node.EMPTY);
////                            }
//                        }
//                    }
//                    else {
//                        content.set(null, null);
////                        mgr.setRootContext(Node.EMPTY);
//                    }
//
//                    fill(current);
//                }
//            }
//        });
//    }
    private void updateMenu(IActiveView view) {
        JMenu menu = activeMenu();
        if (menu == null) {
            return;
        }
        if (view == null || !view.hasContextMenu()) {
            menu.setVisible(false);
        } else {
            menu.removeAll();
            menu.setVisible(true);
            menu.setText(view.getName());
        }
    }

    private JMenu activeMenu() {
        Frame frame = WindowManager.getDefault().getMainWindow();
        if (frame == null || !(frame instanceof JFrame)) {
            return null;
        }
        JFrame mainWindow = (JFrame) frame;
        JMenuBar menuBar = mainWindow.getJMenuBar();
        if (menuBar == null) {
            return null;
        }
        for (int i = 0; i < menuBar.getMenuCount(); ++i) {
            JMenu cur = menuBar.getMenu(i);
            if (cur != null && ACTIVE.equals(cur.getName())) {
                return cur;
            }
        }
        final JMenu nmenu = new JMenu();
        nmenu.setName(ACTIVE);
        menuBar.add(nmenu, POS);
        menuBar.validate();
        nmenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if (updating) {
                    return;
                }
                nmenu.removeAll();
                IActiveView view = currentView();
                if (view != null && view.hasContextMenu()) {
                    view.fill(nmenu);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        return nmenu;
    }

    private void fill(JMenu current) {
        try {
            updating = true;
            TopComponent activated = WindowManager.getDefault().getRegistry().getActivated();
            if (activated != null) {
                IActiveView view = activated instanceof IActiveView ? (IActiveView) activated : activated.getLookup().lookup(IActiveView.class);
                if (view == null) {
                    current.setVisible(false);
                } else {
                    current.removeAll();
                    current.setVisible(true);
                    current.setText(activated.getName());
                }
            } else {
                current.setVisible(false);
            }
        } finally {
            updating = false;
        }
    }

    private IActiveView currentView() {
        return lookup.lookup(IActiveView.class);
    }

//    public void register(Id id, TopComponent view) {
//        views.add(new IdView(id, view));
//    }
//
//    public void unregister(TopComponent view) {
//        int pos = 0, toremove = -1;
//        for (IdView idview : views) {
//            if (idview.view == view) {
//                toremove = pos;
//                break;
//            }
//            ++pos;
//        }
//        if (toremove >= 0) {
//            views.remove(toremove);
//        }
//    }
//
//    public <T extends TopComponent> T search(Id id, Class<T> tclass) {
//        for (IdView idview : views) {
//            if (idview.id.equals(id) && tclass.isInstance(idview.view)) {
//                return (T) idview.view;
//            }
//        }
//        return null;
//    }
//
//    public TopComponent search(Id id) {
//        for (IdView idview : views) {
//            if (idview.id.equals(id)) {
//                return idview.view;
//            }
//        }
//        return null;
//    }
//
    @Override
    public Lookup getLookup() {
        return lookup;
    }
}
