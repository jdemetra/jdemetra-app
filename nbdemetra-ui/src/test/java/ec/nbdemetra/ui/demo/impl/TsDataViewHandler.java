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
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.DemoUtils;
import ec.ui.interfaces.ITsDataView;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JToolBar;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentHandler.class)
public final class TsDataViewHandler extends DemoComponentHandler.InstanceOf<ITsDataView> {

    public TsDataViewHandler() {
        super(ITsDataView.class);
    }

    final TsData data = random();

    @Override
    public void doConfigure(ITsDataView c) {
        c.setTsData(data);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, final ITsDataView c) {
        toolBar.add(new AbstractAction("Random data") {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.setTsData(random());
            }
        });
        toolBar.addSeparator();
    }

    static TsData random() {
        return DemoUtils.randomTsCollection(1).get(0).getTsData();
    }
}
