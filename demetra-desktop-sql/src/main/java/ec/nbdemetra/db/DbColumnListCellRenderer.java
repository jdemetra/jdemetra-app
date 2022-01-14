/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.db;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.function.Function;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Philippe Charles
 */
public final class DbColumnListCellRenderer<COLUMN> implements ListCellRenderer<COLUMN> {

    private final Function<COLUMN, String> name;
    private final Function<COLUMN, String> typeName;
    private final Function<COLUMN, Icon> typeIcon;

    private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();
    private final JPanel main = new JPanel(new BorderLayout());
    private final JLabel east = new JLabel();

    public DbColumnListCellRenderer(Function<COLUMN, String> name, Function<COLUMN, String> typeName, Function<COLUMN, Icon> typeIcon) {
        this.name = name;
        this.typeName = typeName;
        this.typeIcon = typeIcon;
        main.setOpaque(true);
        main.add(delegate, BorderLayout.CENTER);
        main.add(east, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel center = (JLabel) delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        main.setBackground(center.getBackground());
        main.setBorder(center.getBorder());
        center.setBorder(null);
        east.setBorder(null);
        east.setFont(center.getFont());
        east.setForeground(center.getForeground().brighter());
        center.setIcon(typeIcon.apply((COLUMN) value));
        center.setText("<html><b>" + name.apply((COLUMN) value) + "</b>");
        east.setText(typeName.apply((COLUMN) value));
        return main;
    }
}
