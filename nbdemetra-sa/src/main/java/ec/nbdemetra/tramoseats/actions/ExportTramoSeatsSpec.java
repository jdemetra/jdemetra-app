/*
 * Copyright 2016 National Bank of Belgium
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
package ec.nbdemetra.tramoseats.actions;

import ec.nbdemetra.tramoseats.TramoSeatsSpecificationManager;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.interchange.ExportAction;
import ec.nbdemetra.ui.interchange.Exportable;
import ec.nbdemetra.ui.nodes.Nodes;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.xml.tramoseats.XmlTramoSeatsSpecification;
import java.util.List;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.Presenter;

/**
 * Action on TramoSeats specification workspace node allowing the export
 *
 * @author Mats Maggi
 */
@ActionID(category = "Tools",
        id = "ec.nbdemetra.tramoseats.actions.ExportTramoSeatsSpec")
@ActionRegistration(displayName = "#CTL_ExportTramoSeatsSpec", lazy = false)
@ActionReferences({
    @ActionReference(path = TramoSeatsSpecificationManager.ITEMPATH, position = 1000, separatorAfter = 1090)
})
@NbBundle.Messages("CTL_ExportTramoSeatsSpec=Export to")
public class ExportTramoSeatsSpec extends NodeAction implements Presenter.Popup {

    public ExportTramoSeatsSpec() {
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = ExportAction.getPopupPresenter(getExportables(getActivatedNodes()));
        result.setText(Bundle.CTL_ExportTramoSeatsSpec());
        return result;
    }

    @Override
    protected void performAction(Node[] nodes) {

    }

    @Override
    protected boolean enable(Node[] nodes) {
        return !Nodes.asIterable(nodes).filter(ItemWsNode.class).filter(ExportTramoSeatsSpec::isExportable).isEmpty();
    }

    private static boolean isExportable(ItemWsNode o) {
        final WorkspaceItem<TramoSeatsSpecification> xdoc = o.getWorkspace().searchDocument(o.lookup(), TramoSeatsSpecification.class);
        return xdoc != null && !xdoc.getElement().isSystem();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    private static List<Exportable> getExportables(Node[] activatedNodes) {
        return Nodes.asIterable(activatedNodes)
                .filter(ItemWsNode.class)
                .filter(ExportTramoSeatsSpec::isExportable)
                .transform(o -> (Exportable) new ExportableTramoSeatsSpec(o))
                .toList();
    }

    private static final class ExportableTramoSeatsSpec implements Exportable {

        private final ItemWsNode input;

        public ExportableTramoSeatsSpec(ItemWsNode input) {
            this.input = input;
        }

        @Override
        public Config exportConfig() {
            final WorkspaceItem<TramoSeatsSpecification> xdoc = input.getWorkspace().searchDocument(input.lookup(), TramoSeatsSpecification.class);
            XmlTramoSeatsSpecification spec = new XmlTramoSeatsSpecification();
            spec.copy(xdoc.getElement());

            Formatters.Formatter<XmlTramoSeatsSpecification> formatter = Formatters.onJAXB(XmlTramoSeatsSpecification.class, true);
            Config.Builder b = Config.builder(TramoSeatsSpecification.class.getName(), input.getDisplayName(), "1.0.0")
                    .put("specification", formatter.formatAsString(spec));
            return b.build();
        }
    }
}
