/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.actions;

import ec.nbdemetra.ui.properties.l2fprod.PropertiesDialog;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.nbdemetra.x13.X13SpecificationManager;
import ec.nbdemetra.x13.descriptors.X13SpecUI;
import ec.satoolkit.x13.X13Specification;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

@ActionID(category = "Tools",
        id = "ec.nbdemetra.x13.actions.EditX13Spec")
@ActionRegistration(displayName = "#CTL_EditX13Spec")
@ActionReferences({
    @ActionReference(path = X13SpecificationManager.ITEMPATH, position = 1000, separatorAfter = 1090)
})
@NbBundle.Messages("CTL_EditX13Spec=Open")
public class EditX13Spec implements ActionListener {

    private final WsNode context;

    public EditX13Spec(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        EditX13();
    }

    private void EditX13() {
        final WorkspaceItem<X13Specification> xdoc = context.getWorkspace().searchDocument(context.lookup(), X13Specification.class);
        if (xdoc == null || xdoc.getElement() == null) {
            return;

        }
        final X13SpecUI ui = new X13SpecUI(xdoc.getElement().clone(), null, xdoc.isReadOnly());
        PropertiesDialog propDialog =
                new PropertiesDialog(WindowManager.getDefault().getMainWindow(), true, ui,
                new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                xdoc.setElement(ui.getCore());
            }
        });
        propDialog.setTitle(xdoc.getDisplayName());
        propDialog.setVisible(true);
    }
}
