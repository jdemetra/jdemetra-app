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
package demetra.desktop.beans;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.beans.IntrospectionException;
import java.util.function.Consumer;

/**
 * @author Philippe Charles
 */
public interface BeanEditor {

    boolean editBean(@NonNull Object bean) throws IntrospectionException;

    default boolean editBean(@NonNull Object bean, @NonNull Consumer<? super IntrospectionException> onError) {
        try {
            return editBean(bean);
        } catch (IntrospectionException ex) {
            onError.accept(ex);
            return false;
        }
    }
}
