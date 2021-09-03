/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import demetra.desktop.properties.NodePropertySetBuilder;
import java.text.DecimalFormat;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
public class EasterRelatedEventNode extends AbstractEventNode {

    public EasterRelatedEventNode(EasterRelatedEventBean bean) {
        super(bean);
    }

    @Override
    public String getHtmlDisplayName() {
        EasterRelatedEventBean bean = getLookup().lookup(EasterRelatedEventBean.class);
        StringBuilder sb = new StringBuilder();
        sb.append("<b>Easter</b> ");
        DecimalFormat df = new DecimalFormat("+#;-#");
        sb.append(df.format(bean.getOffset()));
        return sb.toString();
    }

    @Override
    protected Sheet createSheet() {
        EasterRelatedEventBean bean = getLookup().lookup(EasterRelatedEventBean.class);
        Sheet result = super.createSheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Easter Related Day");
        b.withInt().select(bean, EasterRelatedEventBean.OFFSET_PROPERTY).min(-366).max(366).display("Offset").add();
        result.put(b.build());
        return result;
    }
}
