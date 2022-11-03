/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.processing.ui.sa;

import demetra.desktop.ui.processing.ItemUI;
import javax.swing.JComponent;
import jdplus.sa.SaBenchmarkingResults;

/**
 *
 * @author palatej
 */
public class BenchmarkingUI implements ItemUI<BenchmarkingUI.Input>{

    @lombok.Value
    public static class Input{
        boolean mul;
        SaBenchmarkingResults benchmarking;
    }

    @Override
    public JComponent getView(Input info) {
        JBenchmarkingView view=new JBenchmarkingView();
        view.set(info.benchmarking.getBenchmarkedSa(), info.benchmarking.getSa(), info.mul);
        return view;
    }
    

    
}
