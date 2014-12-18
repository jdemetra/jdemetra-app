/*
 * Copyright 2013 National Bank of Belgium
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
package ec.nbdemetra.ui.demo.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.demo.DemoComponentHandler;
import ec.ui.interfaces.ITsList;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentHandler.class)
public final class TsListHandler extends DemoComponentHandler.InstanceOf<ITsList> {

    public TsListHandler() {
        super(ITsList.class);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, ITsList c) {
        toolBar.add(createInfoButton(c));
    }

    static JButton createInfoButton(final ITsList view) {
        JPopupMenu menu = new JPopupMenu();
        List<ITsList.InfoType> currentInfo = Lists.newArrayList(view.getInformation());
        for (final ITsList.InfoType o : ITsList.InfoType.values()) {
            JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(o.name());
            menuItem.setName(o.name());
            menuItem.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<ITsList.InfoType> tmp = Lists.newArrayList(view.getInformation());
                    if (tmp.contains(o)) {
                        tmp.remove(o);
                    } else {
                        tmp.add(o);
                    }
                    view.setInformation(Iterables.toArray(tmp, ITsList.InfoType.class));
                }
            });
            menuItem.setState(currentInfo.contains(o));
            menu.add(menuItem);
        }
        JButton result = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.COMPILE_16, menu);
        result.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.setInformation(null);
            }
        });
        return result;
    }
}
