/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tstoolkit.timeseries.calendars.DayEvent;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
public class SpecialEventNode extends AbstractEventNode {

    public SpecialEventNode(SpecialEventBean bean) {
        super(bean);
    }

    @Override
    public String getHtmlDisplayName() {
        SpecialEventBean bean = getLookup().lookup(SpecialEventBean.class);
        StringBuilder sb = new StringBuilder();
        sb.append("<b>Special Day</b> ");
        sb.append(bean.dayEvent).append(", ").append(bean.offset);
        return sb.toString();
    }

    @Override
    protected Sheet createSheet() {
        SpecialEventBean bean = getLookup().lookup(SpecialEventBean.class);
        Sheet result = super.createSheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Special Day");
        b.withEnum(DayEvent.class).select(bean, SpecialEventBean.DAY_EVENT_PROPERTY).display("Day event").add();
        b.withInt().select(bean, SpecialEventBean.OFFSET_PROPERTY).display("Offset").add();
        result.put(b.build());
        return result;
    }
}
