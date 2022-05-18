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
import java.time.Month;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Philippe Charles
 */
public class SingleDateNode extends AbstractNode implements PropertyChangeListener {

    public SingleDateNode(SingleDateBean bean) {
        super(Children.LEAF, Lookups.singleton(bean));
        setName(bean.getClass().getSimpleName());
        bean.addWeakPropertyChangeListener(this);
    }

    @Override
    public String getHtmlDisplayName() {
        SingleDateBean bean = getLookup().lookup(SingleDateBean.class);
        StringBuilder sb = new StringBuilder();
        sb.append("<b>Fixed</b> ");
        sb.append(bean.getDate());
        return sb.toString();
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
        SingleDateBean bean = getLookup().lookup(SingleDateBean.class);
        Sheet result = super.createSheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Single Date");
        b.with(LocalDate.class).select(bean, SingleDateBean.DATE_PROPERTY).display("Date").add();
        b.withDouble().select(bean, AbstractEventBean.WEIGHT_PROPERTY).min(0).max(1).display("Weight").add();
        result.put(b.build());
        return result;
    }
}
