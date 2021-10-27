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
package demetra.desktop.anomalydetection.comparer;

import demetra.desktop.anomalydetection.AnomalyItem;
import java.util.Comparator;

/**
 * Comparator for fields of AnomalyItem.
 * Compares the relative and absolute errors by their unsigned value
 * @author Mats Maggi
 */
public enum AnomalyItemComparer implements Comparator<AnomalyItem> {

    Relative1 {
        @Override
        public int compare(AnomalyItem o1, AnomalyItem o2) {
            Double r1 = Math.abs(o1.getRelativeError(0));
            Double r2 = Math.abs(o2.getRelativeError(0));
            return r1.compareTo(r2);
        }
    },
    
    Relative2 {
        @Override
        public int compare(AnomalyItem o1, AnomalyItem o2) {
            Double r1 = Math.abs(o1.getRelativeError(1));
            Double r2 = Math.abs(o2.getRelativeError(1));
            return r1.compareTo(r2);
        }
    },
    
    Relative3 {
        @Override
        public int compare(AnomalyItem o1, AnomalyItem o2) {
            Double r1 = Math.abs(o1.getRelativeError(2));
            Double r2 = Math.abs(o2.getRelativeError(2));
            return r1.compareTo(r2);
        }
    },
    
    Absolute1 {
        @Override
        public int compare(AnomalyItem o1, AnomalyItem o2) {
            Double r1 = Math.abs(o1.getAbsoluteError(0));
            Double r2 = Math.abs(o2.getAbsoluteError(0));
            return r1.compareTo(r2);
        }
    },
    
    Absolute2 {
        @Override
        public int compare(AnomalyItem o1, AnomalyItem o2) {
            Double r1 = Math.abs(o1.getAbsoluteError(1));
            Double r2 = Math.abs(o2.getAbsoluteError(1));
            return r1.compareTo(r2);
        }
    },
    
    Absolute3 {
        @Override
        public int compare(AnomalyItem o1, AnomalyItem o2) {
            Double r1 = Math.abs(o1.getAbsoluteError(2));
            Double r2 = Math.abs(o2.getAbsoluteError(2));
            return r1.compareTo(r2);
        }
    }
}
