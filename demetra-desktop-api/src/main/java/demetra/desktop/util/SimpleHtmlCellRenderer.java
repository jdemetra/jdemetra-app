/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.util;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.awt.HtmlRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import nbbrd.design.DirectImpl;

/**
 *
 * @author Philippe Charles
 * @param <T>
 */
@DirectImpl
public final class SimpleHtmlCellRenderer<T> implements TableCellRenderer, TreeCellRenderer, ListCellRenderer<T> {

    private final Function<T, String> html;
    private final HtmlRenderer.Renderer delegate;

    public SimpleHtmlCellRenderer(@NonNull Function<T, String> html) {
        this.html = html;
        this.delegate = HtmlRenderer.createRenderer();
        delegate.setHtml(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return delegate.getTableCellRendererComponent(table, html.apply((T) value), isSelected, hasFocus, row, column);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        return delegate.getTreeCellRendererComponent(tree, html.apply((T) value), selected, expanded, leaf, row, hasFocus);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        return delegate.getListCellRendererComponent(list, html.apply(value), index, isSelected, cellHasFocus);
    }
}
