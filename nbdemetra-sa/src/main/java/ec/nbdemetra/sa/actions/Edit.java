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
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(category = "SaProcessing",
id = "ec.nbdemetra.sa.actions.Edit")
@ActionRegistration(displayName = "#CTL_Edit", lazy=false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 1300, separatorBefore=1299),
    @ActionReference(path = MultiProcessingManager.LOCALPATH, position = 1300, separatorBefore=1299),
    @ActionReference(path = "Shortcuts", name = "e")
})
@Messages("CTL_Edit=Edit")
public final class Edit extends AbstractViewAction<SaBatchUI> implements Presenter.Popup {
    
    public static final String PATH="/Edit";

    public Edit(){
        super(SaBatchUI.class);
    }
    
    @Override
    public JMenuItem getPopupPresenter() {
        refreshAction();
        JMenu menu=new JMenu(Bundle.CTL_Edit());
        menu.setEnabled(enabled);
        Menus.fillMenu(menu, MultiProcessingManager.CONTEXTPATH+PATH);
        return menu;
    }

    @Override
    protected void refreshAction() {
    }

    @Override
    protected void process(SaBatchUI cur) {
    }

}
