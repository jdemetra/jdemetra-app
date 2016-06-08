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
package ec.nbdemetra.x13.actions;

import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.interchange.ExportAction;
import ec.nbdemetra.ui.interchange.Exportable;
import ec.nbdemetra.ui.nodes.Nodes;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.nbdemetra.x13.X13SpecificationManager;
import ec.satoolkit.x13.X13Specification;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.xml.x13.XmlX13Specification;
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
 * Action on X13 specification workspace node allowing the export
 *
 * @author Mats Maggi
 */
@ActionID(category = "Tools",
        id = "ec.nbdemetra.x13.actions.ExportX13Spec")
@ActionRegistration(displayName = "#CTL_ExportX13Spec", lazy = false)
@ActionReferences({
    @ActionReference(path = X13SpecificationManager.ITEMPATH, position = 1000, separatorAfter = 1090)
})
@NbBundle.Messages("CTL_ExportX13Spec=Export to")
public class ExportX13Spec extends NodeAction implements Presenter.Popup {

    public ExportX13Spec() {
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = ExportAction.getPopupPresenter(getExportables(getActivatedNodes()));
        result.setText(Bundle.CTL_ExportX13Spec());
        return result;
    }

    @Override
    protected void performAction(Node[] nodes) {

    }

    @Override
    protected boolean enable(Node[] nodes) {
        return !Nodes.asIterable(nodes).filter(ItemWsNode.class).filter(ExportX13Spec::isExportable).isEmpty();
    }

    private static boolean isExportable(ItemWsNode o) {
        final WorkspaceItem<X13Specification> xdoc = o.getWorkspace().searchDocument(o.lookup(), X13Specification.class);
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
                .filter(ExportX13Spec::isExportable)
                .transform(o -> (Exportable) new ExportableX13Spec(o))
                .toList();
    }

    private static final class ExportableX13Spec implements Exportable {

        private final ItemWsNode input;

        public ExportableX13Spec(ItemWsNode input) {
            this.input = input;
        }

        @Override
        public Config exportConfig() {
            final WorkspaceItem<X13Specification> xdoc = input.getWorkspace().searchDocument(input.lookup(), X13Specification.class);
            XmlX13Specification spec = new XmlX13Specification();
            spec.copy(xdoc.getElement());

            Formatters.Formatter<XmlX13Specification> formatter = Formatters.onJAXB(XmlX13Specification.class, true);
            Config.Builder b = Config.builder(X13Specification.class.getName(), input.getDisplayName(), "1.0.0")
                    .put("specification", formatter.formatAsString(spec));
            return b.build();
        }
    }
}
