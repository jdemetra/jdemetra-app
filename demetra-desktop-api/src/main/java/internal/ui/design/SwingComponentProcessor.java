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

import demetra.ui.design.SwingComponent;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import static javax.lang.model.element.Modifier.*;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.swing.JComponent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = Processor.class)
@SupportedAnnotationTypes("demetra.ui.design.SwingComponent")
public final class SwingComponentProcessor extends CustomProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ElementFilter
                .typesIn(roundEnv.getElementsAnnotatedWith(SwingComponent.class))
                .forEach(this::checkSwingComponent);
        return true;
    }

    private void checkSwingComponent(TypeElement swingComponent) {
        if (!swingComponent.getModifiers().contains(FINAL)) {
            error("Must be final", swingComponent);
        }

        if (!hasPublicConstructor(swingComponent)) {
            error("Must have public constructor without args", swingComponent);
        }

        if (!types().isSameType(swingComponent.getSuperclass(), getTypeElement(JComponent.class).asType())) {
            error("Must extend JComponent", swingComponent);
        }

        getPublicFields(swingComponent)
                .forEach(field -> error("Must not have public field", field));
    }

    private static Stream<VariableElement> getPublicFields(TypeElement swingComponent) {
        return ElementFilter
                .fieldsIn(swingComponent.getEnclosedElements())
                .stream()
                .filter(field -> !field.getModifiers().contains(STATIC) && field.getModifiers().contains(PUBLIC));
    }

    private boolean hasPublicConstructor(TypeElement typeElement) {
        return ElementFilter
                .constructorsIn(typeElement.getEnclosedElements())
                .stream()
                .anyMatch(x -> x.getModifiers().contains(PUBLIC));

    }
}
