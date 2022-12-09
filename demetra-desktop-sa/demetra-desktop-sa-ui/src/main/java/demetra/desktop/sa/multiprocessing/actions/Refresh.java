/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.sa.multiprocessing.ui.SaBatchUI;
import demetra.desktop.ui.Menus;
import demetra.desktop.ui.ActiveViewAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(category = "SaProcessing",
        id = "demetra.desktop.sa.multiprocessing.actions.Refresh")
@ActionRegistration(displayName = "#CTL_Refresh", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 1200),
    @ActionReference(path = "Shortcuts", name = "R")
})
@Messages("CTL_Refresh=Refresh")
public final class Refresh extends ActiveViewAction<SaBatchUI> implements Presenter.Popup {

    public static final String PATH = "/Refresh";

    public Refresh() {
        super(SaBatchUI.class);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        refreshAction();
        JMenu menu = new JMenu(Bundle.CTL_Refresh());
        menu.setEnabled(enabled);
        Menus.fillMenu(menu, MultiProcessingManager.CONTEXTPATH + PATH);
        return menu;
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getElement().isRefreshable();
    }

    @Override
    protected void process(SaBatchUI cur) {
    }

}
