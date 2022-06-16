/*
 * Copyright 2013 National Bank of Belgium
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
package demetra.desktop.beans;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 * @author Philippe Charles
 * @param <B> the type of the bean
 * @param <R> the type of the resource
 */
public interface BeanHandler<B, R> {

    B load(R resource);

    void store(R resource, B bean);
    
    static <B, R> BeanHandler<B, R> of(Function<R, B> getter, BiConsumer<R, B> setter) {
        return new BeanHandler<B, R>() {
            @Override
            public B load(R resource) {
                return getter.apply(resource);
            }

            @Override
            public void store(R resource, B bean) {
                setter.accept(resource, bean);
            }            
        };
    }
}
