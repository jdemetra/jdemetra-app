/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.nodes;

import demetra.ui.util.NetBeansServiceBackend;
import java.awt.Image;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.nodes.Node;

/**
 *
 * @author Philippe Charles
 */
@ServiceDefinition(
        quantifier = Quantifier.MULTIPLE,
        backend = NetBeansServiceBackend.class,
        singleton = true
)
public interface NodeAnnotatorSpi {

    @NonNull
    Image annotateIcon(@NonNull Node node, @NonNull Image image);

    @NonNull
    String annotateName(@NonNull Node node, @NonNull String name);
}
