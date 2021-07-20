/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.actions;

import ec.nbdemetra.tramoseats.TramoSeatsSpecificationManager;
import ec.nbdemetra.ui.properties.l2fprod.PropertiesDialog;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.nbdemetra.tramoseats.descriptors.TramoSeatsSpecUI;
import ec.nbdemetra.ws.nodes.ItemWsNode;
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
id = "ec.nbdemetra.tramoseats.actions.EditTramoSeatsSpec")
@ActionRegistration(displayName = "#CTL_EditTramoSeatsSpec")
@ActionReferences({
    @ActionReference(path = TramoSeatsSpecificationManager.ITEMPATH, position = 1000, separatorAfter=1090)
})
@NbBundle.Messages("CTL_EditTramoSeatsSpec=Open")
public class EditTramoSeatsSpec implements ActionListener {

    private final ItemWsNode context;

    public EditTramoSeatsSpec(ItemWsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        editTramoSeats();
    }

    private void editTramoSeats() {
        final WorkspaceItem<TramoSeatsSpecification> xdoc = context.getWorkspace().searchDocument(context.lookup(), TramoSeatsSpecification.class);
        if (xdoc == null|| xdoc.getElement() == null) {
            return;
        }
        final TramoSeatsSpecUI ui = new TramoSeatsSpecUI(xdoc.getElement().clone(), xdoc.isReadOnly());
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
