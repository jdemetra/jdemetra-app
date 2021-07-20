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
import com.google.common.collect.Iterables;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
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
    protected Function<Node, String> htmlDecorator;
    protected Function<Node, Action> preferredActionDecorator;

    public DecoratedNode(Node original) {
        this(original, o -> true);
    }

    public DecoratedNode(Node original, Predicate<Node> filter) {
        super(original, original.isLeaf() ? Children.LEAF : new DecoratedChildren(original, filter));
        this.htmlDecorator = Html.DEFAULT;
        this.preferredActionDecorator = PreferredAction.DEFAULT;
    }

    @Override
    public Node getOriginal() {
        return super.getOriginal();
    }

    public void setHtmlDecorator(Function<Node, String> decorator) {
        Function<Node, String> old = this.htmlDecorator;
        this.htmlDecorator = decorator != null ? decorator : Html.DEFAULT;
        firePropertyChange(HTML_DECORATOR_PROPERTY, old, this.htmlDecorator);
        fireDisplayNameChange(null, getHtmlDisplayName());
    }

    public void setPreferredActionDecorator(Function<Node, Action> decorator) {
        Function<Node, Action> old = this.preferredActionDecorator;
        this.preferredActionDecorator = decorator != null ? decorator : PreferredAction.DEFAULT;
        firePropertyChange(PREFERRED_ACTION_DECORATOR_PROPERTY, old, this.preferredActionDecorator);
    }

    @Override
    public String getHtmlDisplayName() {
        return htmlDecorator.apply(getOriginal());
    }

    @Override
    public Action getPreferredAction() {
        return preferredActionDecorator.apply(getOriginal());
    }

    public Stream<DecoratedNode> breadthFirstStream() {
        return Nodes.breadthFirstStream(this).filter(DecoratedNode.class::isInstance).map(DecoratedNode.class::cast);
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
