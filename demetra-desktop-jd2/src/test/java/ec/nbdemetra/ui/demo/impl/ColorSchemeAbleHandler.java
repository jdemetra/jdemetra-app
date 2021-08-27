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

import demetra.ui.components.parts.HasColorScheme;
import demetra.ui.components.parts.HasColorSchemeSupport;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.demo.DemoComponentHandler;
import nbbrd.service.ServiceProvider;
import org.openide.awt.DropDownButtonFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Philippe Charles
 */
@ServiceProvider
public final class ColorSchemeAbleHandler implements DemoComponentHandler {

    @Override
    public boolean canHandle(Component c) {
        return c instanceof JComponent && c instanceof HasColorScheme;
    }

    @Override
    public void configure(Component c) {
        ((HasColorScheme) c).setColorScheme(null);
    }

    @Override
    public void fillToolBar(JToolBar toolBar, Component c) {
        JMenu menu = HasColorSchemeSupport.menuOf((JComponent & HasColorScheme) c);

        JButton colorSchemeBtn = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.COLOR_SWATCH_16, menu.getPopupMenu());
        colorSchemeBtn.addActionListener(new AbstractAction() {
            int i = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                menu.getItem(i++ % menu.getItemCount()).getAction().actionPerformed(e);
            }
        });
        toolBar.add(colorSchemeBtn);
        toolBar.addSeparator();
    }
}
