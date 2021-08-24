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

import demetra.tsprovider.util.ObsFormat;
import demetra.ui.components.parts.HasObsFormat;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.demo.DemoComponentHandler;
import ec.nbdemetra.ui.demo.TypedDemoComponentHandler;
import ec.util.various.swing.JCommand;
import demetra.ui.components.parts.HasObsFormatSupport;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import nbbrd.service.ServiceProvider;
import org.openide.awt.DropDownButtonFactory;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(DemoComponentHandler.class)
public final class TsControlHandler extends TypedDemoComponentHandler<HasObsFormat> {

    private final DataFormatCommand dataFormatCommand;

    public TsControlHandler() {
        super(HasObsFormat.class);
        ObsFormat[] dataFormats = {
            ObsFormat.of(Locale.FRENCH, "YYYY-MM", null),
            ObsFormat.of(Locale.US, "MM/YY", "0 $"),
            ObsFormat.of(Locale.GERMAN, "(yyyy)MMM", "0.00 €"),};
        this.dataFormatCommand = new DataFormatCommand(dataFormats);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, final HasObsFormat c) {
        toolBar.add(dataFormatCommand.toButton(c));
        toolBar.addSeparator();
    }

    private static final class DataFormatCommand extends JCommand<HasObsFormat> {

        private final ObsFormat[] dataFormats;
        private int position;

        public DataFormatCommand(ObsFormat[] dataFormats) {
            this.dataFormats = dataFormats;
            this.position = 0;
        }

        @Override
        public void execute(HasObsFormat component) throws Exception {
            component.setObsFormat(dataFormats[position]);
            position = (position + 1) % dataFormats.length;
        }

        @Override
        public boolean isEnabled(HasObsFormat component) {
            return dataFormats.length > 0;
        }

        public JButton toButton(final HasObsFormat c) {
            JPopupMenu popup = new JPopupMenu();
            popup.add(new JCheckBoxMenuItem(HasObsFormatSupport.applyDataFormat(null).toAction(c))).setText("Default");
            popup.addSeparator();
            for (ObsFormat o : dataFormats) {
                popup.add(new JCheckBoxMenuItem(HasObsFormatSupport.applyDataFormat(o).toAction(c))).setText(o.toString());
            }
            JButton result = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.LOCALE_ALTERNATE_16, popup);
            result.addActionListener(toAction(c));
            return result;
        }
    }

}
