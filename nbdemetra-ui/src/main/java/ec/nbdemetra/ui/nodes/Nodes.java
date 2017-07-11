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
import ec.tstoolkit.design.UtilityClass;
import ec.tstoolkit.utilities.Trees;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;
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
    public static Stream<Node> childrenStream(@Nonnull Node root) {
        return !root.isLeaf()
                ? Arrays.stream(root.getChildren().getNodes())
                : Stream.empty();
    }

    @Nonnull
    public static FluentIterable<Node> childrenIterable(@Nonnull Node root) {
        return FluentIterable.from(root.isLeaf() ? Collections.emptyList() : Arrays.asList(root.getChildren().getNodes()));
    }

    @Nonnull
    public static FluentIterable<Node> breadthFirstIterable(@Nonnull Node root) {
        return FluentIterable.from(root.isLeaf() ? Collections.singleton(root) : Trees.breadthFirstIterable(root, Nodes::childrenStream));
    }

    @Nonnull
    public static FluentIterable<Node> depthFirstIterable(@Nonnull Node root) {
        return FluentIterable.from(root.isLeaf() ? Collections.singleton(root) : Trees.depthFirstIterable(root, Nodes::childrenStream));
    }

    @Nonnull
    public static FluentIterable<Node> asIterable(@Nonnull Node[] nodes) {
        return FluentIterable.from(Arrays.asList(nodes));
    }
}
