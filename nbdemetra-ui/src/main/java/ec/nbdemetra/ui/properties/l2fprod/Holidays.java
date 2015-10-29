/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties.l2fprod;

import ec.tstoolkit.timeseries.calendars.GregorianCalendarManager;

/**
 *
 * @author Jean Palate
 */
public class Holidays {

    private final String name_;

    public Holidays(String name) {
        if (name != null) {
            this.name_ = name;
        }
        else {
            this.name_ = GregorianCalendarManager.DEF;
        }
    }

    public String getName() {
        return name_;
    }

    @Override
    public String toString() {
        return name_;
    }
}
