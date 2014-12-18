/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tstoolkit.timeseries.DayOfWeek;
import ec.tstoolkit.timeseries.Month;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
public class FixedWeekEventNode extends AbstractEventNode {

    public FixedWeekEventNode(FixedWeekEventBean bean) {
        super(bean);
    }

    @Override
    public String getHtmlDisplayName() {
        FixedWeekEventBean bean = getLookup().lookup(FixedWeekEventBean.class);
        StringBuilder sb = new StringBuilder();
        sb.append("<b>Fixed Week</b> ");
        sb.append(bean.dayOfWeek).append(", ").append(bean.month).append(", ").append(bean.week);
        return sb.toString();
    }

    @Override
    protected Sheet createSheet() {
        FixedWeekEventBean bean = getLookup().lookup(FixedWeekEventBean.class);
        Sheet result = super.createSheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Fixed Week Day");
        b.withEnum(DayOfWeek.class).select(bean, FixedWeekEventBean.DAY_OF_WEEK_PROPERTY).display("Day Of Week").add();
        b.withEnum(Month.class).select(bean, FixedWeekEventBean.MONTH_PROPERTY).display("Month").add();
        b.withInt().select(bean, FixedWeekEventBean.WEEK_PROPERTY).min(1).max(5).display("Week").add();
        result.put(b.build());
        return result;
    }
}
