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
package ec.nbdemetra.ui.tsproviders.actions;

import demetra.bridge.TsConverter;
import demetra.ui.NamedService;
import demetra.ui.TsActions;
import demetra.ui.TsManager;
import ec.tss.tsproviders.DataSet;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "Edit", id = "ec.nbdemetra.ui.nodes.actions.OpenWithSetAction")
@ActionRegistration(displayName = "#CTL_OpenWithSetAction", lazy = false)
@NbBundle.Messages("CTL_OpenWithSetAction=Open with")
public final class OpenWithSetAction extends AbstractAction implements Presenter.Popup {

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JMenuItem getPopupPresenter() {
        Node selectedNode = Utilities.actionsGlobalContext().lookup(Node.class);
        DataSet dataSet = selectedNode.getLookup().lookup(DataSet.class);
        JMenu result = new JMenu(Bundle.CTL_OpenWithSetAction());
        for (NamedService o : TsActions.getDefault().getOpenActions()) {
            result.add(new OpenTsAction(o, dataSet)).setText(o.getDisplayName());
        }
        return result;
    }

    @lombok.AllArgsConstructor
    private static final class OpenTsAction extends AbstractAction {

        private final NamedService tsAction;
        private final DataSet dataSet;

        @Override
        public void actionPerformed(ActionEvent e) {
            demetra.tsprovider.DataSet tmp = TsConverter.toDataSet(dataSet);
            TsManager.getDefault()
                    .getTs(tmp, demetra.timeseries.TsInformationType.None)
                    .ifPresent(o -> TsActions.getDefault().openWith(o, tsAction.getName()));
        }
    }
}
