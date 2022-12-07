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
import demetra.desktop.ui.ActiveViewAction;
import demetra.sa.SaDictionaries;
import java.util.ArrayList;
import java.util.List;
import static javax.swing.Action.NAME;
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
        id = "demetra.sa.multiprocessing.actions.CopyDecomposition")
@ActionRegistration(displayName = "#CTL_CopyDecomposition", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Edit.PATH, position = 1410)
})
@Messages("CTL_CopyDecomposition=Copy Decomposition")
public final class CopyDecomposition extends ActiveViewAction<SaBatchUI> {

//    private final List<String> allFields;
//    private final JListSelection<String> fieldSelectionComponent;

    public CopyDecomposition() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_CopyDecomposition());
//        allFields = BasicConfiguration.allSeries(false, SaManager.instance.getProcessors());
//        fieldSelectionComponent = new JListSelection<>();
//        fieldSelectionComponent.setSourceHeader(new JLabel("Available items :"));
//        fieldSelectionComponent.setTargetHeader(new JLabel("Selected items :"));
//        fieldSelectionComponent.setBorder(new EmptyBorder(10, 10, 10, 10));
//        fieldSelectionComponent.setMinimumSize(new Dimension(400, 300));
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        if (ui == null || ui.getSelectionCount() != 1)
            enabled=false;
        else{
            SaNode node = ui.getSelection()[0];
            enabled=node.isProcessed();
        }
    }

    @Override
    protected void process(SaBatchUI cur) {
        List<String> decomp=new ArrayList<>();
        decomp.add(SaDictionaries.Y);
        decomp.add(SaDictionaries.SA);
        decomp.add(SaDictionaries.T);
        decomp.add(SaDictionaries.S);
        decomp.add(SaDictionaries.I);
        cur.copyComponents(decomp);
    }
}
