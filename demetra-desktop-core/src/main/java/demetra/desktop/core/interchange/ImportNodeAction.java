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
package demetra.desktop.core.interchange;

import demetra.desktop.actions.AbilityNodeAction;
import demetra.desktop.interchange.Importable;
import demetra.desktop.interchange.Interchange;
import demetra.desktop.nodes.AbstractNodeBuilder;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

import javax.swing.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = "demetra.desktop.core.interchange.ImportNodeAction")
@ActionRegistration(displayName = "#CTL_ImportNodeAction", lazy = false)
@Messages("CTL_ImportNodeAction=Import from")
public final class ImportNodeAction extends AbilityNodeAction<Importable> implements Presenter.Popup {

    // FIXME: old code was "new ProvidersNode()" -> missing ability?
    private final Node fakeProviderNode = new AbstractNodeBuilder().build();

    public ImportNodeAction() {
        super(Importable.class);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        Node[] nodes = getActivatedNodes();
        if (isProvidersNode(nodes)) {
            nodes = new Node[]{fakeProviderNode};
        }
        JMenuItem result = Interchange.getDefault().newImportMenu(getImportables(nodes));
        result.setText(Bundle.CTL_ImportNodeAction());
        return result;
    }

    @Override
    protected void performAction(Stream<Importable> items) {
    }

    @Override
    public String getName() {
        return null;
    }

    private static List<Importable> getImportables(Node[] activatedNodes) {
        return Stream.of(activatedNodes)
                .map(node -> node.getLookup().lookup(Importable.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static boolean isProvidersNode(Node[] activatedNodes) {
        return activatedNodes != null && activatedNodes.length == 0;
    }
}
