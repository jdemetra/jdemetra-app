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
package demetra.desktop.benchmarking.ui;

import demetra.util.Id;
import demetra.util.LinearId;

/**
 *
 * @author palatej
 */
@lombok.experimental.UtilityClass
class BenchmarkingViewFactory {
 
    public final String INPUT = "Input", RESULTS = "Results", BIRATIO="BI-ratio", DATA="Data";
    public final Id INPUT_DATA = new LinearId(INPUT, DATA);
    public final Id INPUT_BI = new LinearId(INPUT, BIRATIO);
    public final Id RESULTS_MAIN = new LinearId(RESULTS);
    
}
