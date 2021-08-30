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
package internal.desktop.design;

import demetra.desktop.design.GlobalService;
import static java.util.Arrays.asList;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.ExecutableElement;
import static javax.lang.model.element.Modifier.*;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import nbbrd.service.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(Processor.class)
@SupportedAnnotationTypes("demetra.desktop.design.GlobalService")
public final class GlobalServiceProcessor extends CustomProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ElementFilter
                .typesIn(roundEnv.getElementsAnnotatedWith(GlobalService.class))
                .forEach(this::checkGlobalService);
        return true;
    }

    private void checkGlobalService(TypeElement globalService) {
        if (!globalService.getModifiers().contains(FINAL)) {
            error("Must be final", globalService);
        }

        if (hasPublicConstructor(globalService)) {
            error("Must not have public constructor", globalService);
        }

        Optional<ExecutableElement> defaultMethod = getDefaultMethod(globalService);
        if (!defaultMethod.isPresent()) {
            error("Missing method getDefault()", globalService);
        } else {
            checkDefaultMethod(globalService, defaultMethod.get());
        }
    }

    private void checkDefaultMethod(TypeElement globalService, ExecutableElement defaultMethod) {
        if (!defaultMethod.getModifiers().containsAll(asList(PUBLIC, STATIC))) {
            error("Default method must be public and static", defaultMethod);
        }

        if (!defaultMethod.getTypeParameters().isEmpty()) {
            error("Default method requires no parameters", defaultMethod);
        }

        if (!types().isSameType(defaultMethod.getReturnType(), globalService.asType())) {
            error("Default method must return the annotated type", defaultMethod);
        }
    }

    private Optional<ExecutableElement> getDefaultMethod(TypeElement typeElement) {
        return ElementFilter
                .methodsIn(typeElement.getEnclosedElements())
                .stream()
                .filter(x -> x.getSimpleName().toString().equals("getDefault"))
                .findFirst();
    }

    private boolean hasPublicConstructor(TypeElement t) {
        return ElementFilter
                .constructorsIn(t.getEnclosedElements())
                .stream()
                .anyMatch(x -> x.getModifiers().contains(PUBLIC));

    }
}
