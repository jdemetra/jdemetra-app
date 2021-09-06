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
package demetra.desktop.actions;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @param <ABILITY>
 * @author Philippe Charles
 */
public abstract class AbilityNodeAction<ABILITY> extends NodeAction {

    private final Class<ABILITY> ability;
    private final boolean single;

    protected AbilityNodeAction(Class<ABILITY> ability) {
        this(ability, false);
    }

    protected AbilityNodeAction(Class<ABILITY> ability, boolean single) {
        this.ability = ability;
        this.single = single;
    }

    protected abstract void performAction(Stream<ABILITY> items);

    protected boolean enable(Stream<ABILITY> items) {
        return items.findAny().isPresent();
    }

    private ABILITY lookupAbility(Node node) {
        return node.getLookup().lookup(ability);
    }

    private Stream<ABILITY> lookupAbilityStream(Node[] activatedNodes) {
        return Stream.of(activatedNodes).map(this::lookupAbility).filter(Objects::nonNull);
    }

    @Override
    final protected void performAction(Node[] activatedNodes) {
        performAction(lookupAbilityStream(activatedNodes));
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        if (single && activatedNodes.length != 1) {
            return false;
        }
        return enable(lookupAbilityStream(activatedNodes));
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
