/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Jean Palate
 * @deprecated use {@link AbstractAction} instead
 */
@Deprecated
public abstract class ApplyAction implements IApplyAction {

    private static final String APPLY = "Apply";
    private final String name_;

    public ApplyAction(String name) {
        name_ = name;
    }

    public ApplyAction() {
        name_ = APPLY;
    }

    @Override
    public String getActionName() {
        return name_;
    }

    public static AbstractAction toSwingAction(final IApplyAction action) {
        return new AbstractAction(action.getActionName()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.apply();
            }
        };
    }
}
