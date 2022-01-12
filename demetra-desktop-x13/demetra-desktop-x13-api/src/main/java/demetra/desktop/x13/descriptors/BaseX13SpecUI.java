/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.x13.descriptors;

import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.regarima.RegArimaSpec;
import demetra.x11.X11Spec;

/**
 *
 * @author PALATEJ
 */
public abstract class BaseX13SpecUI implements IPropertyDescriptors{
    
    final X13SpecRoot root;
        
    BaseX13SpecUI(X13SpecRoot root){
        this.root =root;
    }
    
    RegArimaSpec regarima(){return root.getRegarima().getCore();}
    
    X11Spec x11(){return root.getX11();}
    
    boolean isRo(){return root.isRo();}
    
    void update(X11Spec nx11){
        root.update(nx11);
    }
}
