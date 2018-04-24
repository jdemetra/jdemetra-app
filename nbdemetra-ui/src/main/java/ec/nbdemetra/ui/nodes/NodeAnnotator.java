/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.nodes;

import ec.nbdemetra.core.GlobalService;
import ec.tstoolkit.design.ServiceDefinition;
import java.awt.Image;
import javax.annotation.Nonnull;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceDefinition
public interface NodeAnnotator {

    Image annotateIcon(Node node, Image image);

    String annotateName(Node node, String name);

    @GlobalService
    @ServiceProvider(service = Support.class)
    public static class Support {

        @Nonnull
        public static Support getDefault() {
            return Lookup.getDefault().lookup(Support.class);
        }

        public Image annotateIcon(Node node, Image image) {
            Image result = image;
            for (NodeAnnotator o : Lookup.getDefault().lookupAll(NodeAnnotator.class)) {
                result = o.annotateIcon(node, result);
            }
            return result;
        }

        public String annotateName(Node node, String name) {
            String result = name;
            for (NodeAnnotator o : Lookup.getDefault().lookupAll(NodeAnnotator.class)) {
                result = o.annotateName(node, result);
            }
            return result;
        }
    }
}
