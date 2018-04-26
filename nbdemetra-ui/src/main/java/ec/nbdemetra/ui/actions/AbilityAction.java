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

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Philippe Charles
 * @param <T>
 */
public abstract class AbilityAction<T> extends NodeAction {

    private final Predicate<Node> predicate;
    private final Function<Node, T> function;

    protected AbilityAction(Class<T> ability) {
        this.predicate = o -> o.getLookup().lookup(ability) != null;
        this.function = o -> o.getLookup().lookup(ability);
    }

    abstract protected void performAction(Stream<T> items);

    @Override
    protected void performAction(Node[] activatedNodes) {
        performAction(Stream.of(activatedNodes).map(function::apply).filter(Objects::nonNull));
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return Stream.of(activatedNodes).anyMatch(predicate::test);
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
