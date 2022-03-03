/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui;

import java.awt.Frame;
import java.util.Collections;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * A hack simulating MDI documents (to be changed ?)
 *
 * @author Jean Palate
 */
public class ActiveViewManager implements Lookup.Provider {

    public static ActiveViewManager getInstance() {
        return INSTANCE;
    }
    private static final ActiveViewManager INSTANCE = new ActiveViewManager();
    private static final String ACTIVE = "_Active_View";
    private static final int POS = 2;
    private boolean updating = false;
    private final InstanceContent content = new InstanceContent();
    private final Lookup lookup;

    private ActiveViewManager() {
        lookup = new AbstractLookup(content);
     }
    
    public boolean isActive(ActiveView view){
        return lookup.lookup(view.getClass()) == view;
    }

    public void set(ActiveView view) {
        if (view == null) {
            content.set(Collections.emptySet(), null);
        } else {
            content.set(Collections.singleton(view), null);
        }
        updateMenu(view);
    }

    private void updateMenu(ActiveView view) {
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
                ActiveView view = currentView();
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
                ActiveView view = activated instanceof ActiveView ? (ActiveView) activated : activated.getLookup().lookup(ActiveView.class);
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

    private ActiveView currentView() {
        return lookup.lookup(ActiveView.class);
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }
}
