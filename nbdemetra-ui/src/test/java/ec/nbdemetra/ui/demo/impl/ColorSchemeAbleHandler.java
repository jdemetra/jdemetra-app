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

import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.demo.DemoComponentHandler;
import ec.ui.commands.ColorSchemeCommand;
import ec.ui.interfaces.IColorSchemeAble;
import ec.util.chart.ColorScheme;
import ec.util.chart.swing.ColorSchemeIcon;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentHandler.class, position = 200)
public final class ColorSchemeAbleHandler extends DemoComponentHandler.InstanceOf<IColorSchemeAble> {

    public ColorSchemeAbleHandler() {
        super(IColorSchemeAble.class);
    }

    @Override
    public void doConfigure(IColorSchemeAble c) {
        c.setColorScheme(null);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, IColorSchemeAble c) {
        final List<JMenuItem> menuItems = new ArrayList<>();

        JMenuItem item;

        JPopupMenu menu = new JPopupMenu();

        item = menu.add(new JCheckBoxMenuItem(ColorSchemeCommand.applyColorScheme(null).toAction(c)));
        item.setText("Default");
        menuItems.add(item);
        menu.addSeparator();
        for (ColorScheme o : DemetraUI.getDefault().getColorSchemes()) {
            item = menu.add(new JCheckBoxMenuItem(ColorSchemeCommand.applyColorScheme(o).toAction(c)));
            item.setText(o.getDisplayName());
            item.setIcon(new ColorSchemeIcon(o));
            menuItems.add(item);
        }

        JButton coloSchemeBtn = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.COLOR_SWATCH_16, menu);
        coloSchemeBtn.addActionListener(new AbstractAction() {
            int i = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                menuItems.get(i++ % menuItems.size()).doClick();
            }
        });
        toolBar.add(coloSchemeBtn);
        toolBar.addSeparator();
    }
}
