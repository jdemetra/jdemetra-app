/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.ns;

import java.awt.Image;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
public interface INamedService {

    String getName();

    String getDisplayName();

    Image getIcon(int type, boolean opened);

    Sheet createSheet();
}
