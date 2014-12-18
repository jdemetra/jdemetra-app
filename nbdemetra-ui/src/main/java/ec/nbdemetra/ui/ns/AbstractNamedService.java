/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.ns;

import ec.nbdemetra.ui.DemetraUiIcon;
import java.awt.Image;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Philippe Charles
 */
public abstract class AbstractNamedService implements INamedService {

    protected final NamedServiceSupport support;

    protected AbstractNamedService(Class<? extends INamedService> service, String name) {
        this.support = new NamedServiceSupport(service, name);
    }

    @Override
    public String getName() {
        return support.getName();
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    @Override
    public Sheet createSheet() {
        return new Sheet();
    }
}
