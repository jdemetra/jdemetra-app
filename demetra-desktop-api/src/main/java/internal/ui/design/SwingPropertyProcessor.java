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
package internal.ui.design;

import demetra.ui.design.SwingProperty;
import java.util.Optional;
import org.openide.util.lookup.ServiceProvider;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import java.util.Set;
import javax.lang.model.element.Modifier;

import static javax.lang.model.element.Modifier.*;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = Processor.class)
@SupportedAnnotationTypes("demetra.ui.design.SwingProperty")
public final class SwingPropertyProcessor extends CustomProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ElementFilter
                .fieldsIn(roundEnv.getElementsAnnotatedWith(SwingProperty.class))
                .forEach(this::checkSwingComponent);
        return true;
    }

    private void checkSwingComponent(VariableElement swingProperty) {
        Set<Modifier> propertyModifiers = swingProperty.getModifiers();

        if (!propertyModifiers.contains(PUBLIC)
                || !swingProperty.getModifiers().contains(STATIC)
                || !swingProperty.getModifiers().contains(FINAL)) {
            error("Property must be public, static and final", swingProperty);
            return;
        }

        if (!types().isSameType(swingProperty.asType(), getTypeElement(String.class).asType())) {
            error("Property must be of type String", swingProperty);
            return;
        }

        if (!swingProperty.getSimpleName().toString().endsWith("_PROPERTY")) {
            error("Property must be named *_PROPERTY", swingProperty);
            return;
        }

        String propertyName = (String) swingProperty.getConstantValue();

        Optional<VariableElement> propertyField = ElementFilter.fieldsIn(swingProperty.getEnclosingElement().getEnclosedElements())
                .stream()
                .filter(field -> field.getSimpleName().toString().equals(propertyName))
                .findFirst();

        if (!propertyField.isPresent()) {
            error("Property field '" + propertyName + "' is missing", swingProperty);
            return;
        }

        Set<Modifier> propertyFieldModifiers = propertyField.get().getModifiers();

        if (!propertyFieldModifiers.contains(PRIVATE)
                || propertyFieldModifiers.contains(FINAL)
                || propertyFieldModifiers.contains(STATIC)) {
            error("Property field must be private and not final nor static", swingProperty);
            return;
        }
    }
}
