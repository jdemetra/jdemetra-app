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

import ec.nbdemetra.ui.demo.DemoComponentHandler;
import internal.ui.components.HasChartCommands;
import ec.util.various.swing.FontAwesome;
import demetra.ui.util.FontAwesomeUtils;
import java.beans.BeanInfo;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.openide.awt.DropDownButtonFactory;
import demetra.ui.components.parts.HasChart;
import ec.nbdemetra.ui.demo.TypedDemoComponentHandler;
import nbbrd.service.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(DemoComponentHandler.class)
public final class TsChartHandler extends TypedDemoComponentHandler<HasChart> {

    public TsChartHandler() {
        super(HasChart.class);
    }

    @Override
    public void doConfigure(HasChart c) {
        c.setTitle("This is a title");
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, HasChart c) {
        JPopupMenu menu = new JPopupMenu();

        menu.add(new JCheckBoxMenuItem(HasChartCommands.toggleTitleVisibility().toAction(c))).setText("Show title");
        menu.add(new JCheckBoxMenuItem(HasChartCommands.toggleLegendVisibility().toAction(c))).setText("Show legend");
//        menu.add(new JCheckBoxMenuItem(TsChartCommand.showAll().toAction(c))).setText("Show all");

        toolBar.add(DropDownButtonFactory.createDropDownButton(FontAwesomeUtils.getIcon(FontAwesome.FA_BAR_CHART_O, BeanInfo.ICON_MONO_16x16), menu));
        toolBar.addSeparator();
    }
}
