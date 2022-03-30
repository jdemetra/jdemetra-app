/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
