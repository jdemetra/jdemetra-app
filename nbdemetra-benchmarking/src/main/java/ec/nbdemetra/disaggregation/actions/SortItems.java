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
package ec.nbdemetra.disaggregation.actions;

import ec.nbdemetra.disaggregation.TsDisaggregationModelManager;
import ec.nbdemetra.ui.actions.AbstractSortItems;
import ec.nbdemetra.ws.nodes.WsNode;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "ec.nbdemetra.disaggregation.actions.SortItems")
@ActionRegistration(
        displayName = "#CTL_SortItems")
@ActionReferences({
    @ActionReference(path = "Shortcuts", name = "S"),
    @ActionReference(path = TsDisaggregationModelManager.PATH, position = 1100)})
@Messages("CTL_SortItems=Sort")
public final class SortItems extends AbstractSortItems {
    public SortItems(WsNode context) {
        super(context);
    }
}
