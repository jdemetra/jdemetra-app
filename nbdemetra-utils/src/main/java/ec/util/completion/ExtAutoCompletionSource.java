/*
 * Copyright 2015 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.util.completion;

import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
public abstract class ExtAutoCompletionSource implements AutoCompletionSource {

    @Nonnull
    abstract public Request getRequest(@Nonnull String term);

    public static abstract class Request implements Callable<List<?>> {

        @Nonnull
        abstract public String getTerm();

        @Nonnull
        abstract public Behavior getBehavior();
    }

    @Nonnull
    public static Request wrap(@Nonnull AutoCompletionSource source, @Nonnull String term) {
        return new RequestWrapper(source, term);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static final class RequestWrapper extends Request {

        private final AutoCompletionSource source;
        private final String term;

        public RequestWrapper(AutoCompletionSource source, String term) {
            this.source = source;
            this.term = term;
        }

        @Override
        public String getTerm() {
            return term;
        }

        @Override
        public Behavior getBehavior() {
            return source.getBehavior(term);
        }

        @Override
        public List<?> call() throws Exception {
            return source.getValues(term);
        }
    }
    //</editor-fold>
}
