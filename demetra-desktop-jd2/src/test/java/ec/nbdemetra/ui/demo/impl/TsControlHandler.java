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
import demetra.desktop.components.parts.HasObsFormat;
import demetra.desktop.components.parts.HasObsFormatSupport;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.demo.DemoComponentHandler;
import ec.util.various.swing.JCommand;
import nbbrd.service.ServiceProvider;
import org.openide.awt.DropDownButtonFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * @author Philippe Charles
 */
@ServiceProvider
public final class TsControlHandler implements DemoComponentHandler {

    private final DataFormatCommand dataFormatCommand;

    public TsControlHandler() {
        ObsFormat[] dataFormats = {
                ObsFormat.of(Locale.FRENCH, "YYYY-MM", null),
                ObsFormat.of(Locale.US, "MM/YY", "0 $"),
                ObsFormat.of(Locale.GERMAN, "(yyyy)MMM", "0.00 €"),};
        this.dataFormatCommand = new DataFormatCommand(dataFormats);
    }

    @Override
    public boolean canHandle(Component c) {
        return c instanceof JComponent && c instanceof HasObsFormat;
    }

    @Override
    public void configure(Component c) {
    }

    @Override
    public void fillToolBar(JToolBar toolBar, Component c) {
        toolBar.add(dataFormatCommand.toButton((JComponent & HasObsFormat) c));
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
        public void execute(HasObsFormat component) {
            component.setObsFormat(dataFormats[position]);
            position = (position + 1) % dataFormats.length;
        }

        @Override
        public boolean isEnabled(HasObsFormat component) {
            return dataFormats.length > 0;
        }

        public <C extends JComponent & HasObsFormat> JButton toButton(C c) {
            JPopupMenu popup = new JPopupMenu();
            popup.add(HasObsFormatSupport.newApplyFormatMenu(c, null));
            popup.addSeparator();
            for (ObsFormat o : dataFormats) {
                popup.add(HasObsFormatSupport.newApplyFormatMenu(c, o));
            }
            JButton result = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.LOCALE_ALTERNATE_16, popup);
            result.addActionListener(toAction(c));
            return result;
        }
    }
}
