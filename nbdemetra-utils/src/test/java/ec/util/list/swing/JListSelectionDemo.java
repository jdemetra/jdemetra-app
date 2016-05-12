/*
 * Copyright 2016 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.util.list.swing;

import static ec.util.list.swing.JListSelection.APPLY_HORIZONTAL_ACTION;
import static ec.util.list.swing.JListSelection.SELECT_ACTION;
import static ec.util.list.swing.JListSelection.SELECT_ALL_ACTION;
import static ec.util.list.swing.JListSelection.SOURCE_HEADER_PROPERTY;
import static ec.util.list.swing.JListSelection.UNSELECT_ACTION;
import static ec.util.list.swing.JListSelection.UNSELECT_ALL_ACTION;
import ec.util.various.swing.BasicSwingLauncher;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import java.awt.Component;
import java.util.stream.Stream;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.SwingConstants;

/**
 *
 * @author Philippe Charles
 */
public final class JListSelectionDemo {

    public static void main(String[] arg) {
        new BasicSwingLauncher()
                .content(JListSelectionDemo::create)
                .launch();
    }

    private static Component create() {
        JListSelection<FontAwesome> result = new JListSelection<>();
        Stream.of(FontAwesome.values()).limit(10).forEach(result.getSourceModel()::addElement);
        result.setCellRenderer(JLists.cellRendererOf(JListSelectionDemo::applyIcon));
        result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        result.setComponentPopupMenu(createMenu(result).getPopupMenu());
        ToggleHeadersCommand.INSTANCE.executeSafely(result);
        return result;
    }

    private static JMenu createMenu(JListSelection<?> list) {
        ActionMap am = list.getActionMap();
        JMenu result = new JMenu();
        result.add(new JCheckBoxMenuItem(am.get(SELECT_ACTION))).setText("Select");
        result.add(new JCheckBoxMenuItem(am.get(UNSELECT_ACTION))).setText("Unselect");
        result.add(new JCheckBoxMenuItem(am.get(SELECT_ALL_ACTION))).setText("Select all");
        result.add(new JCheckBoxMenuItem(am.get(UNSELECT_ALL_ACTION))).setText("Unselect all");
        result.addSeparator();
        result.add(new JCheckBoxMenuItem(am.get(APPLY_HORIZONTAL_ACTION))).setText("Horizontal");
        result.add(new JCheckBoxMenuItem(ToggleHeadersCommand.INSTANCE.toAction(list))).setText("Headers");
        return result;
    }

    private static final class ToggleHeadersCommand extends JCommand<JListSelection<?>> {

        public static final ToggleHeadersCommand INSTANCE = new ToggleHeadersCommand();

        @Override
        public void execute(JListSelection<?> c) throws Exception {
            if (c.getSourceHeader() == null) {
                c.setSourceHeader(newLabel("Source header:", SwingConstants.LEADING));
                c.setSourceFooter(newLabel("Source footer", SwingConstants.CENTER));
                c.setTargetHeader(newLabel("Target header:", SwingConstants.LEADING));
                c.setTargetFooter(newLabel("Target footer", SwingConstants.CENTER));
            } else {
                c.setSourceHeader(null);
                c.setSourceFooter(null);
                c.setTargetHeader(null);
                c.setTargetFooter(null);
            }
        }

        @Override
        public boolean isSelected(JListSelection<?> c) {
            return c.getSourceHeader() != null;
        }

        @Override
        public ActionAdapter toAction(JListSelection<?> component) {
            return super.toAction(component).withWeakPropertyChangeListener(component, SOURCE_HEADER_PROPERTY);
        }
    }

    private static JLabel newLabel(String text, int alignment) {
        JLabel result = new JLabel(text);
        result.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        result.setHorizontalAlignment(alignment);
        return result;
    }

    private static void applyIcon(JLabel label, FontAwesome fa) {
        label.setIcon(fa.getIcon(label.getForeground(), label.getFont().getSize2D()));
    }
}
