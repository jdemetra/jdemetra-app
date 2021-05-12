/*
 * Copyright 2015 National Bank of Belgium
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
package ec.nbdemetra.ui.tssave;

import demetra.timeseries.TsCollection;
import demetra.ui.NamedService;
import demetra.ui.TsActions;
import demetra.ui.actions.AbilityAction;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;
import demetra.ui.actions.TsCollectable;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = "ec.nbdemetra.ui.tssave.TsSaveAction")
@ActionRegistration(displayName = "#CTL_TsSaveAction", lazy = false)
@Messages("CTL_TsSaveAction=Save to")
public final class TsSaveAction extends AbilityAction<TsCollectable> implements Presenter.Popup {

    public TsSaveAction() {
        super(TsCollectable.class);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return getPopupPresenter(getAll(getActivatedNodes()));
    }

    @Override
    protected void performAction(Stream<TsCollectable> items) {
    }

    @Override
    public String getName() {
        return null;
    }

    private static List<TsCollectable> getAll(Node[] activatedNodes) {
        return Stream.of(activatedNodes)
                .map(o -> o.getLookup().lookup(TsCollectable.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @NonNull
    public static JMenuItem getPopupPresenter(@NonNull List<TsCollectable> data) {
        JMenu result = new JMenu();
        result.setText(Bundle.CTL_TsSaveAction());
        for (NamedService o : TsActions.getDefault().getSaveActions()) {
            JMenuItem item = result.add(new ItemAction(o, data));
            item.setText(o.getDisplayName());
        }
        return result;
    }

    private static final class ItemAction extends AbstractAction {

        private final NamedService o;
        private final List<TsCollectable> data;

        private ItemAction(NamedService o, List<TsCollectable> data) {
            super(o.getName());
            this.o = o;
            this.data = data;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<TsCollection> xdata = data.stream().map(TsCollectable::getTsCollection).collect(Collectors.toList());
            TsActions.getDefault().saveWith(xdata, o.getName());
        }
    }
}
