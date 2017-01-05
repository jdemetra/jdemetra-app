package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.toedter.calendar.IDateEditor;
import com.toedter.calendar.JTextFieldDateEditor;
import ec.tstoolkit.timeseries.Day;
import internal.CustomPropertyEditorSupport;
import java.awt.Component;
import java.text.ParseException;
import java.util.Date;
import java.util.function.BiConsumer;

/**
 *
 * @author Demortier Jeremy
 */
public class JDayPropertyEditor extends AbstractPropertyEditor {

    public JDayPropertyEditor() {
        this.editor = createEditor();
        JDayResource.INSTANCE.bindValue(editor, this::firePropertyChange);
    }

    @Override
    public Object getValue() {
        return JDayResource.INSTANCE.getValue(editor);
    }

    @Override
    public void setValue(Object o) {
        JDayResource.INSTANCE.setValue(editor, o);
    }

    private static Component createEditor() {
        JTextFieldDateEditor result = new JTextFieldDateEditor("yyyy-MM-dd", "####-##-##", '_') {
            @Override
            public void commitEdit() throws ParseException {
                preventInvalidCommit();
            }

            @Override
            public Date getDate() {
                return parseDateOrPrevious();
            }

            private void preventInvalidCommit() throws ParseException {
                dateFormatter.parse(getText());
            }

            private Date parseDateOrPrevious() {
                Date previous = date;
                if (super.getDate() == null) {
                    date = previous;
                }
                return date;
            }
        };
        result.setBorder(LookAndFeelTweaks.EMPTY_BORDER);
        return result;
    }

    private enum JDayResource implements CustomPropertyEditorSupport.Resource {
        INSTANCE;

        @Override
        public void bindValue(Component editor, BiConsumer<Object, Object> y) {
            editor.addPropertyChangeListener("date", evt -> {
                Day oday = null, nday = null;
                if (evt.getOldValue() instanceof Date) {
                    oday = new Day((Date) evt.getOldValue());
                }
                if (evt.getNewValue() instanceof Date) {
                    nday = new Day((Date) evt.getNewValue());
                }
                y.accept(oday, nday);
            });
        }

        @Override
        public Object getValue(Component editor) {
            Date date = ((IDateEditor) editor).getDate();
            return date != null ? new Day(date) : Day.BEG;
        }

        @Override
        public void setValue(Component editor, Object value) {
            ((IDateEditor) editor).setDate(value != null ? ((Day) value).getTime() : Day.toDay().getTime());
        }
    }
}
