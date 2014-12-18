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
package ec.util.chart;

/**
 *
 * @author Philippe Charles
 */
public abstract class ObsPredicate {

    public abstract boolean apply(int series, int obs);

    public static ObsPredicate alwaysTrue() {
        return TRUE;
    }

    public static ObsPredicate alwaysFalse() {
        return FALSE;
    }
    //
    private static final ObsPredicate TRUE = new ObsPredicate() {
        @Override
        public boolean apply(int series, int obs) {
            return true;
        }
    };
    private static final ObsPredicate FALSE = new ObsPredicate() {
        @Override
        public boolean apply(int series, int obs) {
            return false;
        }
    };
}
