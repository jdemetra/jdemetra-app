/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view.tsprocessing;

import ec.satoolkit.DecompositionMode;
import ec.tstoolkit.modelling.ModellingDictionary;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.analysis.DiagnosticInfo;
import ec.tstoolkit.timeseries.analysis.SlidingSpans;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.view.SlidingSpanView;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class SlidingSpansDetailUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, SlidingSpans> {

    private String info_;

    public SlidingSpansDetailUI(String info){
        info_=info;
    }

    @Override
    public JComponent getView(V host, SlidingSpans information) {
        SlidingSpanView view = new SlidingSpanView();
        view.setTsToolkit(host.getToolkit());
        if (information != null){
            double threshold = 0.03;
            DecompositionMode mode = information.getReferenceInfo().getData(ModellingDictionary.MODE, DecompositionMode.class);
            boolean mul = mode == DecompositionMode.Multiplicative;
            if (!mul) {
                TsData s = information.getReferenceInfo().getData(info_, TsData.class);
                if (s != null) {
                    DescriptiveStatistics stats = new DescriptiveStatistics(s);
                    threshold = Math.sqrt(stats.getSumSquare() / stats.getDataCount());
                }
            }
            view.setThreshold(threshold);

            if (info_.equals(ModellingDictionary.SA)) {
                if (mul) {
                    view.setInfo(DiagnosticInfo.PeriodToPeriodGrowthDifference);
                }
                else {
                    view.setInfo(DiagnosticInfo.PeriodToPeriodDifference);
                }
            }
            else {
                if (mul) {
                    view.setInfo(DiagnosticInfo.RelativeDifference);
                }
                else {
                    view.setInfo(DiagnosticInfo.AbsoluteDifference);
                }
            }

            view.setInfoName(info_);
            view.setSlidingSpans(information);
        }
        else {
            view.setSlidingSpans(null);
        }
        return view;
    }
}
