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
package demetra.desktop.sa.util;

import demetra.sa.SaProcessingFactory;
import demetra.sa.SaProcessor;
import demetra.sa.SaSpecification;
import java.util.List;

/**
 *
 * @author palatej
 */
public interface ActionsHelper {
    
    List<String> selectedSeries();
    
    List<String> selectedMatrixItems();
    
    List<String> allSeries();
    
    List<String> allMatrixItems();
    
    boolean match(SaSpecification spec);
    
    boolean match(SaProcessingFactory fac);

    int defaultSeriesParameter();
    
}
