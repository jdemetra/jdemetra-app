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

import ec.nbdemetra.ui.nodes.SingleNodeAction;
import java.awt.Toolkit;
import java.io.IOException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

import org.openide.util.NbBundle.Messages;

@Deprecated
@Messages("CTL_CopySourceAction=Copy")
public final class CopySourceAction extends SingleNodeAction<Node> {

    public CopySourceAction() {
        super(Node.class);
    }

    @Override
    protected void performAction(Node activatedNode) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(activatedNode.clipboardCopy(), null);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected boolean enable(Node activatedNode) {
        return activatedNode.canCopy();
    }

    @Override
    public String getName() {
        return Bundle.CTL_CopySourceAction();
    }
}
