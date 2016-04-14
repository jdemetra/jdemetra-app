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

import ec.nbdemetra.ui.actions.AbilityAction;
import ec.nbdemetra.ui.nodes.Nodes;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = "ec.nbdemetra.ui.tssave.TsSaveAction")
@ActionRegistration(displayName = "#CTL_TsSaveAction", lazy = false)
@Messages("CTL_TsSaveAction=Save")
public final class TsSaveAction extends AbilityAction<ITsSavable> implements Presenter.Popup {

    public TsSaveAction() {
        super(ITsSavable.class);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return getPopupPresenter(getAll(getActivatedNodes()));
    }

    @Override
    protected void performAction(Iterable<ITsSavable> items) {
    }

    @Override
    public String getName() {
        return null;
    }

    private static List<ITsSavable> getAll(Node[] activatedNodes) {
        return Nodes.asIterable(activatedNodes)
                .transform(o -> o.getLookup().lookup(ITsSavable.class))
                .filter(o -> o != null)
                .toList();
    }

    @Nonnull
    public static JMenuItem getPopupPresenter(@Nonnull List<ITsSavable> data) {
        JMenu result = new JMenu();
        result.setText(Bundle.CTL_TsSaveAction());
        for (ITsSave o : Lookup.getDefault().lookupAll(ITsSave.class)) {
            JMenuItem item = result.add(new ItemAction(o, data));
            item.setText(o.getDisplayName());
        }
        return result;
    }

    private static final class ItemAction extends AbstractAction {

        private final ITsSave o;
        private final List<ITsSavable> data;

        private ItemAction(ITsSave o, List<ITsSavable> data) {
            super(o.getName());
            this.o = o;
            this.data = data;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TsCollection col = TsFactory.instance.createTsCollection();
            for (ITsSavable x : data) {
                col.append(Arrays.asList(x.getAllTs()));
            }
            o.save(col.toArray());
        }
    }
}
