/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.db;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 *
 * @author Philippe Charles
 */
public abstract class DbColumnListCellRenderer<COLUMN> extends DefaultListCellRenderer {

    protected final JPanel main = new JPanel(new BorderLayout());
    protected final JLabel east = new JLabel();

    public DbColumnListCellRenderer() {
        main.setOpaque(true);
        main.add(this, BorderLayout.CENTER);
        main.add(east, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel center = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        main.setBackground(center.getBackground());
        main.setBorder(center.getBorder());
        center.setBorder(null);
        east.setBorder(null);
        east.setFont(center.getFont());
        east.setForeground(center.getForeground().brighter());
        center.setIcon(getTypeIcon((COLUMN)value));
        center.setText("<html><b>" + getName((COLUMN)value) + "</b>");
        east.setText(getTypeName((COLUMN)value));
        return main;
    }

    abstract protected String getName(COLUMN value);

    abstract protected String getTypeName(COLUMN value);

    abstract protected Icon getTypeIcon(COLUMN value);
}
