/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.actions;

import ec.nbdemetra.ui.properties.l2fprod.PropertiesDialog;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.nbdemetra.x13.RegArimaSpecificationManager;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.nbdemetra.x13.descriptors.RegArimaSpecUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

@ActionID(category = "Tools",
        id = "ec.nbdemetra.x13.actions.EditRegArimaSpec")
@ActionRegistration(displayName = "#CTL_EditRegArimaSpec")
@ActionReferences({
    @ActionReference(path = RegArimaSpecificationManager.ITEMPATH, position = 1000, separatorAfter = 1090)
})
@NbBundle.Messages("CTL_EditRegArimaSpec=Open")
public class EditRegArimaSpec implements ActionListener {

    private final WsNode context;

    public EditRegArimaSpec(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final WorkspaceItem<RegArimaSpecification> xdoc = context.getWorkspace().searchDocument(context.lookup(), RegArimaSpecification.class);
        if (xdoc == null || xdoc.getElement() == null) {
            return;
        }
        RegArimaSpecification spec = xdoc.getElement();
        if (spec == null) {
            return;

        }
        final RegArimaSpecUI ui = new RegArimaSpecUI(spec.clone(), null, xdoc.isReadOnly());
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
