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
package internal.ui;

import demetra.ui.GlobalService;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = Processor.class)
@SupportedAnnotationTypes("demetra.ui.GlobalService")
public final class GlobalServiceProcessor extends AbstractProcessor {

    private final Diagnostic.Kind kind = Diagnostic.Kind.ERROR;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Messager m = processingEnv.getMessager();
        for (TypeElement e : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(GlobalService.class))) {

            if (!e.getModifiers().contains(Modifier.FINAL)) {
                m.printMessage(kind, "Must be final", e);
                continue;
            }

            if (hasPublicConstructor(e)) {
                m.printMessage(kind, "Must not have public constructor", e);
                continue;
            }

            Optional<ExecutableElement> method = getDefaultMethod(e);
            if (!method.isPresent()) {
                m.printMessage(kind, "Missing method getDefault()", e);
                continue;
            }
            if (!method.get().getModifiers().containsAll(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC))) {
                m.printMessage(kind, "Default method must be public and static", e);
                continue;
            }
            ExecutableType t = (ExecutableType) method.get().asType();
            if (!t.getParameterTypes().isEmpty()) {
                m.printMessage(kind, "Default method requires no parameters", e);
                continue;
            }
            if (!processingEnv.getTypeUtils().isSameType(t.getReturnType(), e.asType())) {
                m.printMessage(kind, "Default method must return the annotated type", e);
                continue;
            }
        }
        return true;
    }

    private Optional<ExecutableElement> getDefaultMethod(TypeElement typeElement) {
        return ElementFilter
                .methodsIn(typeElement.getEnclosedElements())
                .stream()
                .filter(method -> method.getSimpleName().toString().equals("getDefault"))
                .findFirst();
    }

    private boolean hasPublicConstructor(TypeElement typeElement) {
        return ElementFilter
                .constructorsIn(typeElement.getEnclosedElements())
                .stream()
                .anyMatch(method -> method.getModifiers().contains(Modifier.PUBLIC));

    }
}
