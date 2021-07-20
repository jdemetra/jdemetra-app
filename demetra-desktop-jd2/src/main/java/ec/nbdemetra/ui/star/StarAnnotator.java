/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.star;

import demetra.tsprovider.DataSource;
import ec.nbdemetra.ui.nodes.NodeAnnotator;
import java.awt.Image;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = NodeAnnotator.class)
public class StarAnnotator implements NodeAnnotator {

    boolean isStarred(Node node) {
        DataSource dataSource = node.getLookup().lookup(DataSource.class);
        return dataSource != null && StarList.getInstance().isStarred(dataSource);
    }

    @Override
    public Image annotateIcon(Node node, Image image) {
        if (isStarred(node)) {
            Image badge = ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/bullet_star.png", false);
            return ImageUtilities.mergeImages(image, badge, 10, 0);

//            String ressource = !starred ? "ec/nbdemetra/ui/nodes/star-empty.png" : "ec/nbdemetra/ui/nodes/star.png";
//            return ImageUtilities.loadImage(ressource, true);
        }
        return image;
    }

    @Override
    public String annotateName(Node node, String name) {
        return name;
    }
}
