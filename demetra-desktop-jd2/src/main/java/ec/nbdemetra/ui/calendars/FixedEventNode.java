/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import demetra.ui.properties.NodePropertySetBuilder;
import ec.tstoolkit.timeseries.Month;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
public class FixedEventNode extends AbstractEventNode {

    public FixedEventNode(FixedEventBean bean) {
        super(bean);
    }

    @Override
    public String getHtmlDisplayName() {
        FixedEventBean bean = getLookup().lookup(FixedEventBean.class);
        StringBuilder sb = new StringBuilder();
        sb.append("<b>Fixed</b> ");
        sb.append(bean.getMonth()).append(", ").append(bean.getDay());
        return sb.toString();
    }

    @Override
    protected Sheet createSheet() {
        FixedEventBean bean = getLookup().lookup(FixedEventBean.class);
        Sheet result = super.createSheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Fixed Day");
        b.withInt().select(bean, FixedEventBean.DAY_PROPERTY).min(1).max(31).display("Day").add();
        b.withEnum(Month.class).select(bean, FixedEventBean.MONTH_PROPERTY).display("Month").add();
        result.put(b.build());
        return result;
    }
}
