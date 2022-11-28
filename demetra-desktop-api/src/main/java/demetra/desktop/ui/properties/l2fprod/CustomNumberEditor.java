/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.ui.properties.l2fprod;

import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author palatej
 */
public class CustomNumberEditor extends DefaultCellEditor {

    public CustomNumberEditor() {
        super(new JFormattedTextField());
        final JFormattedTextField editor = (JFormattedTextField) super.getComponent();
        editor.setHorizontalAlignment(SwingConstants.RIGHT);
        editor.setBorder(null);
        Locale myLocale = Locale.getDefault(); // better still
        NumberFormat fmt = NumberFormat.getInstance(myLocale);
        editor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(fmt)));
        super.delegate = new DefaultCellEditor.EditorDelegate() {

            @Override
            public void setValue(Object value) {
                editor.setValue(value != null ? ((Number) value).doubleValue() : value);
            }

            @Override
            public Object getCellEditorValue() {
                Object val = editor.getValue();
                return val != null ? ((Number) val).doubleValue() : val;
            }

            @Override
            public boolean stopCellEditing() {
                try {
                    editor.commitEdit();
                }
                catch (java.text.ParseException e) {
                }
                return super.stopCellEditing();
            }
        };
    }
}
