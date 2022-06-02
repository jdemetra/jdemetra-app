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
package demetra.desktop.core.actions;

import demetra.desktop.NamedService;
import demetra.desktop.TsActionManager;
import demetra.desktop.TsCollectable;
import demetra.desktop.actions.AbilityNodeAction;
import demetra.timeseries.TsCollection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = TsSaveNodeAction.ID)
@ActionRegistration(displayName = "#CTL_TsSaveNodeAction", lazy = false)
@Messages("CTL_TsSaveNodeAction=Save to")
public final class TsSaveNodeAction extends AbilityNodeAction<TsCollectable> implements Presenter.Popup {

    public static final String ID = "demetra.desktop.core.actions.TsSaveNodeAction";

    public TsSaveNodeAction() {
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
        result.setText(Bundle.CTL_TsSaveNodeAction());
        for (NamedService o : TsActionManager.get().getSaveActions()) {
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
            TsActionManager.get().saveWith(xdata, o.getName());
        }
    }
}
