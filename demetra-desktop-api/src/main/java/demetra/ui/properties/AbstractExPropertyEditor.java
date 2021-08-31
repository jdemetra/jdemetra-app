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
package demetra.ui.properties;

import demetra.ui.util.Collections2;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

import java.beans.PropertyEditorSupport;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Philippe Charles
 */
public abstract class AbstractExPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private final Supplier<InplaceEditor> inplaceEditor = Collections2.memoize(this::createInplaceEditor);

    @Override
    public void attachEnv(PropertyEnv env) {
        env.registerInplaceEditorFactory(this);
    }

    @Override
    public InplaceEditor getInplaceEditor() {
        return inplaceEditor.get();
    }

    @NonNull
    abstract protected InplaceEditor createInplaceEditor();

    @NonNull
    public static <T> Optional<T> attr(@Nullable PropertyEnv env, @NonNull String attrName, @NonNull Class<T> attrType) {
        if (env == null) {
            return Optional.empty();
        }
        Object value = env.getFeatureDescriptor().getValue(attrName);
        return attrType.isInstance(value) ? Optional.of(attrType.cast(value)) : Optional.empty();
    }
}
