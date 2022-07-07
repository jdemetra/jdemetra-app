/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.ui.processing.stats;

import demetra.desktop.components.tools.JStabilityView;
import demetra.desktop.ui.processing.ItemUI;
import demetra.information.Explorable;
import demetra.timeseries.TsDomain;
import demetra.timeseries.regression.RegressionItem;
import demetra.toolkit.dictionaries.Dictionary;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import javax.swing.JComponent;
import jdplus.timeseries.simplets.analysis.MovingProcessing;

/**
 *
 * @author PALATEJ
 */
public class StabilityUI implements ItemUI<StabilityUI.Information> {

    @Override
    public JComponent getView(Information info) {
        JStabilityView view = new JStabilityView();
        boolean empty = true;
        MovingProcessing<Explorable> processing = info.getMovingProcessing();
        for (String item : info.getIds()) {
            Map<TsDomain, Double> movingInfo = processing.movingInfo(x
                    -> {
                RegressionItem reg=x.getData(item, RegressionItem.class);
                return reg == null ? Double.NaN : reg.getCoefficient();
            });
            if (isDefined(movingInfo)) {
                empty = false;
                int sep = item.lastIndexOf(Dictionary.SEP);
                view.add(sep > 0 ? item.substring(sep + 1) : item, movingInfo, null, false);
            }
        }
        if (empty) {
            view.showException(info.getExceptionMessage());

        } else {
            view.display();
        }

        return view;
    }

    @lombok.Value
    public static class Information {

        MovingProcessing<Explorable> movingProcessing;
        String[] Ids;
        String exceptionMessage;
    }

    private boolean isDefined(Map<TsDomain, Double> data) {
        if (data == null || data.isEmpty()) {
            return false;
        }

        for (Map.Entry<TsDomain, Double> d : data.entrySet()) {
            if (Double.isFinite(d.getValue())) {
                return true;
            }
        }
        return false;
    }

}
