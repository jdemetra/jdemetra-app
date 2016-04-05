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

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import ec.tstoolkit.design.UtilityClass;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import javax.annotation.Nonnull;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 * Convenient methods to deal with nodes.
 *
 * @author Philippe Charles
 */
@UtilityClass(Node.class)
public final class Nodes {

    private Nodes() {
        // static class
    }

    @Nonnull
    public static Action[] actionsForPath(@Nonnull String path) {
        return Iterables.toArray(Utilities.actionsForPath(path), Action.class);
    }

    @Nonnull
    public static FluentIterable<Node> childrenIterable(@Nonnull Node root) {
        return FluentIterable.from(root.isLeaf() ? Collections.emptyList() : Arrays.asList(root.getChildren().getNodes()));
    }

    @Nonnull
    public static FluentIterable<Node> breadthFirstIterable(@Nonnull Node root) {
        return FluentIterable.from(root.isLeaf() ? Collections.singleton(root) : new BreadthFirstIterable(root));
    }

    @Nonnull
    public static FluentIterable<Node> depthFirstIterable(@Nonnull Node root) {
        return FluentIterable.from(root.isLeaf() ? Collections.singleton(root) : new DepthFirstIterable(root));
    }

    @Nonnull
    public static FluentIterable<Node> asIterable(@Nonnull Node[] nodes) {
        return FluentIterable.from(Arrays.asList(nodes));
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static class BreadthFirstIterable implements Iterable<Node> {

        private final Node root;

        public BreadthFirstIterable(Node root) {
            this.root = root;
        }

        @Override
        public Iterator<Node> iterator() {
            final Deque<Node> queue = new LinkedList<>();
            queue.add(root);
            return new UnmodifiableIterator<Node>() {
                @Override
                public boolean hasNext() {
                    return !queue.isEmpty();
                }

                @Override
                public Node next() {
                    Node result = queue.removeFirst();
                    if (!result.isLeaf()) {
                        Collections.addAll(queue, result.getChildren().getNodes());
                    }
                    return result;
                }
            };
        }
    }

    private static class DepthFirstIterable implements Iterable<Node> {

        private final Node root;

        public DepthFirstIterable(Node root) {
            this.root = root;
        }

        @Override
        public Iterator<Node> iterator() {
            final Stack<Iterator<Node>> stack = new Stack<>();
            stack.push(Iterators.singletonIterator(root));
            return new UnmodifiableIterator<Node>() {
                @Override
                public boolean hasNext() {
                    return !stack.isEmpty() && stack.peek().hasNext();
                }

                @Override
                public Node next() {
                    Iterator<Node> top = stack.peek();
                    Node result = top.next();
                    if (!top.hasNext()) {
                        stack.pop();
                    }
                    if (!result.isLeaf()) {
                        stack.push(Iterators.forEnumeration(result.getChildren().nodes()));
                    }
                    return result;
                }
            };
        }
    }
    //</editor-fold>
}
