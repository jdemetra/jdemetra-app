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
import ec.ui.commands.TsControlCommand;
import ec.ui.interfaces.ITsControl;
import ec.util.various.swing.JCommand;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
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

    private final DataFormatCommand dataFormatCommand;

    public TsControlHandler() {
        super(ITsControl.class);
        DataFormat[] dataFormats = {
            new DataFormat(Locale.FRENCH, "YYYY-MM", null),
            new DataFormat(Locale.US, "MM/YY", "0 $"),
            new DataFormat(Locale.GERMAN, "(yyyy)MMM", "0.00 €"),};
        this.dataFormatCommand = new DataFormatCommand(dataFormats);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, final ITsControl c) {
        toolBar.add(dataFormatCommand.toButton(c));
        toolBar.addSeparator();
    }

    private static final class DataFormatCommand extends JCommand<ITsControl> {

        private final DataFormat[] dataFormats;
        private int position;

        public DataFormatCommand(DataFormat[] dataFormats) {
            this.dataFormats = dataFormats;
            this.position = 0;
        }

        @Override
        public void execute(ITsControl component) throws Exception {
            component.setDataFormat(dataFormats[position]);
            position = (position + 1) % dataFormats.length;
        }

        @Override
        public boolean isEnabled(ITsControl component) {
            return dataFormats.length > 0;
        }

        public JButton toButton(final ITsControl c) {
            JPopupMenu popup = new JPopupMenu();
            popup.add(new JCheckBoxMenuItem(TsControlCommand.applyDataFormat(null).toAction(c))).setText("Default");
            popup.addSeparator();
            for (DataFormat o : dataFormats) {
                popup.add(new JCheckBoxMenuItem(TsControlCommand.applyDataFormat(o).toAction(c))).setText(o.toString());
            }
            JButton result = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.LOCALE_ALTERNATE_16, popup);
            result.addActionListener(toAction(c));
            return result;
        }
    }

}
