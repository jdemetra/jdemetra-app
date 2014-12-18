/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import ec.tss.tsproviders.utils.IConstraint;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.openide.DialogDescriptor;
import org.openide.NotificationLineSupport;
import org.openide.util.WeakListeners;

/**
 *
 * @author Philippe Charles
 */
public abstract class CustomDialogDescriptor<T> extends DialogDescriptor implements PropertyChangeListener {

    protected final NotificationLineSupport nls;
    protected final T constraintData;

    public CustomDialogDescriptor(JComponent p, String title, T constraintData) {
        super(p, title);
        this.nls = createNotificationLineSupport();
        this.constraintData = constraintData;
        p.addPropertyChangeListener(WeakListeners.propertyChange(this, p));
    }

    protected final void validate(IConstraint<T>... list) {
        for (IConstraint<T> o : list) {
            String msg = o.check(constraintData);
            if (msg != null) {
                nls.setWarningMessage(msg);
                setValid(false);
                return;
            }
        }
        nls.clearMessages();
        setValid(true);
    }
}
