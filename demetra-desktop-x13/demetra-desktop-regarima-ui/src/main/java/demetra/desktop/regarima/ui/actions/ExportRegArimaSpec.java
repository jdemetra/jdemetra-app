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
package demetra.desktop.regarima.ui.actions;

import demetra.desktop.Config;
import demetra.desktop.interchange.Exportable;
import demetra.desktop.interchange.InterchangeManager;
import demetra.desktop.regarima.documents.RegArimaSpecManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.information.InformationSet;
import demetra.regarima.RegArimaSpec;
import demetra.toolkit.io.xml.information.XmlInformationSet;
import demetra.x13.io.information.RegArimaSpecMapping;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JMenuItem;
import nbbrd.io.text.Formatter;
import nbbrd.io.xml.bind.Jaxb;
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
 * Action on RegArima specification workspace node allowing the export
 *
 * @author Mats Maggi
 */
@ActionID(category = "Tools",
        id = "demetra.desktop.regarima.ui.actions.ExportRegArimaSpec")
@ActionRegistration(displayName = "#CTL_ExportRegArimaSpec", lazy = false)
@ActionReferences({
    @ActionReference(path = RegArimaSpecManager.ITEMPATH, position = 1000, separatorAfter = 1090)
})
@NbBundle.Messages("CTL_ExportRegArimaSpec=Export to")
public class ExportRegArimaSpec extends NodeAction implements Presenter.Popup {

    public ExportRegArimaSpec() {
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = InterchangeManager.get().newExportMenu(getExportables(getActivatedNodes()));
        result.setText(Bundle.CTL_ExportRegArimaSpec());
        return result;
    }

    @Override
    protected void performAction(Node[] nodes) {

    }

    @Override
    protected boolean enable(Node[] nodes) {
        return Stream.of(nodes).anyMatch(ExportRegArimaSpec::isExportable);
    }

    private static boolean isExportable(Node o) {
        return o instanceof ItemWsNode && isExportable((ItemWsNode) o);
    }

    private static boolean isExportable(ItemWsNode o) {
        final WorkspaceItem<RegArimaSpec> xdoc = o.getWorkspace().searchDocument(o.lookup(), RegArimaSpec.class);
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
                .filter(ExportRegArimaSpec::isExportable)
                .map(ItemWsNode.class::cast)
                .map(ExportableRegArimaSpec::new)
                .collect(Collectors.toList());
    }

    private static final class ExportableRegArimaSpec implements Exportable {

        private final ItemWsNode input;

        public ExportableRegArimaSpec(ItemWsNode input) {
            this.input = input;
        }

        @Override
        public Config exportConfig() {
            final WorkspaceItem<RegArimaSpec> xdoc = input.getWorkspace().searchDocument(input.lookup(), RegArimaSpec.class);
            InformationSet set = RegArimaSpecMapping.SERIALIZER_V3.write(xdoc.getElement(), true);
            Config.Builder b = Config.builder(RegArimaSpec.class.getName(), input.getDisplayName(), "3.0.0")
                    .parameter("specification", INFORMATIONFORMATTER.formatAsString(set));
            return b.build();
        }
    }

    private static final Formatter<InformationSet> INFORMATIONFORMATTER = Jaxb.Formatter.of(XmlInformationSet.class).asFormatter()
            .compose(o -> {
                XmlInformationSet result = new XmlInformationSet();
                result.copy(o);
                return result;
            });
}
