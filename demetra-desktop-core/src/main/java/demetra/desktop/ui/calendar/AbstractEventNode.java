/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.calendar;

import demetra.desktop.DemetraIcons;
import demetra.desktop.properties.NodePropertySetBuilder;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.Date;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Philippe Charles
 */
public class AbstractEventNode extends AbstractNode implements PropertyChangeListener {

    public AbstractEventNode(AbstractEventBean bean) {
        super(Children.LEAF, Lookups.singleton(bean));
        setName(bean.getClass().getSimpleName());
        bean.addWeakPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        fireDisplayNameChange(null, getDisplayName());
    }

    @Override
    public Image getIcon(int type) {
        return DemetraIcons.CALENDAR_16.getImageIcon().getImage();
    }

    @Override
    protected Sheet createSheet() {
        AbstractEventBean bean = getLookup().lookup(AbstractEventBean.class);
        Sheet result = super.createSheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Event");
        b.withDouble().select(bean, AbstractEventBean.WEIGHT_PROPERTY).min(0).max(1).display("Weight").add();
        b.with(LocalDate.class).select(bean, AbstractEventBean.START_PROPERTY).display("Start").add();
        b.with(LocalDate.class).select(bean, AbstractEventBean.END_PROPERTY).display("End").add();
        result.put(b.build());
        return result;
    }
}
