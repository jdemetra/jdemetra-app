/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.tramoseats.descriptors;

import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.seats.DecompositionSpec;
import demetra.tramo.TramoSpec;

/**
 *
 * @author PALATEJ
 */
public abstract class BaseTramoSeatsSpecUI implements IPropertyDescriptors{
    
    final TramoSeatsSpecRoot root;
        
    BaseTramoSeatsSpecUI(TramoSeatsSpecRoot root){
        this.root =root;
    }
    
    TramoSpec tramo(){return root.getTramo().getCore();}
    DecompositionSpec seats(){return root.getSeats();}
    
    boolean isRo(){return root.isRo();}
    
    void update(DecompositionSpec nseats){
        root.update(nseats);
    }
}
