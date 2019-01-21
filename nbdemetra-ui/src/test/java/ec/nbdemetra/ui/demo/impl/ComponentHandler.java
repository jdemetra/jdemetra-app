/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.ui.demo.impl;

import demetra.ui.util.NbComponents;
import ec.nbdemetra.ui.demo.DemoComponentHandler;
import ec.util.table.swing.JTables;
import ec.util.various.swing.FontAwesome;
import static ec.util.various.swing.FontAwesome.FA_HAND_O_UP;
import ec.util.various.swing.JCommand;
import ec.util.various.swing.ext.FontAwesomeUtils;
import static ec.util.various.swing.ext.FontAwesomeUtils.toSize;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import static java.beans.BeanInfo.ICON_MONO_16x16;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentHandler.class, position = 100)
public final class ComponentHandler extends DemoComponentHandler {

    private final JCommand<Component> watchCommand;
    private final JCommand<JComponent> popupMenuCommand;

    public ComponentHandler() {
        this.watchCommand = JCommand.of(ComponentHandler::watch);
        this.popupMenuCommand = new PopupMenuCommand();
    }

    @Override
    public void fillToolBar(JToolBar toolBar, Component c) {
        AbstractButton item;

        item = toolBar.add(watchCommand.toAction(c));
        item.setToolTipText("Watch");
        item.setIcon(ImageUtilities.loadImageIcon("eye.png", false));

        if (c instanceof JComponent) {
            item = new JToggleButton(popupMenuCommand.toAction((JComponent) c));
            item.setIcon(FontAwesomeUtils.getIcon(FA_HAND_O_UP, ICON_MONO_16x16));
            item.setSelectedIcon(FA_HAND_O_UP.getIcon(Color.RED, toSize(ICON_MONO_16x16)));
            item.setToolTipText("Override popup menu");
            toolBar.add(item);
        }

        toolBar.addSeparator();
    }

    private static void watch(Component c) {
        JTable table = new JTable(new PropertyChangeModel(c));
        JTables.setWidthAsPercentages(table, .2, .4, .4);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRendererImpl());
        showInDialog("Watching " + c.getClass().getName(), table);
    }

    private static final class PopupMenuCommand extends JCommand<JComponent> {

        private final JPopupMenu popupMenu;
        private boolean toggled;

        public PopupMenuCommand() {
            this.popupMenu = new JPopupMenu();
            popupMenu.add(new JLabel(FontAwesome.FA_LOCK.getIcon(Color.RED, 40f)));
            this.toggled = false;
        }

        @Override
        public void execute(JComponent component) throws Exception {
            component.setComponentPopupMenu(toggled ? null : popupMenu);
            toggled = !toggled;
        }

        @Override
        public boolean isSelected(JComponent component) {
            return toggled;
        }
    }

    private static final class PropertyChangeModel extends DefaultTableModel implements PropertyChangeListener {

        public PropertyChangeModel(Component c) {
            super(new Object[]{"Name", "Old", "New"}, 0);
            c.addPropertyChangeListener(WeakListeners.propertyChange(this, c));
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            insertRow(0, new Object[]{evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()});
        }
    }

    private static final class DefaultTableCellRendererImpl extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, valueAsString(value), isSelected, hasFocus, row, column);
        }

        private String valueAsString(Object value) {
            if (value == null) {
                return null;
            }
            if (value.getClass().isArray()) {
                Class<?> tmp = value.getClass().getComponentType();
                StringBuilder sb = new StringBuilder("<html><font color=gray>");
                for (String o : tmp.getPackage().getName().split("\\.")) {
                    sb.append(o.charAt(0)).append(".");
                }
                return sb.append("</font>")
                        .append(tmp.getSimpleName())
                        .append(" <font color=gray>[").append(Array.getLength(value)).append("]</font>")
                        .toString();
            }
            return value.toString();
        }
    }

    private static void showInDialog(String title, Component c) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setModalityType(Dialog.ModalityType.MODELESS);
        dialog.getContentPane().add(NbComponents.newJScrollPane(c));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(null);
//        dialog.getRootPane().setWindowDecorationStyle(JRootPane.INFORMATION_DIALOG);
        dialog.setVisible(true);
    }
}
