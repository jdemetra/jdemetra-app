/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package demetra.ui.nodes;

import nbbrd.design.BuilderPattern;
import demetra.ui.util.Collections2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
@BuilderPattern(AbstractNode.class)
public final class AbstractNodeBuilder {

    private final List<Node> nodes;
    private String name;
    private boolean orderable;
    private Sheet sheet;

    public AbstractNodeBuilder() {
        nodes = new ArrayList<>();
        this.name = null;
        this.orderable = true;
        this.sheet = null;
    }

    public AbstractNodeBuilder add(Node node) {
        this.nodes.add(node);
        return this;
    }

    public AbstractNodeBuilder add(Iterable<? extends Node> nodes) {
        Collections2.addAll(this.nodes, nodes);
        return this;
    }

    public AbstractNodeBuilder add(Stream<? extends Node> nodes) {
        nodes.forEach(this.nodes::add);
        return this;
    }

    public AbstractNodeBuilder add(Node[] nodes) {
        return add(Arrays.asList(nodes));
    }

    public AbstractNodeBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AbstractNodeBuilder orderable(boolean orderable) {
        this.orderable = orderable;
        return this;
    }

    public AbstractNodeBuilder sheet(Sheet sheet) {
        this.sheet = sheet;
        return this;
    }

    public AbstractNode build() {
        Children children = nodes.isEmpty() ? Children.LEAF : (orderable ? new Index.ArrayChildren() : new Children.Array());
        children.add(Collections2.toArray(nodes, Node.class));
        CustomNode result = new CustomNode(children, sheet);
        if (name != null) {
            result.setName(name);
        }
        return result;
    }

    private static final class CustomNode extends AbstractNode {

        private final Sheet sheet;

        private CustomNode(Children children, Sheet sheet) {
            super(children);
            this.sheet = sheet;
        }

        @Override
        protected Sheet createSheet() {
            return sheet != null ? sheet : super.createSheet();
        }
    }
}
