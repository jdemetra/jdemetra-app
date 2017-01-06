/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package ec.nbdemetra.ui.properties;

import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.toedter.calendar.JDateChooser;
import ec.tstoolkit.timeseries.Day;
import java.util.Date;
import javax.swing.JComponent;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.nodes.PropertyEditorRegistration;

/**
 *
 * @author Philippe Charles
 */
@PropertyEditorRegistration(targetType = Day.class)
public class DayPropertyEditor extends AbstractExPropertyEditor {

    @Override
    protected InplaceEditor createInplaceEditor() {
        return new DayInplaceEditor();
    }

    private static final class DayInplaceEditor extends AbstractInplaceEditor {

        final JDateChooser component = new JDateChooser("yyyy-MM-dd", "####-##-##", '_') {
            {
                dateEditor.getUiComponent().setBorder(LookAndFeelTweaks.EMPTY_BORDER);
            }
        };

        {
            component.addPropertyChangeListener("date", evt -> fireActionPerformed(COMMAND_SUCCESS));
        }

        @Override
        public JComponent getComponent() {
            return component;
        }

        @Override
        public Object getValue() {
            Date date = component.getDate();
            return date != null ? new Day(date) : null;
        }

        @Override
        public void setValue(Object o) {
            Day day = (Day) o;
            component.setDate(o != null ? day.getTime() : null);
        }
    }
}
