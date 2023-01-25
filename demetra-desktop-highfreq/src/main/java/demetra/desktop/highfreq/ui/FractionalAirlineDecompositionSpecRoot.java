/*
 * Copyright 2023 National Bank of Belgium
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
package demetra.desktop.highfreq.ui;

import demetra.highfreq.DecompositionSpec;
import demetra.highfreq.ExtendedAirlineDecompositionSpec;


/**
 *
 * @author Jean Palate
 */
@lombok.Getter
@lombok.AllArgsConstructor
public class FractionalAirlineDecompositionSpecRoot  {
    
    public FractionalAirlineDecompositionSpecRoot(ExtendedAirlineDecompositionSpec spec, boolean ro){
        preprocessing=new FractionalAirlineSpecRoot(spec.getPreprocessing(), ro);
        decomposition=spec.getDecomposition();
     }
    
    @lombok.NonNull
    final FractionalAirlineSpecRoot preprocessing;
    @lombok.NonNull
    DecompositionSpec decomposition;
  
    ExtendedAirlineDecompositionSpec getCore() {
        return ExtendedAirlineDecompositionSpec.builder()
                .preprocessing(preprocessing.getCore())
                .decomposition(decomposition)
                .build();
    }
    
    boolean isRo(){
        return preprocessing.isRo();
    }
    
    void update(DecompositionSpec ndecomposition){
        decomposition=ndecomposition;
    }
    
}
