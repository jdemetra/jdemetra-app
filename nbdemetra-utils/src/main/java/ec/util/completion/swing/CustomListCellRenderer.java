/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.completion.swing;

import ec.util.completion.AutoCompletionSources;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * A specialized renderer used by JAutoCompletion that highlights terms.
 *
 * @author Philippe Charles
 */
public class CustomListCellRenderer<T> extends DefaultListCellRenderer {

    final boolean highlightTerm;

    public CustomListCellRenderer() {
        this(true);
    }

    public CustomListCellRenderer(boolean highlightTerm) {
        this.highlightTerm = highlightTerm;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel result = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String term = list.getModel() instanceof CustomListModel ? ((CustomListModel) list.getModel()).getTerm() : null;
        setText(toString(term, list, (T) value, index, isSelected, cellHasFocus));
        setIcon(toIcon(term, list, (T) value, index, isSelected, cellHasFocus));
        setToolTipText(toToolTipText(term, list, (T) value, index, isSelected, cellHasFocus));
        return result;
    }

    /**
     * Renders a value as a String.
     *
     * @param term
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return
     */
    protected String toString(String term, JList list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        String valueAsString = getValueAsString(value);
        if (!highlightTerm || term == null || term.isEmpty()) {
            return valueAsString;
        }
        int beginIndex = getNormalizedString(valueAsString).indexOf(getNormalizedString(term));
        if (beginIndex == -1) {
            return valueAsString;
        }
        int endIndex = beginIndex + term.length();
        return "<html>" + valueAsString.substring(0, beginIndex) + "<b>" + valueAsString.substring(beginIndex, endIndex) + "</b>" + valueAsString.substring(endIndex);
    }

    /**
     * Renders a value as an Icon.
     *
     * @param term
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return
     */
    protected Icon toIcon(String term, JList list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        return null;
    }

    /**
     * Renders a value as a tooltip text.
     *
     * @param term
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return
     */
    protected String toToolTipText(String term, JList list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        return null;
    }

    /**
     * Format a value as a string.<br>Default behavior uses
     * {@link Object#toString()}.
     *
     * @param value the value to be formatted
     * @return
     */
    protected String getValueAsString(T value) {
        return value.toString();
    }

    /**
     * Returns a normalized string used by the highlighter.<br>Default behavior
     * uses {@link AutoCompletionSources#normalize(java.lang.String)}.
     *
     * @param input the string to be normalized
     * @return a normalized string
     */
    protected String getNormalizedString(String input) {
        return AutoCompletionSources.normalize(input);
    }
}
