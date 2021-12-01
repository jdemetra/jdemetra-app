/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.descriptors;

import demetra.regarima.RegArimaSpec;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Jean Palate
 */
class RegArimaSpecRoot  {

    RegArimaSpec core;
    final boolean ro_;

    RegArimaSpecRoot(@NonNull RegArimaSpec spec, boolean ro) {
        core = spec;
        ro_ = ro;
    }

    RegArimaSpec getCore() {
        return core;
    }
    
    boolean isRo(){
        return ro_;
    }

 }
