/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import demetra.desktop.DemetraIcons;
import demetra.desktop.properties.NodePropertySetBuilder;
import ec.tstoolkit.timeseries.Day;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
        b.with(Day.class).select(bean, AbstractEventBean.START_PROPERTY).display("Start").add();
        b.with(Day.class).select(bean, AbstractEventBean.END_PROPERTY).display("End").add();
        b.withDouble().select(bean, AbstractEventBean.WEIGHT_PROPERTY).min(0).max(1).display("Weight").add();
        result.put(b.build());
        return result;
    }
}