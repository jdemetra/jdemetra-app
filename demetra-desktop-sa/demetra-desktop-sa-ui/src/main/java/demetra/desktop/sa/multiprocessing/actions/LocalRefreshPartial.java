/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.ui.Menus;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;

@ActionID(category = "SaProcessing",
id = "demetra.desktop.sa.multiprocessing.actions.LocalRefreshPartial")
@ActionRegistration(displayName = "#CTL_LocalRefreshPartial", lazy=false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.LOCALPATH + LocalRefresh.PATH, position = 1220),
    @ActionReference(path = "Shortcuts", name = "p")
})
@NbBundle.Messages("CTL_LocalRefreshPartial=Partial concurrent adjustment")
public final class LocalRefreshPartial extends AbstractAction implements Presenter.Popup {

    public static final String PATH = "/Refresh/Partial";

    public LocalRefreshPartial() {
        super(Bundle.CTL_LocalRefreshPartial());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu menu = new JMenu(this);
        Menus.fillMenu(menu, MultiProcessingManager.LOCALPATH + PATH);
        return menu;
    }
}
