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

import demetra.ui.ColorSchemeManager;
import demetra.ui.components.parts.HasColorScheme;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.demo.DemoComponentHandler;
import ec.nbdemetra.ui.demo.TypedDemoComponentHandler;
import demetra.ui.components.parts.HasColorSchemeSupport;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import nbbrd.service.ServiceProvider;
import org.openide.awt.DropDownButtonFactory;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(DemoComponentHandler.class)
public final class ColorSchemeAbleHandler extends TypedDemoComponentHandler<HasColorScheme> {

    public ColorSchemeAbleHandler() {
        super(HasColorScheme.class);
    }

    @Override
    public void doConfigure(HasColorScheme c) {
        c.setColorScheme(null);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, HasColorScheme c) {
        JPopupMenu menu = HasColorSchemeSupport.menuOf(c).getPopupMenu();

        List<Action> colorSchemes = ColorSchemeManager.getDefault()
                .getColorSchemes()
                .stream()
                .map(HasColorSchemeSupport::getApplyColorSchemeCommand)
                .map(o -> o.toAction(c))
                .collect(Collectors.toList());

        JButton coloSchemeBtn = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.COLOR_SWATCH_16, menu);
        coloSchemeBtn.addActionListener(new AbstractAction() {
            int i = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                colorSchemes.get(i++ % colorSchemes.size()).actionPerformed(e);
            }
        });
        toolBar.add(coloSchemeBtn);
        toolBar.addSeparator();
    }
}
