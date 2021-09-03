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
package ec.nbdemetra.ui.tsproviders.actions;

import demetra.ui.nodes.SingleNodeAction;
import org.netbeans.api.actions.Editable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Edit", id = "ec.nbdemetra.ui.nodes.EditSourceAction")
@ActionRegistration(displayName = "#CTL_EditSourceAction", lazy = false)
@Messages("CTL_EditSourceAction=Edit")
public final class EditSourceAction extends SingleNodeAction<Node> {

    public EditSourceAction() {
        super(Node.class);
    }

    @Override
    protected void performAction(Node activatedNode) {
        activatedNode.getLookup().lookup(Editable.class).edit();
    }

    @Override
    protected boolean enable(Node activatedNode) {
        return activatedNode.getLookup().lookup(Editable.class) != null;
    }

    @Override
    public String getName() {
        return Bundle.CTL_EditSourceAction();
    }
}
