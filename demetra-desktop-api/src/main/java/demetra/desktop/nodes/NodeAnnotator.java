package demetra.desktop.nodes;

import demetra.desktop.design.GlobalService;
import demetra.desktop.util.CollectionSupplier;
import demetra.desktop.util.LazyGlobalService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.nodes.Node;

import java.awt.*;

@GlobalService
public final class NodeAnnotator {

    @NonNull
    public static NodeAnnotator getDefault() {
        return LazyGlobalService.get(NodeAnnotator.class, NodeAnnotator::new);
    }

    private NodeAnnotator() {
    }

    private final CollectionSupplier<NodeAnnotatorSpi> providers = NodeAnnotatorSpiLoader::get;

    public Image annotateIcon(Node node, Image image) {
        Image result = image;
        for (NodeAnnotatorSpi o : providers.get()) {
            result = o.annotateIcon(node, result);
        }
        return result;
    }

    public String annotateName(Node node, String name) {
        String result = name;
        for (NodeAnnotatorSpi o : providers.get()) {
            result = o.annotateName(node, result);
        }
        return result;
    }
}
