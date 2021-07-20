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

import ec.tstoolkit.utilities.Trees;
import java.util.Arrays;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 * Convenient methods to deal with nodes.
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class Nodes {

    @NonNull
    public Action[] actionsForPath(@NonNull String path) {
        return Utilities.actionsForPath(path).stream().toArray(Action[]::new);
    }

    @NonNull
    public Stream<Node> childrenStream(@NonNull Node root) {
        return !root.isLeaf()
                ? Arrays.stream(root.getChildren().getNodes())
                : Stream.empty();
    }

    @NonNull
    public Stream<Node> breadthFirstStream(@NonNull Node root) {
        return Trees.breadthFirstStream(root, Nodes::childrenStream);
    }

    @NonNull
    public Stream<Node> depthFirstStream(@NonNull Node root) {
        return Trees.depthFirstStream(root, Nodes::childrenStream);
    }
}
