/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.highfreq.ui;

import demetra.highfreq.ExtendedAirlineModellingSpec;


/**
 *
 * @author Jean Palate
 */
@lombok.Getter
@lombok.AllArgsConstructor
public class FractionalAirlineSpecRoot  {
    
    ExtendedAirlineModellingSpec core;
    boolean ro;
}
