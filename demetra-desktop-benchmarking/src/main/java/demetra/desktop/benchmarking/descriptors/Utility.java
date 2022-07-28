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
package demetra.desktop.benchmarking.descriptors;

import demetra.desktop.ui.properties.l2fprod.CustomPropertyEditorRegistry;

/**
 *
 * @author PALATEJ
 */
@lombok.experimental.UtilityClass
public class Utility {

    public static enum AggregationType {
        Sum, Average, Last, First
    }

    public AggregationType convert(demetra.data.AggregationType a) {
        return switch (a) {
            case Average -> AggregationType.Average;
            case Last -> AggregationType.Last;
            case First -> AggregationType.First;
            default -> AggregationType.Sum;
        };
    }

    public demetra.data.AggregationType convert(AggregationType a) {
        return switch (a) {
            case Average -> demetra.data.AggregationType.Average;
            case Last -> demetra.data.AggregationType.Last;
            case First -> demetra.data.AggregationType.First;
            default -> demetra.data.AggregationType.Sum;
        };
    }
    
}
