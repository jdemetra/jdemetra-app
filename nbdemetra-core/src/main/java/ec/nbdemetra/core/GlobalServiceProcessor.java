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
package ec.nbdemetra.core;

import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.Diagnostic;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = Processor.class)
@SupportedAnnotationTypes("ec.nbdemetra.core.GlobalService")
public final class GlobalServiceProcessor extends AbstractProcessor {

    private final Diagnostic.Kind kind = Diagnostic.Kind.ERROR;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Messager m = processingEnv.getMessager();
        for (Element e : roundEnv.getElementsAnnotatedWith(GlobalService.class)) {

            if (e.getModifiers().contains(Modifier.FINAL)) {
                m.printMessage(kind, "Cannot be final", e);
                continue;
            }

            Element method = getDefaultMethod((TypeElement) e);
            if (method == null) {
                m.printMessage(kind, "Missing method getDefault()", e);
                continue;
            }
            if (!(method.getModifiers().contains(Modifier.PUBLIC) && method.getModifiers().contains(Modifier.STATIC))) {
                m.printMessage(kind, "Default method must be public and static", e);
                continue;
            }
            ExecutableType t = (ExecutableType) method.asType();
            if (!t.getParameterTypes().isEmpty()) {
                m.printMessage(kind, "Default method requires no parameters", e);
                continue;
            }
            if (!t.getReturnType().toString().equals(e.asType().toString())) {
                m.printMessage(kind, "Default method must return the annotated type", e);
                continue;
            }
        }
        return true;
    }

    private Element getDefaultMethod(TypeElement typeElement) {
        for (Element x : typeElement.getEnclosedElements()) {
            if (x.getKind() == ElementKind.METHOD) {
                if (x.getSimpleName().toString().equals("getDefault")) {
                    return x;
                }
            }
        }
        return null;
    }
}
