/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.util;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.awt.HtmlRenderer;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Philippe Charles
 */
public class SimpleHtmlListCellRenderer<T> implements ListCellRenderer {

    protected final HtmlProvider<T> htmlProvider;
    protected final HtmlRenderer.Renderer htmlRenderer;

    public SimpleHtmlListCellRenderer(@NonNull HtmlProvider<T> htmlProvider) {
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
