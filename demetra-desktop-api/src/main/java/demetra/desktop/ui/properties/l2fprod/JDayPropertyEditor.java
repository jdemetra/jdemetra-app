package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.toedter.calendar.JTextFieldDateEditor;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.function.BiConsumer;

/**
 *
 * @author Demortier Jeremy
 */
public class JDayPropertyEditor extends AbstractPropertyEditor {

    public JDayPropertyEditor() {
        this.editor = createEditor();
        DayResource.INSTANCE.bindValue((JTextFieldDateEditor) editor, this::firePropertyChange);
    }

    @Override
    public Object getValue() {
        return DayResource.INSTANCE.getValue((JTextFieldDateEditor) editor);
    }

    @Override
    public void setValue(Object o) {
        DayResource.INSTANCE.setValue((JTextFieldDateEditor) editor, (Day) o);
    }

    private static JTextFieldDateEditor createEditor() {
        JTextFieldDateEditor result = new PatchedTextFieldDateEditor();
        result.setBorder(LookAndFeelTweaks.EMPTY_BORDER);
        return result;
    }

    private enum DayResource implements CustomPropertyEditorSupport.Resource<JTextFieldDateEditor, LocalDate> {

        INSTANCE;

        @Override
        public void bindValue(JTextFieldDateEditor editor, BiConsumer<Day, Day> broadcaster) {
            editor.addPropertyChangeListener("date", evt -> {
                Day oday = null, nday = null;
                if (evt.getOldValue() instanceof Date) {
                    oday = new Day((Date) evt.getOldValue());
                }
                if (evt.getNewValue() instanceof Date) {
                    nday = new Day((Date) evt.getNewValue());
                }
                broadcaster.accept(oday, nday);
            });
        }

        @Override
        public Day getValue(JTextFieldDateEditor editor) {
            Date date = editor.getDate();
            return date != null ? new Day(date) : Day.BEG;
        }

        @Override
        public void setValue(JTextFieldDateEditor editor, Day value) {
            editor.setDate(value != null ? value.getTime() : Day.toDay().getTime());
        }
    }

    private static final class PatchedTextFieldDateEditor extends JTextFieldDateEditor {

        private PatchedTextFieldDateEditor() {
            super("yyyy-MM-dd", "####-##-##", '_');
        }

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
    }
}
