/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.output;

import ec.nbdemetra.ui.DemetraUiIcon;
import ec.tss.sa.ISaOutputFactory;
import java.awt.Image;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Jean Palate
 */
public abstract class AbstractOutputNode<T> extends AbstractNode {

    public AbstractOutputNode(T config) {
        super(Children.LEAF, Lookups.singleton(config));
    }
    
    public abstract ISaOutputFactory getFactory();
 
    @Override
    public Image getIcon(int type) {
        return DemetraUiIcon.DOCUMENT_PRINT_16.getImageIcon().getImage();
    }
    
}
