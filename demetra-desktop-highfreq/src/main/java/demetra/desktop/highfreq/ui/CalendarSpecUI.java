/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.ui.properties.l2fprod.Holidays;
import demetra.highfreq.HolidaysSpec;
import demetra.timeseries.calendars.HolidaysOption;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public class CalendarSpecUI  extends BaseFractionalAirlineSpecUI {

    @Override
    public String toString() {
        return inner().isUsed() ? "in use" : "";
    }

    private HolidaysSpec inner() {
       return core().getRegression().getCalendar();
    }

    public CalendarSpecUI(FractionalAirlineSpecRoot root) {
        super(root);
    }
    
    public boolean isro(){
        return super.isRo() || inner().hasFixedCoefficients();
    }
    
    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = calendarDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = hoptionDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = singleDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    
    public Holidays getHolidays() {
        return new Holidays(inner().getHolidays());
    }

    public void setHolidays(Holidays holidays) {
        update(inner().toBuilder().holidays(holidays.getName()).build());
    }

    private EnhancedPropertyDescriptor calendarDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("holidays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CALENDAR_ID);
            desc.setDisplayName("calendar");
            desc.setShortDescription("Calendar");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public HolidaysOption getHolidaysOption() {
        return inner().getHolidaysOption();
    }

    public void setHolidaysOption(HolidaysOption option) {
        if (option != inner().getHolidaysOption()) {
            update(inner().toBuilder()
                    .holidaysOption(option)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor hoptionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("HolidaysOption", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, HOPTION_ID);
            desc.setDisplayName("option");
            desc.setShortDescription("Holiday option");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    public boolean isSingle() {
        return inner().isSingle();
    }

    public void setSingle(boolean single) {
        if (single != inner().isSingle()) {
            update(inner().toBuilder()
                    .single(single)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor singleDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Single", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SINGLE_ID);
            desc.setDisplayName("single");
            desc.setShortDescription("single holiday variable");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    private static final int CALENDAR_ID = 3, HOPTION_ID=4, SINGLE_ID = 5;

    @Override
    @NbBundle.Messages("calendarSpecUI.getDisplayName=Holidays")
    public String getDisplayName() {
        return Bundle.calendarSpecUI_getDisplayName();
    }
    
}
