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

import demetra.ui.components.parts.HasChart;
import demetra.ui.components.parts.HasChartSupport;
import demetra.ui.util.FontAwesomeUtils;
import ec.nbdemetra.ui.demo.DemoComponentHandler;
import ec.util.various.swing.FontAwesome;
import nbbrd.service.ServiceProvider;
import org.openide.awt.DropDownButtonFactory;

import javax.swing.*;
import java.awt.*;
import java.beans.BeanInfo;

/**
 * @author Philippe Charles
 */
@ServiceProvider
public final class TsChartHandler implements DemoComponentHandler {

    @Override
    public boolean canHandle(Component c) {
        return c instanceof JComponent && c instanceof HasChart;
    }

    @Override
    public void configure(Component c) {
        ((HasChart) c).setTitle("This is a title");
    }

    @Override
    public void fillToolBar(JToolBar toolBar, Component c) {
        JPopupMenu menu = new JPopupMenu();

        menu.add(HasChartSupport.newToggleTitleVisibilityMenu((JComponent & HasChart) c));
        menu.add(HasChartSupport.newToggleLegendVisibilityMenu((JComponent & HasChart) c));
//        menu.add(new JCheckBoxMenuItem(TsChartCommand.showAll().toAction(c))).setText("Show all");

        toolBar.add(DropDownButtonFactory.createDropDownButton(FontAwesomeUtils.getIcon(FontAwesome.FA_BAR_CHART_O, BeanInfo.ICON_MONO_16x16), menu));
        toolBar.addSeparator();
    }
}
