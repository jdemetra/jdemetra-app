/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.util;

import demetra.desktop.DemetraIcons;
import demetra.util.Id;
import demetra.util.LinearId;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Jean Palate
 */
public final class IdNodes {

    private IdNodes() {
        // static class
    }

    public static class IdIcon implements Icon {

        final Id id;

        public IdIcon(Id id) {
            this.id = id;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.drawImage(DemetraIcons.DOCUMENT_16.getImageIcon().getImage(), x, y, null);
            String tail = id.tail();
            if (tail != null && !tail.isEmpty()) {
                Graphics2D g2 = (Graphics2D) g;
                Font old = c.getFont();
                Color oldColor = c.getForeground();
                g.setFont(c.getFont().deriveFont(7f));
                g.setColor(Color.DARK_GRAY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawCenteredString(Character.toString(tail.charAt(0)), x + 17, y + 20, g);
                g.setColor(oldColor);
                g.setFont(old);
            }
        }

        public void drawCenteredString(String s, int w, int h, Graphics g) {
            FontMetrics fm = g.getFontMetrics();
            int x = (w - fm.stringWidth(s)) / 2;
            int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2);
            g.drawString(s, x, y);
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }
    final static INodeFactory<Id> FACTORY = (Children children, Id id) -> {
        AbstractNode result = new AbstractNode(children, Lookups.singleton(id)) {
            
            final IdIcon icon = new IdIcon(id);
            
            @Override
            public Image getIcon(int type) {
                return ImageUtilities.icon2Image(icon);
            }
            
            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }
        };
        result.setName(id.tail());
        return result;
    };

    public static Node getRootNode2(List<Id> list) {
        Node root = FACTORY.create(new Children.Array(), new LinearId());
        Map<Id, Node> index = new HashMap<>();
        for (Id id : list) {
            Node prev = root;
            for (Id pathItem : id.path()) {
                Node cur = index.get(pathItem);
                if (cur == null) {
                    cur = FACTORY.create(new Children.Array(), pathItem);
                    prev.getChildren().add(new Node[]{cur});
                    index.put(pathItem, cur);
                }
                prev = cur;
            }
        }
        return root;
    }

    public static Node getRootNode(List<Id> list) {
        NodeBuilder root = new NodeBuilder(FACTORY, new LinearId());
        Map<Id, NodeBuilder> index = new HashMap<>();
        for (Id id : list) {
            NodeBuilder prev = root;
            for (Id pathItem : id.path()) {
                NodeBuilder cur = index.get(pathItem);
                if (cur == null) {
                    cur = prev.addAndGet(pathItem);
                    index.put(pathItem, cur);
                }
                prev = cur;
            }
        }
        return root.build();
    }

    interface INodeFactory<T> {

        Node create(Children children, T id);
    }

    static class NodeBuilder<T>  {

        private final T id;
        private final INodeFactory<T> factory;
        private final List<NodeBuilder> children;

        NodeBuilder(INodeFactory<T> factory, T id) {
            this.id = id;
            this.factory = factory;
            this.children = new ArrayList<>();
        }

        public NodeBuilder<T> addAndGet(T id) {
            NodeBuilder result = new NodeBuilder(factory, id);
            children.add(result);
            return result;
        }

        public Node build() {
            if (children.isEmpty()) {
                return factory.create(Children.LEAF, id);
            }
            Node[] nodes = new Node[children.size()];
            for (int i = 0; i < nodes.length; i++) {
                nodes[i] = children.get(i).build();
            }
            Children result = new Children.Array();
            result.add(nodes);
            return factory.create(result, id);
        }
    }

    public static Node findNode(Node root, Id id) {
        Node node = root;
        for (int i = 0; i < id.getCount(); i++) {
            node = node.getChildren().findChild(id.get(i));
            if (node == null) {
                return null;
            }
        }
        return node;
    }
}
