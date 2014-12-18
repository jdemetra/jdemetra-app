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
package ec.nbdemetra.ui.nodes;

import javax.annotation.Nonnull;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/**
 * A context-aware action that targets a single node. <br>To be used if the
 * action is performed from a single node and might be disabled. <br>The action
 * is disabled if more than one node are selected or if nothing is selected.
 *
 * @author Philippe Charles
 */
public abstract class SingleNodeAction<T extends Node> extends NodeAction {

    private final Class<T> nodeType;

    public SingleNodeAction(@Nonnull Class<T> nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    protected final void performAction(Node[] nodes) {
        performAction((T) nodes[0]);
    }

    @Override
    protected final boolean enable(Node[] activatedNodes) {
        return activatedNodes.length == 1
                && nodeType.isInstance(activatedNodes[0])
                && enable((T) activatedNodes[0]);
    }

    /**
     * Performs some action. Real work is done here.
     *
     * @param activatedNode a non-null node to be used as context
     */
    protected abstract void performAction(@Nonnull T activatedNode);

    /**
     * Checks if the action is enabled or not. It is called before running the
     * action.
     *
     * @param activatedNode a non-null node to be used as context
     * @return
     */
    protected abstract boolean enable(@Nonnull T activatedNode);

    @Deprecated
    protected T getSingleNode(Node[] activatedNodes) {
        return (T) activatedNodes[0];
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
