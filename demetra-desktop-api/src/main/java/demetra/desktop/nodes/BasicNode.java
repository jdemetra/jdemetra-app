/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.nodes;

import demetra.desktop.DemetraIcons;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

import javax.swing.*;
import java.awt.*;

/**
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
            Image badge = DemetraIcons.EXCLAMATION_MARK_SMALL_16.getImageIcon().getImage();
            return ImageUtilities.mergeImages(image, badge, 0, 0);
        }
        return image;
    }

    protected Image annotate(Image image) {
        Image result = image;
        return NodeAnnotatorManager.get().annotateIcon(this, result);
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
        return NodeAnnotatorManager.get().annotateName(this, result);
    }

    public void refreshAnnotation() {
        fireIconChange();
        fireOpenedIconChange();
    }
}
