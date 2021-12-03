/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.descriptors;

import demetra.timeseries.TsDomain;
import demetra.tramo.TramoSpec;

/**
 *
 * @author Jean Palate
 */
@lombok.Getter
@lombok.AllArgsConstructor
class TramoSpecRoot  {
    
    TramoSpec core;
    boolean ro;
    TsDomain domain;

 
    TramoSpec getCore() {
        return core;
    }
    
    boolean isRo(){
        return ro;
    }
    
 }
