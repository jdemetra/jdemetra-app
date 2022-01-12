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
package demetra.desktop.core.properties;

import demetra.desktop.properties.AbstractInplaceEditor;
import demetra.desktop.properties.AbstractExPropertyEditor;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.toedter.calendar.JDateChooser;
import demetra.timeseries.calendars.CalendarUtility;
import java.time.LocalDate;
import java.util.Date;
import javax.swing.JComponent;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.nodes.PropertyEditorRegistration;

/**
 *
 * @author Philippe Charles
 */
@PropertyEditorRegistration(targetType = LocalDate.class)
public class LocalDatePropertyEditor extends AbstractExPropertyEditor {

    @Override
    protected InplaceEditor createInplaceEditor() {
        return new DateInplaceEditor();
    }

    private static final class DateInplaceEditor extends AbstractInplaceEditor {

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
            return date != null ? CalendarUtility.toLocalDate(date) : null;
        }

        @Override
        public void setValue(Object o) {
            LocalDate day = (LocalDate) o;
            component.setDate(o != null ? CalendarUtility.toDate(day) : null);
        }
    }
}
