/*
 * Copyright 2017 National Bank of Belgium
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
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.sa.multiprocessing.ui.SaBatchUI;
import demetra.desktop.sa.multiprocessing.ui.SaNode;
import demetra.desktop.sa.util.ActionsHelpers;
import demetra.desktop.ui.ActiveViewAction;
import demetra.sa.SaDictionaries;
import demetra.sa.SaProcessingFactory;
import ec.util.list.swing.JListSelection;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static javax.swing.Action.NAME;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Action allowing export of the final decomposition
 *
 * @author Mats Maggi
 */
@ActionID(category = "SaProcessing",
        id = "demetra.sa.multiprocessing.actions.CopyComponents")
@ActionRegistration(displayName = "#CTL_CopyComponents", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Edit.PATH, position = 1430)
})
@Messages("CTL_CopyComponents=Copy Components...")
public final class CopyComponents extends ActiveViewAction<SaBatchUI> {

    public CopyComponents() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_CopyComponents());

    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getSelectionCount() > 0;
    }

    @Override
    protected void process(SaBatchUI cur) {

        SaNode[] selection = context().getSelection();
        Collection<SaProcessingFactory> facs = SaNode.factoriesOf(selection);
        List<String> all = new ArrayList<>(ActionsHelpers.getInstance().merge(facs, helper -> helper.allSeries()));
        List<String> selected = ActionsHelpers.getInstance().merge(facs, helper -> helper.selectedSeries());

        JListSelection fieldSelectionComponent = new JListSelection<>();
        fieldSelectionComponent.setSourceHeader(new JLabel("Available items :"));
        fieldSelectionComponent.setTargetHeader(new JLabel("Selected items :"));
        fieldSelectionComponent.setBorder(new EmptyBorder(10, 10, 10, 10));
        fieldSelectionComponent.setMinimumSize(new Dimension(400, 300));
         fieldSelectionComponent.getSourceModel().clear();
        fieldSelectionComponent.getTargetModel().clear();
        all.removeAll(selected);
        all.forEach(fieldSelectionComponent.getSourceModel()::addElement);
        selected.forEach(fieldSelectionComponent.getTargetModel()::addElement);
        fieldSelectionComponent.setPreferredSize(new Dimension(400, 300));

        NotifyDescriptor d = new NotifyDescriptor(fieldSelectionComponent, "Select fields",
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                null,
                NotifyDescriptor.OK_OPTION);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
            cur.copyComponents(fieldSelectionComponent.getSelectedValues());
        }
    }
}
