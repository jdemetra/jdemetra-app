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

import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.demo.DemoComponentHandler;
import ec.ui.commands.TsGridCommand;
import ec.ui.interfaces.ITsGrid;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentHandler.class)
public final class TsGridHandler extends DemoComponentHandler.InstanceOf<ITsGrid> {

    public TsGridHandler() {
        super(ITsGrid.class);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, ITsGrid c) {
        toolBar.add(createZoomButton(c));
    }

    static JButton createZoomButton(ITsGrid view) {
        JMenu menu = new JMenu();
        for (int o : new int[]{200, 100, 75, 50, 25}) {
            menu.add(new JCheckBoxMenuItem(TsGridCommand.applyZoomRatio(o).toAction(view))).setText(o + "%");
        }
        return DropDownButtonFactory.createDropDownButton(DemetraUiIcon.MAGNIFYING_TOOL, menu.getPopupMenu());
    }
}
