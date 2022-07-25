/*
 * Copyright 2013 National Bank of Belgium
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
package demetra.desktop.disaggregation.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Tools",
id = "ec.nbdemetra.disaggregation.actions.OpenTsModelDisaggregation")
@ActionRegistration(
    displayName = "#CTL_OpenTsModelDisaggregation")
@ActionReferences({
    @ActionReference(path = TsDisaggregationModelManager.ITEMPATH, position = 1600, separatorBefore = 1590)
})
@Messages("CTL_OpenTsModelDisaggregation=Open")
public final class OpenTsModelDisaggregation implements ActionListener {

    private final WsNode context;

    public OpenTsModelDisaggregation(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<TsDisaggregationModelDocument> doc = context.getWorkspace().searchDocument(context.lookup(), TsDisaggregationModelDocument.class);
        TsDisaggregationModelManager mgr = WorkspaceFactory.getInstance().getManager(TsDisaggregationModelManager.class);
        mgr.openDocument(doc);
    }
}
