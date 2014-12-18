/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.awt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 * @author Philippe Charles
 */
public class OuputPropertyChangeListener implements PropertyChangeListener {

    static final InputOutput IO = IOProvider.getDefault().getIO("PropertyChangeListener", new Action[]{});

    public OuputPropertyChangeListener() {
        //IO.select();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        OutputWriter out = IO.getOut();
        out.println(evt.getSource().getClass().getName() + " -> " + evt.getPropertyName());
    }
}
