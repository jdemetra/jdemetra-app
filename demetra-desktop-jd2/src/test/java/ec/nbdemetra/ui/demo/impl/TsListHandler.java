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

import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.demo.DemoComponentHandler;
import demetra.ui.components.JTsTable;
import static demetra.ui.components.JTsTable.Column.DATA;
import static demetra.ui.components.JTsTable.Column.FREQ;
import static demetra.ui.components.JTsTable.Column.LAST;
import static demetra.ui.components.JTsTable.Column.LENGTH;
import static demetra.ui.components.JTsTable.Column.NAME;
import static demetra.ui.components.JTsTable.Column.START;
import static demetra.ui.components.JTsTable.Column.TS_IDENTIFIER;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentHandler.class)
public final class TsListHandler extends DemoComponentHandler.InstanceOf<JTsTable> {

    public TsListHandler() {
        super(JTsTable.class);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, JTsTable c) {
        toolBar.add(createInfoButton(c));
    }

    static JButton createInfoButton(final JTsTable view) {
        JPopupMenu menu = new JPopupMenu();
        List<JTsTable.Column> currentInfo = view.getColumns();
        for (JTsTable.Column o : Arrays.asList(NAME, FREQ, START, LAST, LENGTH, DATA, TS_IDENTIFIER)) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(o.getName());
            item.setName(o.getName());
            item.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<JTsTable.Column> tmp = new ArrayList<>(view.getColumns());
                    if (tmp.contains(o)) {
                        tmp.remove(o);
                    } else {
                        tmp.add(o);
                    }
                    view.setColumns(tmp);
                }
            });
            item.setState(currentInfo.contains(o));
            menu.add(item);
        }
        JButton result = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.COMPILE_16, menu);
        result.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.setColumns(null);
            }
        });
        return result;
    }
}
