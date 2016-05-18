/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.awt;

import java.awt.Component;
import javax.annotation.Nonnull;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.openide.awt.HtmlRenderer;

/**
 *
 * @author Philippe Charles
 */
public class SimpleHtmlListCellRenderer<T> implements ListCellRenderer {

    protected final HtmlProvider<T> htmlProvider;
    protected final HtmlRenderer.Renderer htmlRenderer;

    public SimpleHtmlListCellRenderer(@Nonnull HtmlProvider<T> htmlProvider) {
        this.htmlProvider = htmlProvider;
        this.htmlRenderer = HtmlRenderer.createRenderer();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        htmlRenderer.setHtml(true);
        return htmlRenderer.getListCellRendererComponent(list, htmlProvider.getHtmlDisplayName((T) value), index, isSelected, cellHasFocus);
    }

    @FunctionalInterface
    public interface HtmlProvider<T> {

        String getHtmlDisplayName(T value);
    }
}
