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
import demetra.desktop.interchange.Exportable;
import demetra.desktop.interchange.Interchange;
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
@ActionID(category = "File", id = "demetra.desktop.core.interchange.ExportNodeAction")
@ActionRegistration(displayName = "#CTL_ExportNodeAction", lazy = false)
@Messages("CTL_ExportNodeAction=Export to")
public final class ExportNodeAction extends AbilityNodeAction<Exportable> implements Presenter.Popup {

    public ExportNodeAction() {
        super(Exportable.class);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = Interchange.getDefault().newExportMenu(getExportables(getActivatedNodes()));
        result.setText(Bundle.CTL_ExportNodeAction());
        return result;
    }

    @Override
    protected void performAction(Stream<Exportable> items) {
    }

    @Override
    public String getName() {
        return null;
    }

    private static List<Exportable> getExportables(Node[] activatedNodes) {
        return Stream.of(activatedNodes)
                .map(node -> node.getLookup().lookup(Exportable.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
