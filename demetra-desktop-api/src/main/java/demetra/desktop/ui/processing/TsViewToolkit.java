/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.timeseries.TsCollection;
import demetra.desktop.components.JHtmlView;
import demetra.desktop.components.JTsChart;
import demetra.desktop.components.JTsGrid;
import demetra.desktop.components.parts.HasTsCollection;
import demetra.html.HtmlElement;
import demetra.html.HtmlUtil;
import demetra.timeseries.Ts;
import demetra.util.Collections2;

import javax.swing.*;

/**
 * @author Jean Palate
 */
@lombok.experimental.UtilityClass
public class TsViewToolkit {

    public static JTsGrid getGrid(Iterable<Ts> series) {
        TsCollection all = Collections2.streamOf(series).collect(TsCollection.toTsCollection());
        JTsGrid result = new JTsGrid();
        result.setTsUpdateMode(HasTsCollection.TsUpdateMode.None);
        result.setTsCollection(all);
        boolean single = all.size() == 1 && all.get(0).getData().getAnnualFrequency()>0;
        result.setMode(single ? JTsGrid.Mode.SINGLETS : JTsGrid.Mode.MULTIPLETS);
        return result;
    }

    public static JTsChart getChart(Iterable<Ts> series) {
        JTsChart result = new JTsChart();
        result.setTsUpdateMode(HasTsCollection.TsUpdateMode.None);
        result.setTsCollection(Collections2.streamOf(series).collect(TsCollection.toTsCollection()));
        return result;
    }

    public static JHtmlView getHtmlViewer(HtmlElement html) {
        JHtmlView result = new JHtmlView();
        result.setHtml(HtmlUtil.toString(html));
        return result;
    }

    public static JLabel getMessageViewer(String msg) {
        JLabel result = new JLabel();
        result.setHorizontalAlignment(SwingConstants.CENTER);
        result.setFont(result.getFont().deriveFont(result.getFont().getSize2D() * 3 / 2));
        result.setText("<html><center>" + msg);
        return result;
    }
}
