/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui;

import demetra.desktop.util.PopupListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.ContextAwareAction;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Jean Palate
 */
public class Menus {

    /**
     * Retrieves actions from the given paths.
     *
     * @param defActions Default actions for
     * @param paths Action paths
     * @return All found actions from the given paths
     */
    public static Action[] createActions(Action[] defActions, String... paths) {
        ArrayList<Action> subActions = new ArrayList<>();
        ArrayList<Action> actions = new ArrayList<>();
        for (String path : paths) {
            List<? extends Action> actionsForPath = Utilities.actionsForPath(path);
            for (Action a : actionsForPath) {
                if (a instanceof Presenter.Popup) {
                    List<Action> presenterActions = findSubActions((Presenter.Popup) a);
                    if (!presenterActions.isEmpty()) {
                        subActions.addAll(presenterActions);
                    }
                    else {
                        continue;
                    }
                }
                actions.add(a);
            }
        }

        if (defActions != null) {
            actions.addAll(Arrays.asList(defActions));
        }

        // remove all actions that are already in a submenu 
        actions.removeAll(subActions);
        return actions.toArray(new Action[0]);
    }

    public static void fillMenu(JMenu menu, String... paths) {
        fillMenu(menu.getPopupMenu(), paths);
    }

    public static void fillMenu(JPopupMenu menu, String... paths) {
        fillMenu(menu, createActions(null, paths));
    }

    public static void fillMenu(JMenu menu, Action[] actions) {
        fillMenu(menu.getPopupMenu(), actions);
    }

    public static void fillMenu(JPopupMenu menu, Action[] actions) {
        boolean sep = false;
        for (Action action : actions) {
            if (action == null) {
                if (sep) {
                    menu.addSeparator();
                    sep = false;
                }
            }
            else {
                sep = true;
                if (action instanceof DynamicMenuContent) {
                    DynamicMenuContent dmenu = (DynamicMenuContent) action;
                    JComponent[] items = dmenu.getMenuPresenters();
                    if (items != null) {
                        for (JComponent item : items) {
                            menu.add(item);
                        }
                    }
                }
                else if (action instanceof Presenter.Popup) {
                    Presenter.Popup popup = (Presenter.Popup) action;
                    menu.add(popup.getPopupPresenter());
                }
                else if (action instanceof Presenter.Menu) {
                    Presenter.Menu item = (Presenter.Menu) action;
                    menu.add(item.getMenuPresenter());
                }
                else if (action instanceof ContextAwareAction) {
                    menu.add(((ContextAwareAction) action).createContextAwareInstance(Utilities.actionsGlobalContext()));
                }
                else {
                    menu.add(action);
                }
            }
        }
    }

    private static List<Action> findSubActions(Presenter.Popup subMenu) {
        List<Action> actions = new ArrayList<>();

        JMenuItem item = subMenu.getPopupPresenter();
        if (item instanceof JMenu) {
            JMenu menu = (JMenu) item;
            for (int i = 0; i < menu.getItemCount(); i++) {
                JMenuItem cur = menu.getItem(i);
                if (cur != null) {
                    Action a = menu.getItem(i).getAction();
                    actions.add(a);

                    if (a instanceof Presenter.Popup) {
                        actions.addAll(findSubActions((Presenter.Popup) a));
                    }
                }
            }
        }

        return actions;
    }

    public static class DynamicPopup extends PopupListener {

        String[] paths;

        public DynamicPopup(String... paths) {
            this.paths = paths;
        }

        @Override
        protected JPopupMenu getPopup(MouseEvent e) {
            JPopupMenu popup = new JPopupMenu();
            fillMenu(popup, paths);
            return popup;
        }
    }
}
