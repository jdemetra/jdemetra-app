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
package ec.nbdemetra.ui.interchange;

import com.google.common.base.Predicates;
import ec.nbdemetra.ui.Jdk6Functions;
import ec.nbdemetra.ui.actions.AbilityAction;
import ec.nbdemetra.ui.nodes.Nodes;
import ec.nbdemetra.ui.tsproviders.ProviderNode;
import ec.nbdemetra.ui.tsproviders.ProvidersNode;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = "ec.nbdemetra.ui.interchange.ImportAction")
@ActionRegistration(displayName = "#CTL_ImportAction", lazy = false)
@ActionReferences({
    @ActionReference(path = ProvidersNode.ACTION_PATH, position = 1430, separatorBefore = 1400),
    @ActionReference(path = ProviderNode.ACTION_PATH, position = 1430, separatorBefore = 1400)
})
@Messages("CTL_ImportAction=Import from")
public final class ImportAction extends AbilityAction<Importable> implements Presenter.Popup {

    private final Node fakeProviderNode = new ProvidersNode();

    public ImportAction() {
        super(Importable.class);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        Node[] nodes = getActivatedNodes();
        if (ProvidersNode.isProvidersNode(nodes)) {
            nodes = new Node[]{fakeProviderNode};
        }
        return getPopupPresenter(getImportables(nodes));
    }

    @Override
    protected void performAction(Iterable<Importable> items) {
    }

    @Override
    public String getName() {
        return null;
    }

    private static List<Importable> getImportables(Node[] activatedNodes) {
        return Nodes.asIterable(activatedNodes)
                .transform(Jdk6Functions.lookupNode(Importable.class))
                .filter(Predicates.notNull())
                .toList();
    }

    @Nonnull
    public static JMenuItem getPopupPresenter(@Nonnull List<? extends Importable> importables) {
        JMenu result = new JMenu();
        result.setText(Bundle.CTL_ImportAction());
        for (InterchangeBroker o : Lookup.getDefault().lookupAll(InterchangeBroker.class)) {
            JMenuItem item = result.add(new Import(o, importables));
            item.setText(o.getDisplayName());
            item.setEnabled(o.canImport(importables));
        }
        return result;
    }

    private static final class Import extends AbstractAction {

        private final InterchangeBroker o;
        private final List<? extends Importable> importables;

        public Import(InterchangeBroker o, List<? extends Importable> importables) {
            super(o.getName());
            this.o = o;
            this.importables = importables;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                o.performImport(importables);
            } catch (IOException | IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
