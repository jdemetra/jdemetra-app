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
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.tss.sa.SaManager;
import ec.tss.sa.output.BasicConfiguration;
import ec.util.list.swing.JListSelection;
import java.awt.Dimension;
import java.util.ArrayList;
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
 * Action allowing export of selected series components from the processed
 * results of a Seasonal Adjustment batch processing
 *
 * @author Mats Maggi
 */
@ActionID(category = "SaProcessing",
        id = "ec.nbdemetra.sa.actions.CopyComponents")
@ActionRegistration(displayName = "#CTL_CopyComponents", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Edit.PATH, position = 1327)
})
@Messages("CTL_CopyComponents=Copy Components...")
public final class CopyComponents extends AbstractViewAction<SaBatchUI> {

    private final List<String> allFields;
    private final JListSelection<String> fieldSelectionComponent;

    public CopyComponents() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_CopyComponents());
        allFields = BasicConfiguration.allSeries(false, SaManager.instance.getProcessors());
        fieldSelectionComponent = new JListSelection<>();
        fieldSelectionComponent.setSourceHeader(new JLabel("Available items :"));
        fieldSelectionComponent.setTargetHeader(new JLabel("Selected items :"));
        fieldSelectionComponent.setBorder(new EmptyBorder(10, 10, 10, 10));
        fieldSelectionComponent.setMinimumSize(new Dimension(400, 300));
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getSelectionCount() > 0;
    }

    @Override
    protected void process(SaBatchUI cur) {
        DemetraUI demetraUI = DemetraUI.getDefault();
        List<String> tmpAvailable = new ArrayList<>(allFields);
        List<String> selectedElements = demetraUI.getSelectedSeriesFields();
        fieldSelectionComponent.getSourceModel().clear();
        fieldSelectionComponent.getTargetModel().clear();
        tmpAvailable.removeAll(selectedElements);
        tmpAvailable.forEach(fieldSelectionComponent.getSourceModel()::addElement);
        selectedElements.forEach(fieldSelectionComponent.getTargetModel()::addElement);
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
