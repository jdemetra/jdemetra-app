/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.ui.processing.stats;

import demetra.desktop.ui.JSlidingSpansView;
import demetra.desktop.ui.processing.ItemUI;
import demetra.information.Explorable;
import demetra.timeseries.TsData;
import java.util.function.Function;
import javax.swing.JComponent;
import jdplus.stats.DescriptiveStatistics;
import jdplus.timeseries.simplets.analysis.DiagnosticInfo;
import jdplus.timeseries.simplets.analysis.SlidingSpans;

/**
 *
 * @author Mats Maggi
 */
public class SlidingSpansUI<I> implements ItemUI<SlidingSpansUI.Information<I>> {

    public SlidingSpansUI() {
    }

    @Override
    public JComponent getView(Information<I> input) {
        JSlidingSpansView view = new JSlidingSpansView();
        view.setInfo(input.getDiag());
        view.setInfoName(input.getInfo());
        view.setExtractor(input.getExtractor());
        view.setMultiplicative(input.isMultiplicative());
        if (input.isMultiplicative()) {
            view.setThreshold(0.03);
        } else {
            TsData s = input.getExtractor().apply(input.getSlidingSpans().getReferenceInfo());
            if (s != null) {
                DescriptiveStatistics stats = DescriptiveStatistics.of(s.getValues());
                view.setThreshold(Math.sqrt(stats.getSumSquare() / stats.getDataCount()));
            }
        }
        view.setSlidingSpans(input.getSlidingSpans());
        return view;
    }

    @lombok.Value
    public static class Information<I> {

        private boolean multiplicative;
        private SlidingSpans<I> slidingSpans;
        private DiagnosticInfo diag;
        private String info;
        private Function<I, TsData> extractor;

    }

}
