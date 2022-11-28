package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import demetra.data.Parameter;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.BiConsumer;
import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Demortier Jeremy
 */
public class ParameterEditor extends AbstractPropertyEditor {

    public ParameterEditor() {
        this.editor = createEditor();
        Resource.INSTANCE.bindValue((JFormattedTextField) editor, this::firePropertyChange);
    }

    @Override
    public Object getValue() {
        return Resource.INSTANCE.getValue((JFormattedTextField) editor);
    }

    @Override
    public void setValue(Object o) {
        Resource.INSTANCE.setValue((JFormattedTextField) editor, (Parameter) o);
    }

    private static JFormattedTextField createEditor() {
        JFormattedTextField editor = new JFormattedTextField(){
            
        };
        editor.setHorizontalAlignment(SwingConstants.RIGHT);
        editor.setBorder(null);
        Locale myLocale = Locale.getDefault(); // better still
        NumberFormat fmt = NumberFormat.getInstance(myLocale);
        fmt.setMaximumFractionDigits(6);
        NumberFormatter nfmt = new NumberFormatter(fmt){
                @Override
                public Object stringToValue(String text) throws ParseException {
                    if (text.isBlank())
                        return Double.NaN;
                    else
                        return super.stringToValue(text);
                }
        };
        nfmt.setAllowsInvalid(true);
        nfmt.setCommitsOnValidEdit(false);
        DefaultFormatterFactory dfmt = new javax.swing.text.DefaultFormatterFactory(nfmt);
        editor.setFormatterFactory(dfmt);
        editor.setBorder(LookAndFeelTweaks.EMPTY_BORDER);

        return editor;
    }

    private enum Resource implements CustomPropertyEditorSupport.Resource<JFormattedTextField, Parameter> {

        INSTANCE;

        @Override
        public void bindValue(JFormattedTextField editor, BiConsumer<Parameter, Parameter> broadcaster) {
            editor.addPropertyChangeListener("parameter", evt -> {
                Parameter od = Parameter.undefined(), nd = Parameter.undefined();
                if (evt.getOldValue() instanceof Number d) {
                    od = Parameter.fixed(d.doubleValue());
                }
                if (evt.getNewValue() instanceof Number d) {
                    nd = Parameter.fixed(d.doubleValue());
                }
                broadcaster.accept(od, nd);
            });
        }

        @Override
        public Parameter getValue(JFormattedTextField editor) {
            Object value = editor.getValue();
            if (value == null || !(value instanceof Number number)) {
                return Parameter.undefined();
            } else {
                double d = number.doubleValue();
                if (Double.isNaN(d))
                    return Parameter.undefined();
                return Parameter.fixed(number.doubleValue());
            }
        }

        @Override
        public void setValue(JFormattedTextField editor, Parameter value) {
            editor.setValue(value == null || !value.isDefined() ? null : value.getValue());
        }

        /**
         * Calendar.getInstance() creates a new instance of GregorianCalendar
         * and its constructor triggers a lot of internal synchronized code. =>
         * We use ThreadLocal to avoid this overhead
         */
    }

}
