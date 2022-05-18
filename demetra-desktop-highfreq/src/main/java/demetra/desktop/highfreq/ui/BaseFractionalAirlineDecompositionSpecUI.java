/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.highfreq.DecompositionSpec;
import demetra.highfreq.ExtendedAirlineDecompositionSpec;
import demetra.highfreq.ExtendedAirlineModellingSpec;

/**
 *
 * @author PALATEJ
 */
public abstract class BaseFractionalAirlineDecompositionSpecUI implements IPropertyDescriptors{
    
    final FractionalAirlineDecompositionSpecRoot root;
        
    BaseFractionalAirlineDecompositionSpecUI(FractionalAirlineDecompositionSpecRoot root){
        this.root =root;
    }
    
    ExtendedAirlineModellingSpec preprocessing(){return root.getPreprocessing().getCore();}
    DecompositionSpec seats(){return root.getDecomposition();}
    
    boolean isRo(){return root.isRo();}
    
    void update(DecompositionSpec nseats){
        root.update(nseats);
    }
}
