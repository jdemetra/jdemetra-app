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
import ec.tss.TsInformation;
import ec.ui.interfaces.IReadDataBlockView;
import static ec.util.various.swing.FontAwesome.FA_ERASER;
import ec.util.various.swing.JCommand;
import demetra.ui.util.FontAwesomeUtils;
import static java.beans.BeanInfo.ICON_COLOR_16x16;
import javax.swing.JButton;
import javax.swing.JToolBar;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentHandler.class)
public final class ReadDataBlockViewHandler extends DemoComponentHandler.InstanceOf<IReadDataBlockView> {

    private final RandomTsCommand<IReadDataBlockView> randomCommand;
    private final JCommand<IReadDataBlockView> resetCommand;

    public ReadDataBlockViewHandler() {
        super(IReadDataBlockView.class);
        this.randomCommand = RandomTsCommand.of(ReadDataBlockViewHandler::apply);
        this.resetCommand = JCommand.of(IReadDataBlockView::reset);
    }

    @Override
    public void doConfigure(IReadDataBlockView c) {
        randomCommand.executeSafely(c);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, IReadDataBlockView c) {
        JButton item;

        toolBar.add(randomCommand.toButton(c));

        item = toolBar.add(resetCommand.toAction(c));
        item.setIcon(FontAwesomeUtils.getIcon(FA_ERASER, ICON_COLOR_16x16));

        toolBar.addSeparator();
    }

    private static void apply(IReadDataBlockView c, TsInformation ts) {
        c.setDataBlock(ts.data);
    }
}
