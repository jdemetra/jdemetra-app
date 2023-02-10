/*
 * Copyright 2023 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.sa.descriptors.highfreq;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.desktop.ui.properties.l2fprod.Holidays;
import demetra.modelling.highfreq.HolidaysSpec;
import demetra.timeseries.calendars.HolidaysOption;
import demetra.timeseries.regression.HolidaysVariable;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public abstract class AbstractHolidaysSpecUI implements IPropertyDescriptors {

    @Override
    public String toString() {
        return spec().isUsed() ? "in use" : "";
    }

    protected abstract HolidaysSpec spec();

    protected abstract HighFreqSpecUI root();

    public boolean isro() {
        return root().isRo() || spec().hasFixedCoefficients();
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
        desc = nwDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    public Holidays getHolidays() {
        return new Holidays(spec().getHolidays());
    }

    public void setHolidays(Holidays holidays) {
        root().update(spec().toBuilder().holidays(holidays.getName()).build());
    }

    @NbBundle.Messages({
        "highfreq.holidaysSpecUI.calendarDesc.name=Calendar",
        "highfreq.holidaysSpecUI.calendarDesc.desc=Calendar used to generate the variables"
    })
    private EnhancedPropertyDescriptor calendarDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("holidays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CALENDAR_ID);
            desc.setDisplayName(Bundle.highfreq_holidaysSpecUI_calendarDesc_name());
            desc.setShortDescription(Bundle.highfreq_holidaysSpecUI_calendarDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public HolidaysOption getHolidaysOption() {
        return spec().getHolidaysOption();
    }

    public void setHolidaysOption(HolidaysOption option) {
        if (option != spec().getHolidaysOption()) {
            root().update(spec().toBuilder()
                    .holidaysOption(option)
                    .build());
        }
    }

    @NbBundle.Messages({
        "highfreq.holidaysSpecUI.hoptionDesc.name=Option",
        "highfreq.holidaysSpecUI.hoptionDesc.desc=Skip: holdays falling a non-working days are discarded, "
                + "Default: no special treatment, Next/Previous: moving to the next/previous working days"
    })
    private EnhancedPropertyDescriptor hoptionDesc() {
        if (!spec().isUsed()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("HolidaysOption", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, HOPTION_ID);
            desc.setDisplayName(Bundle.highfreq_holidaysSpecUI_hoptionDesc_name());
            desc.setShortDescription(Bundle.highfreq_holidaysSpecUI_hoptionDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isSingle() {
        return spec().isSingle();
    }

    public void setSingle(boolean single) {
        if (single != spec().isSingle()) {
            root().update(spec().toBuilder()
                    .single(single)
                    .build());
        }
    }

    @NbBundle.Messages({
        "highfreq.holidaysSpecUI.singleDesc.name=Single",
        "highfreq.holidaysSpecUI.singleDesc.desc=A single variable is used for all the holidays"
    })
    private EnhancedPropertyDescriptor singleDesc() {
        if (!spec().isUsed()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Single", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SINGLE_ID);
            desc.setDisplayName(Bundle.highfreq_holidaysSpecUI_singleDesc_name());
            desc.setShortDescription(Bundle.highfreq_holidaysSpecUI_singleDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isWeekEnd() {
        return Arrays.equals(spec().getNonWorkingDays(), HolidaysVariable.NONWORKING_WE);
    }

    public void setWeekEnd(boolean we) {
        if (we != isWeekEnd()) {
            root().update(spec().toBuilder()
                    .nonWorkingDays(we ? HolidaysVariable.NONWORKING_WE : HolidaysVariable.NONWORKING_SUNDAYS)
                    .build());
        }
    }

    @NbBundle.Messages({
        "highfreq.holidaysSpecUI.nwDesc.name=Non working week-ends",
        "highfreq.holidaysSpecUI.nwDesc.desc=Week-ends are considered as non-working days. Otherwise, only the Sundays are considered as non-working days"
    })
    private EnhancedPropertyDescriptor nwDesc() {
        if (!spec().isUsed()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("WeekEnd", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NONWORKING_ID);
            desc.setDisplayName(Bundle.highfreq_holidaysSpecUI_nwDesc_name());
            desc.setShortDescription(Bundle.highfreq_holidaysSpecUI_nwDesc_name());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private static final int CALENDAR_ID = 3, HOPTION_ID = 4, SINGLE_ID = 5, NONWORKING_ID = 6;

    @Override
    @NbBundle.Messages("highfreq.holidaysSpecUI.displayName=Holidays")
    public String getDisplayName() {
        return Bundle.highfreq_holidaysSpecUI_displayName();
    }

}
