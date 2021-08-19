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
import demetra.ui.components.JTsGrid;
import ec.nbdemetra.ui.demo.TypedDemoComponentHandler;
import internal.ui.components.HasGridCommands;
import javax.swing.JButton;
import javax.swing.JToolBar;
import nbbrd.service.ServiceProvider;
import org.openide.awt.DropDownButtonFactory;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(DemoComponentHandler.class)
public final class TsGridHandler extends TypedDemoComponentHandler<JTsGrid> {

    public TsGridHandler() {
        super(JTsGrid.class);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, JTsGrid c) {
        toolBar.add(createZoomButton(c));
    }

    static JButton createZoomButton(JTsGrid view) {
        return DropDownButtonFactory.createDropDownButton(DemetraUiIcon.MAGNIFYING_TOOL, HasGridCommands.newZoomRationMenu(view).getPopupMenu());
    }
}
