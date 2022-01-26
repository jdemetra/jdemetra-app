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
package demetra.desktop.x13.ui.actions;

import demetra.desktop.Config;
import demetra.desktop.interchange.Exportable;
import demetra.desktop.interchange.Interchange;
import demetra.desktop.x13.ui.X13SpecManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.information.InformationSet;
import demetra.x13.X13Spec;
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
        id = "demetra.desktop.x13.ui.actions.ExportX13Spec")
@ActionRegistration(displayName = "#CTL_ExportX13Spec", lazy = false)
@ActionReferences({
    @ActionReference(path = X13SpecManager.ITEMPATH, position = 1000, separatorAfter = 1090)
})
@NbBundle.Messages("CTL_ExportX13Spec=Export to")
public class ExportX13Spec extends NodeAction implements Presenter.Popup {

    public ExportX13Spec() {
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = Interchange.getDefault().newExportMenu(getExportables(getActivatedNodes()));
        result.setText(Bundle.CTL_ExportX13Spec());
        return result;
    }

    @Override
    protected void performAction(Node[] nodes) {

    }

    @Override
    protected boolean enable(Node[] nodes) {
        return Stream.of(nodes).anyMatch(ExportX13Spec::isExportable);
    }

    private static boolean isExportable(Node o) {
        return o instanceof ItemWsNode && isExportable((ItemWsNode) o);
    }

    private static boolean isExportable(ItemWsNode o) {
        final WorkspaceItem<X13Spec> xdoc = o.getWorkspace().searchDocument(o.lookup(), X13Spec.class);
        return xdoc != null && xdoc.getStatus() != WorkspaceItem.Status.System;
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
                .filter(ExportX13Spec::isExportable)
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
//            final WorkspaceItem<TramoSpec> xdoc = input.getWorkspace().searchDocument(input.lookup(), TramoSpec.class);
//            InformationSet set = xdoc.getElement().write(true);
//            XmlInformationSet xmlSet = new XmlInformationSet();
//            xmlSet.copy(set);
//
//            IFormatter<XmlInformationSet> formatter = Formatters.onJAXB(XmlInformationSet.class, true);
//            Config.Builder b = Config.builder(TramoSpecification.class.getName(), input.getDisplayName(), "1.0.0")
//                    .parameter("specification", formatter.formatAsString(xmlSet));
//            return b.build();
            return null;
        }
    }
}
