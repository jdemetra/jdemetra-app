/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tsaction;

import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.ns.NamedServiceChoicePanel;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;
import org.openide.util.actions.Presenter;

@ActionID(category = "Edit",
        id = "ec.nbdemetra.ui.ChooseTsActionAction")
@ActionRegistration(
        displayName = "#CTL_ChooseTsActionAction",
        lazy = false)
@ActionReferences({
    @ActionReference(path = "Toolbars/Other", position = 300)
})
@Messages("CTL_ChooseTsActionAction=Choose TsAction")
public final class ChooseTsActionAction extends AbstractAction implements Presenter.Toolbar, VetoableChangeListener, PropertyChangeListener {

    final NamedServiceChoicePanel choicePanel;

    public ChooseTsActionAction() {
        this.choicePanel = new NamedServiceChoicePanel();
        DemetraUI demetraUI = DemetraUI.getDefault();
        choicePanel.setContent(demetraUI.getTsActions());
        choicePanel.setSelectedServiceName(demetraUI.getTsActionName());
        choicePanel.getExplorerManager().addVetoableChangeListener(this);
        demetraUI.addPropertyChangeListener(WeakListeners.propertyChange(this, demetraUI));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
    }

    @Override
    public Component getToolbarPresenter() {
        JPanel result = new JPanel(new FlowLayout());
        result.add(choicePanel);
        return result;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DemetraUI.TS_ACTION_NAME_PROPERTY)) {
            Node o = choicePanel.getExplorerManager().getRootContext().getChildren().findChild((String) evt.getNewValue());
            if (o != null) {
                try {
                    choicePanel.getExplorerManager().setSelectedNodes(new Node[]{o});
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            Node[] nodes = (Node[]) evt.getNewValue();
            if (nodes.length > 0) {
                DemetraUI.getDefault().setTsActionName(nodes[0].getName());
            }
        }
    }
}
