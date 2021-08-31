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
package demetra.ui.actions;

import java.util.Objects;
import java.util.stream.Stream;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Philippe Charles
 * @param <T>
 */
public abstract class AbilityNodeAction<T> extends NodeAction {

    private final Class<T> ability;

    protected AbilityNodeAction(Class<T> ability) {
        this.ability = ability;
    }

    private boolean hasAbility(Node node) {
        return node.getLookup().lookup(ability) != null;
    }

    private T getAbility(Node node) {
        return node.getLookup().lookup(ability);
    }

    abstract protected void performAction(Stream<T> items);

    @Override
    protected void performAction(Node[] activatedNodes) {
        performAction(Stream.of(activatedNodes).map(this::getAbility).filter(Objects::nonNull));
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return Stream.of(activatedNodes).anyMatch(this::hasAbility);
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
