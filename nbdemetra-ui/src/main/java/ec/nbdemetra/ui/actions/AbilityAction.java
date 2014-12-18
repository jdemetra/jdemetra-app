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
package ec.nbdemetra.ui.actions;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import ec.nbdemetra.ui.Jdk6Functions;
import ec.nbdemetra.ui.Jdk6Predicates;
import ec.nbdemetra.ui.nodes.Nodes;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Philippe Charles
 * @param <T>
 */
public abstract class AbilityAction<T> extends NodeAction {

    final Predicate<Node> predicate;
    final Function<Node, T> function;

    protected AbilityAction(Class<T> ability) {
        this.predicate = Jdk6Predicates.lookupNode(ability);
        this.function = Jdk6Functions.lookupNode(ability);
    }

    abstract protected void performAction(Iterable<T> items);

    @Override
    protected void performAction(Node[] activatedNodes) {
        performAction(Nodes.asIterable(activatedNodes).transform(function).filter(Predicates.notNull()));
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return Nodes.asIterable(activatedNodes).anyMatch(predicate);
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    public HelpCtx getHelpCtx() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
