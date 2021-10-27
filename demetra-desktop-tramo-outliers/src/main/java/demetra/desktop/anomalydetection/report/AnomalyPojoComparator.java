/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.anomalydetection.report;

import demetra.timeseries.TsPeriod;
import java.util.Comparator;

/**
 * Comparator providing sort of Anomaly POJO items used by Jasper Reports
 * @author Mats Maggi
 */
public enum AnomalyPojoComparator implements Comparator<AnomalyPojo> {
    
    Relative_Asc("Relative error ascending") {
        @Override
        public int compare(AnomalyPojo o1, AnomalyPojo o2) {
            Double r1 = Math.abs(o1.getRelativeError());
            Double r2 = Math.abs(o2.getRelativeError());
            return r1.compareTo(r2);
        }
    },
    
    Relative_Desc("Relative error descending") {
        @Override
        public int compare(AnomalyPojo o1, AnomalyPojo o2) {
            Double r1 = Math.abs(o1.getRelativeError());
            Double r2 = Math.abs(o2.getRelativeError());
            return r2.compareTo(r1);
        }
    },
    
    Absolute_Asc("Absolute error ascending") {
        @Override
        public int compare(AnomalyPojo o1, AnomalyPojo o2) {
            Double r1 = Math.abs(o1.getAbsoluteError());
            Double r2 = Math.abs(o2.getAbsoluteError());
            return r1.compareTo(r2);
        }
    },
    
    Absolute_Desc("Absolute error descending") {
        @Override
        public int compare(AnomalyPojo o1, AnomalyPojo o2) {
            Double r1 = Math.abs(o1.getAbsoluteError());
            Double r2 = Math.abs(o2.getAbsoluteError());
            return r2.compareTo(r1);
        }
    },
    
    Period_Asc("Period ascending") {
        @Override
        public int compare(AnomalyPojo o1, AnomalyPojo o2) {
            TsPeriod r1 = o1.getPeriod();
            TsPeriod r2 = o2.getPeriod();
            return r1.compareTo(r2);
        }
    },
    
    Period_Desc("Period descending") {
        @Override
        public int compare(AnomalyPojo o1, AnomalyPojo o2) {
            TsPeriod r1 = o1.getPeriod();
            TsPeriod r2 = o2.getPeriod();
            return -r2.compareTo(r1);
        }
    };
    
    private final String desc;

    AnomalyPojoComparator(String desc) {
        this.desc = desc;
    }
    
    @Override
    public String toString() {
        return desc;
    }
}
