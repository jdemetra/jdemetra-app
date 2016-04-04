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

import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
public abstract class ObsPredicate {

    public abstract boolean apply(int series, int obs);

    final public boolean apply(@Nonnull ObsIndex index) throws NullPointerException {
        return apply(index.getSeries(), index.getObs());
    }

    @Nonnull
    public ObsFunction<Boolean> asFunction() {
        return new AsFunction(this);
    }

    @Nonnull
    public static ObsPredicate alwaysTrue() {
        return TRUE;
    }

    @Nonnull
    public static ObsPredicate alwaysFalse() {
        return FALSE;
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
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

    private static final class AsFunction extends ObsFunction<Boolean> {

        private final ObsPredicate predicate;

        public AsFunction(ObsPredicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public Boolean apply(int series, int obs) {
            return predicate.apply(series, obs);
        }
    }
    //</editor-fold>
}
