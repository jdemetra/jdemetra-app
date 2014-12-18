/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.nodes;

import com.google.common.collect.Iterables;
import ec.tstoolkit.design.IBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
public class AbstractNodeBuilder implements IBuilder<AbstractNode> {

    final List<Node> nodes;
    String name;
    boolean orderable;
    Sheet sheet;

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
        Iterables.addAll(this.nodes, nodes);
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

    @Override
    public AbstractNode build() {
        Children children = nodes.isEmpty() ? Children.LEAF : (orderable ? new Index.ArrayChildren() : new Children.Array());
        children.add(Iterables.toArray(nodes, Node.class));
        CustomNode result = new CustomNode(children, sheet);
        if (name != null) {
            result.setName(name);
        }
        return result;
    }

    static class CustomNode extends AbstractNode {

        final Sheet sheet;

        CustomNode(Children children, Sheet sheet) {
            super(children);
            this.sheet = sheet;
        }

        @Override
        protected Sheet createSheet() {
            return sheet != null ? sheet : super.createSheet();
        }
    }
}
