/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.core.star;

import demetra.desktop.star.StarList;
import demetra.tsprovider.DataSource;
import java.awt.Image;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;
import demetra.ui.nodes.NodeAnnotatorSpi;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = NodeAnnotatorSpi.class)
public class StarAnnotator implements NodeAnnotatorSpi {

    boolean isStarred(Node node) {
        DataSource dataSource = node.getLookup().lookup(DataSource.class);
        return dataSource != null && StarList.getDefault().isStarred(dataSource);
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
