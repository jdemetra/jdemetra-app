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
import ec.tss.tsproviders.utils.DataFormat;
import ec.ui.interfaces.ITsControl;
import java.awt.event.ActionEvent;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentHandler.class, position = 100)
public final class TsControlHandler extends DemoComponentHandler.InstanceOf<ITsControl> {

    public TsControlHandler() {
        super(ITsControl.class);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, final ITsControl c) {
        JPopupMenu dataFormatPopup = new JPopupMenu();

        DataFormat[] dataFormats = {
            new DataFormat(Locale.FRENCH, "YYYY-MM", null),
            new DataFormat(Locale.US, "MM/YY", "0 $"),
            new DataFormat(Locale.GERMAN, "(yyyy)MMM", "0.00 €"),};

        for (final DataFormat o : dataFormats) {
            dataFormatPopup.add(new AbstractAction(o.toString()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    c.setDataFormat(o);
                }
            });
        }
        JButton dataFormatButton = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.LOCALE_ALTERNATE_16, dataFormatPopup);
        dataFormatButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.setDataFormat(null);
            }
        });
        toolBar.add(dataFormatButton);
        toolBar.addSeparator();
    }
}
