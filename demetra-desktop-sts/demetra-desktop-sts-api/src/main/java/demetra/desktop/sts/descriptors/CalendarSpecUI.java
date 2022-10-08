/*
 * Copyright 2022 National Bank of Belgium
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
package demetra.desktop.sts.descriptors;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class CalendarSpecUI extends BasePreprocessingSpecUI {

    /**
     *
     * @param spec
     */
    public CalendarSpecUI(PreprocessingSpecification spec) {
        super(spec);
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = tdDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = lpDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = eDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = testDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Calendar";
    }

    public TradingDaysType getTradingDays(){
        return core.dtype;
    }

    public void setTradingDays(TradingDaysType value){
        core.dtype=value;
    }

    public LengthOfPeriodType getLeapYear(){
        return core.ltype;
    }

    public void setLeapYear(LengthOfPeriodType value){
        core.ltype=value;
    }

    public boolean getEaster(){
        return core.easter;
    }

    public void setEaster(boolean value){
        core.easter=value;
    }

    public boolean getTest(){
        return core.pretest;
    }

    public void setTest(boolean value){
        core.pretest=value;
    }

    private static final int TD_ID = 1, LP_ID = 2, E_ID = 3, TEST_ID = 4;

    private EnhancedPropertyDescriptor tdDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("tradingDays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(TD_NAME);
            desc.setShortDescription(TD_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor lpDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("leapYear", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LP_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(LP_NAME);
            desc.setShortDescription(LP_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor eDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("easter", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, E_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(E_NAME);
            desc.setShortDescription(E_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor testDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("test", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TEST_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(TEST_NAME);
            desc.setShortDescription(TEST_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private static final String
            TD_NAME = "Trading days",
            LP_NAME = "Leap year",
            E_NAME = "Easter",
            TEST_NAME = "Pre-test";

    private static final String
            TD_DESC = "Trading days effect",
            LP_DESC = "Leap year effect",
            E_DESC = "Easter effect",
            TEST_DESC = "Test for the presence of calendar effects";
}
