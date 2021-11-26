/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.properties.l2fprod;

import demetra.timeseries.calendars.CalendarManager;


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
            this.name_ = CalendarManager.DEF;
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
