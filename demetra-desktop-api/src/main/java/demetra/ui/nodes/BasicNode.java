/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.ui.nodes;

import demetra.ui.nodes.NodeAnnotator;
import java.awt.Image;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Philippe Charles
 */
public abstract class BasicNode<LTYPE> extends AbstractNode {

    protected final BasicChildFactory<?> factory;
    protected final Class<LTYPE> clazz;
    protected final String actionPath;

    public BasicNode(BasicChildFactory<?> factory, LTYPE objectToLookup, String actionPath) {
        super(factory != null ? Children.create(factory, true) : Children.LEAF, Lookups.singleton(objectToLookup));
        this.factory = factory;
        this.clazz = (Class<LTYPE>) objectToLookup.getClass();
        this.actionPath = actionPath;
    }

    public BasicNode(Children children, LTYPE objectToLookup, String actionPath) {
        super(children, Lookups.singleton(objectToLookup));
        this.factory = null;
        this.clazz = (Class<LTYPE>) objectToLookup.getClass();
        this.actionPath = actionPath;
    }

    public BasicChildFactory<?> getFactory() {
        return factory;
    }

    public LTYPE lookup() {
        return getLookup().lookup(clazz);
    }

    @Override
    public Action[] getActions(boolean context) {
        return Nodes.actionsForPath(actionPath);
    }

    protected Image mergeExceptionBadge(Image image) {
        if (factory != null && factory.getException() != null) {
            Image badge = ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/exclamation-small-red.png", false);
            return ImageUtilities.mergeImages(image, badge, 0, 0);
        }
        return image;
    }

    protected Image annotate(Image image) {
        Image result = image;
        return NodeAnnotator.getDefault().annotateIcon(this, result);
    }

    @Override
    public Image getIcon(int type) {
        return annotate(super.getIcon(type));
    }

    @Override
    public Image getOpenedIcon(int type) {
        return annotate(super.getOpenedIcon(type));
    }

    @Override
    public String getDisplayName() {
        String result = super.getDisplayName();
        return NodeAnnotator.getDefault().annotateName(this, result);
    }

    public void refreshAnnotation() {
        fireIconChange();
        fireOpenedIconChange();
    }
}
