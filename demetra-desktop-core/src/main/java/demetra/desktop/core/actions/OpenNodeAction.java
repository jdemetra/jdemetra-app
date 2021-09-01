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
package demetra.desktop.core.actions;

import demetra.ui.actions.AbilityNodeAction;
import java.util.stream.Stream;

import org.netbeans.api.actions.Openable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = "demetra.desktop.core.actions.OpenNodeAction")
@ActionRegistration(displayName = "#OpenNodeAction", lazy = false)
@Messages("OpenNodeAction=Open")
public final class OpenNodeAction extends AbilityNodeAction<Openable> {

    public OpenNodeAction() {
        super(Openable.class);
    }

    @Override
    protected void performAction(Stream<Openable> items) {
        items.forEach(Openable::open);
    }

    @Override
    public String getName() {
        return Bundle.OpenNodeAction();
    }
}
