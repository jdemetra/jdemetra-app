/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.ui.Menus;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(category = "SaProcessing",
        id = "ec.nbdemetra.sa.actions.LocalRefresh")
@ActionRegistration(displayName = "#CTL_LocalRefresh", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.LOCALPATH, position = 1200),
    @ActionReference(path = "Shortcuts", name = "r")
})
@Messages("CTL_LocalRefresh=Refresh")
public final class LocalRefresh extends AbstractViewAction<SaBatchUI> implements Presenter.Popup {

    public static final String PATH = "/Refresh";

    public LocalRefresh() {
        super(SaBatchUI.class);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        refreshAction();
        JMenu menu = new JMenu(Bundle.CTL_Refresh());
        menu.setEnabled(enabled);
        Menus.fillMenu(menu, MultiProcessingManager.LOCALPATH + PATH);
        return menu;
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getSelectionCount() > 0;
//            enabled=!ui.getDocument().getElement().isNew() && ui.getSelectionCount() > 0;
    }

    @Override
    protected void process(SaBatchUI cur) {
    }
}
