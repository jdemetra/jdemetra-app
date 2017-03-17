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

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import ec.nbdemetra.ui.awt.JProperty;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 * Decorator pattern applied to a node.
 *
 * @author Philippe Charles
 */
public class DecoratedNode extends FilterNode {

    public static final String HTML_DECORATOR_PROPERTY = "htmlDecorator";
    public static final String PREFERRED_ACTION_DECORATOR_PROPERTY = "preferredActionDecorator";
    protected final JProperty<Function<Node, String>> htmlDecorator;
    protected final JProperty<Function<Node, Action>> preferredActionDecorator;

    public DecoratedNode(Node original) {
        this(original, o -> true);
    }

    public DecoratedNode(Node original, Predicate<Node> filter) {
        super(original, original.isLeaf() ? Children.LEAF : new DecoratedChildren(original, filter));
        this.htmlDecorator = newProperty(HTML_DECORATOR_PROPERTY, (o, n) -> n != null ? n : Html.DEFAULT, null);
        this.preferredActionDecorator = newProperty(PREFERRED_ACTION_DECORATOR_PROPERTY, (o, n) -> n != null ? n : PreferredAction.DEFAULT, null);
    }

    @Override
    public Node getOriginal() {
        return super.getOriginal();
    }

    public void setHtmlDecorator(Function<Node, String> decorator) {
        this.htmlDecorator.set(decorator);
        fireDisplayNameChange(null, getHtmlDisplayName());
    }

    public void setPreferredActionDecorator(Function<Node, Action> decorator) {
        this.preferredActionDecorator.set(decorator);
    }

    @Override
    public String getHtmlDisplayName() {
        return htmlDecorator.get().apply(getOriginal());
    }

    @Override
    public Action getPreferredAction() {
        return preferredActionDecorator.get().apply(getOriginal());
    }

    public FluentIterable<DecoratedNode> breadthFirstIterable() {
        return Nodes.breadthFirstIterable(this).filter(DecoratedNode.class);
    }

    protected final <T> JProperty<T> newProperty(String name, JProperty.Setter<T> setter, T initialValue) {
        return new JProperty<T>(name, setter, setter.apply(null, initialValue)) {
            @Override
            protected void firePropertyChange(T oldValue, T newValue) {
                DecoratedNode.this.firePropertyChange(getName(), oldValue, newValue);
            }
        };
    }

    private static class DecoratedChildren extends FilterNode.Children {

        private final Predicate<Node> filter;

        public DecoratedChildren(Node owner, Predicate<Node> filter) {
            super(owner);
            this.filter = filter;
        }

        @Override
        protected Node copyNode(Node original) {
            return new DecoratedNode(original, filter);
        }

        @Override
        protected Node[] createNodes(Node key) {
            List<Node> result = new ArrayList<>();
            for (Node o : super.createNodes(key)) {
                if (filter.test(((DecoratedNode) o).getOriginal())) {
                    result.add(o);
                }
            }
            return Iterables.toArray(result, Node.class);
        }
    }

    public enum Html implements Function<Node, String> {

        DEFAULT {
            @Override
            public String apply(Node input) {
                return input.getHtmlDisplayName();
            }
        },
        BOLD {
            @Override
            public String apply(Node node) {
                return "<b>" + node.getDisplayName() + "</b>";
            }
        };
    }

    public enum PreferredAction implements Function<Node, Action> {

        DEFAULT {
            @Override
            public Action apply(Node input) {
                return input.getPreferredAction();
            }
        },
        DO_NOTHING {
            @Override
            public Action apply(Node input) {
                return doNothing;
            }
            final Action doNothing = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };
        }
    }

    public static final Function<DecoratedNode, Node> TO_ORIGINAL = o -> o.getOriginal();
}
