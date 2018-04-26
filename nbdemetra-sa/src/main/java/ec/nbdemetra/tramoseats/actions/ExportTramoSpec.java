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

import ec.nbdemetra.tramoseats.TramoSpecificationManager;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.interchange.ExportAction;
import ec.nbdemetra.ui.interchange.Exportable;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.IFormatter;
import ec.tss.xml.information.XmlInformationSet;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
 * Action on Tramo specification workspace node allowing the export
 *
 * @author Mats Maggi
 */
@ActionID(category = "Tools",
        id = "ec.nbdemetra.tramoseats.actions.ExportTramoSpec")
@ActionRegistration(displayName = "#CTL_ExportTramoSpec", lazy = false)
@ActionReferences({
    @ActionReference(path = TramoSpecificationManager.ITEMPATH, position = 1000, separatorAfter = 1090)
})
@NbBundle.Messages("CTL_ExportTramoSpec=Export to")
public class ExportTramoSpec extends NodeAction implements Presenter.Popup {

    public ExportTramoSpec() {
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = ExportAction.getPopupPresenter(getExportables(getActivatedNodes()));
        result.setText(Bundle.CTL_ExportTramoSpec());
        return result;
    }

    @Override
    protected void performAction(Node[] nodes) {

    }

    @Override
    protected boolean enable(Node[] nodes) {
        return Stream.of(nodes).anyMatch(ExportTramoSpec::isExportable);
    }

    private static boolean isExportable(Node o) {
        return o instanceof ItemWsNode && isExportable((ItemWsNode) o);
    }

    private static boolean isExportable(ItemWsNode o) {
        final WorkspaceItem<TramoSpecification> xdoc = o.getWorkspace().searchDocument(o.lookup(), TramoSpecification.class);
        return xdoc != null && !TramoSpecification.isSystem(xdoc.getElement());
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
        return Stream.of(activatedNodes)
                .filter(ExportTramoSpec::isExportable)
                .map(ItemWsNode.class::cast)
                .map(ExportableTramoSpec::new)
                .collect(Collectors.toList());
    }

    private static final class ExportableTramoSpec implements Exportable {

        private final ItemWsNode input;

        public ExportableTramoSpec(ItemWsNode input) {
            this.input = input;
        }

        @Override
        public Config exportConfig() {
            final WorkspaceItem<TramoSpecification> xdoc = input.getWorkspace().searchDocument(input.lookup(), TramoSpecification.class);
            InformationSet set = xdoc.getElement().write(true);
            XmlInformationSet xmlSet = new XmlInformationSet();
            xmlSet.copy(set);

            IFormatter<XmlInformationSet> formatter = Formatters.onJAXB(XmlInformationSet.class, true);
            Config.Builder b = Config.builder(TramoSpecification.class.getName(), input.getDisplayName(), "1.0.0")
                    .put("specification", formatter.formatAsString(xmlSet));
            return b.build();
        }
    }
}
