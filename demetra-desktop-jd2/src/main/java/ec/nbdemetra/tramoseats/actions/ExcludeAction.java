/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "ec.nbdemetra.tramoseats.actions.ExcludeAction")
@ActionRegistration(displayName = "#CTL_ExcludeAction")
@ActionReferences({})
@Messages("CTL_ExcludeAction=Exclude")
public final class ExcludeAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
    }
}
