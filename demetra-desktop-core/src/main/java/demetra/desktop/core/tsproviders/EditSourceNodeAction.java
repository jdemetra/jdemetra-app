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
package demetra.desktop.core.tsproviders;

import demetra.desktop.actions.AbilityNodeAction;
import org.netbeans.api.actions.Editable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

import java.util.stream.Stream;

@ActionID(category = "Edit", id = EditSourceNodeAction.ID)
@ActionRegistration(displayName = "#CTL_EditSourceAction", lazy = false)
@Messages("CTL_EditSourceAction=Edit")
public final class EditSourceNodeAction extends AbilityNodeAction<Editable> {

    public static final String ID = "demetra.desktop.core.tsproviders.EditSourceAction";

    public EditSourceNodeAction() {
        super(Editable.class, true);
    }

    @Override
    protected void performAction(Stream<Editable> items) {
        items.forEach(Editable::edit);
    }

    @Override
    public String getName() {
        return Bundle.CTL_EditSourceAction();
    }
}
