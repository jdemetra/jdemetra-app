/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.descriptors;

import demetra.tramo.TramoSpec;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Jean Palate
 */
class TramoSpecRoot  {

    TramoSpec core;
    final boolean ro_;

    TramoSpecRoot(@NonNull TramoSpec spec, boolean ro) {
        core = spec;
        ro_ = ro;
    }

    TramoSpec getCore() {
        return core;
    }
    
    boolean isRo(){
        return ro_;
    }

 }
