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
import ec.nbdemetra.ui.tsproviders.DataSourceNode;
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
@ActionID(category = "File", id = "ec.nbdemetra.ui.interchange.ExportAction")
@ActionRegistration(displayName = "#CTL_ExportAction", lazy = false)
@ActionReferences({
    @ActionReference(path = DataSourceNode.ACTION_PATH, position = 1430, separatorBefore = 1400)
})
@Messages("CTL_ExportAction=Export to")
public final class ExportAction extends AbilityAction<Exportable> implements Presenter.Popup {

    public ExportAction() {
        super(Exportable.class);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return getPopupPresenter(getExportables(getActivatedNodes()));
    }

    @Override
    protected void performAction(Iterable<Exportable> items) {
    }

    @Override
    public String getName() {
        return null;
    }

    private static List<Exportable> getExportables(Node[] activatedNodes) {
        return Nodes.asIterable(activatedNodes)
                .transform(Jdk6Functions.lookupNode(Exportable.class))
                .filter(Predicates.notNull())
                .toList();
    }

    @Nonnull
    public static JMenuItem getPopupPresenter(@Nonnull List<? extends Exportable> exportables) {
        JMenu result = new JMenu();
        result.setText(Bundle.CTL_ExportAction());
        for (InterchangeBroker o : Lookup.getDefault().lookupAll(InterchangeBroker.class)) {
            JMenuItem item = result.add(new Export(o, exportables));
            item.setText(o.getDisplayName());
            item.setEnabled(o.canExport(exportables));
        }
        return result;
    }

    private static final class Export extends AbstractAction {

        private final InterchangeBroker o;
        private final List<? extends Exportable> exportables;

        public Export(InterchangeBroker o, List<? extends Exportable> exportables) {
            super(o.getName());
            this.o = o;
            this.exportables = exportables;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                o.performExport(exportables);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
