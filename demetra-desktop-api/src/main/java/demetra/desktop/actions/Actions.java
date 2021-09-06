/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.actions;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.beans.PropertyChangeListener;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public final class Actions {

    @NonNull
    public static <C extends AbstractButton> C hideWhenDisabled(@NonNull C c) {
        c.setVisible(c.isEnabled());
        c.addPropertyChangeListener(HIDE);
        return c;
    }

    private static final PropertyChangeListener HIDE = evt -> {
        if ("enabled".equals(evt.getPropertyName()) && evt.getSource() instanceof JComponent) {
            ((JComponent) evt.getSource()).setVisible((Boolean) evt.getNewValue());
        }
    };

    public static final String COPY_NODE_ACTION_ID = "org.openide.actions.CopyAction";
}
