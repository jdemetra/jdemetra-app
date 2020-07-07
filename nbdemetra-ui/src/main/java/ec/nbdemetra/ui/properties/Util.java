/*
 * Copyright 2016 National Bank of Belgium
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
package ec.nbdemetra.ui.properties;

import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
final class Util {

    private Util() {
        // static class
    }

    @NonNull
    public static <T> Optional<T> attr(@Nullable PropertyEnv env, @NonNull String attrName, @NonNull Class<T> attrType) {
        if (env == null) {
            return Optional.empty();
        }
        Object value = env.getFeatureDescriptor().getValue(attrName);
        return attrType.isInstance(value) ? Optional.of(attrType.cast(value)) : Optional.empty();
    }

}
