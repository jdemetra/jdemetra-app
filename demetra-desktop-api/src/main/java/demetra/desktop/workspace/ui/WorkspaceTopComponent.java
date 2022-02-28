/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace.ui;

import java.util.Collection;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.ui.Menus;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.openide.windows.WindowManager;

/**
 *
 * @author Jean Palate
 * @param <T>
 */
public abstract class WorkspaceTopComponent<T> extends TopComponent implements ExplorerManager.Provider, LookupListener {

    protected final WorkspaceItem<T> doc;
    private final Lookup.Result<WorkspaceFactory.Event> wsevent;

    protected abstract String getContextPath();

    protected WorkspaceTopComponent(WorkspaceItem<T> doc) {
        this.doc = doc;
        this.wsevent = WorkspaceFactory.getInstance().getLookup().lookupResult(WorkspaceFactory.Event.class);
    }

    public WorkspaceItem<T> getDocument() {
        return doc;
    }

    @Override
    public String getName() {
        return doc == null ? super.getName() : doc.getDisplayName();
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
        updateMenu(true);
    }

    @Override
    public void componentDeactivated() {
        updateMenu(false);
        super.componentDeactivated();
    }

    @Override
    public Action[] getActions() {
        return Menus.createActions(super.getActions(), getContextPath());
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        doc.setView(this);
        wsevent.addLookupListener(this);
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        wsevent.removeLookupListener(this);
        doc.setView(null);
        super.componentClosed();
    }

    public void refresh() {
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends WorkspaceFactory.Event> all = wsevent.allInstances();
        for (WorkspaceFactory.Event ev : all) {
            if (ev.id.equals(doc.getId())) {
                switch (ev.info) {
                    case WorkspaceFactory.Event.REMOVINGITEM:
                        SwingUtilities.invokeLater(this::close);
                        break;
                    case WorkspaceFactory.Event.ITEMCHANGED:
                        if (ev.source != this) {
                            SwingUtilities.invokeLater(this::refresh);

                        }
                        break;
                    case WorkspaceFactory.Event.ITEMRENAMED:
                        if (ev.source != this) {
                            SwingUtilities.invokeLater(()->
                            this.setDisplayName(doc.getDisplayName()));
                        }
                        break;
                }
            }
        }
    }

    protected boolean fill(JMenu menu) {
        if (doc != null) {
            Menus.fillMenu(menu, getContextPath());
        }
        return true;
    }

    protected boolean hasContextMenu() {
        return true;
    }

    private void updateMenu(boolean show) {
        JMenu menu = activeMenu();
        if (menu == null) {
            return;
        }
        if (!show || !hasContextMenu()) {
            menu.setVisible(false);
        } else {
            menu.removeAll();
            menu.setVisible(true);
            menu.setText(getName());
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
                nmenu.removeAll();
                if (hasContextMenu()) {
                    fill(nmenu);
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

    private static final String ACTIVE = "_Active_View";
    private static final int POS = 2;
}
