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

import demetra.desktop.design.SwingAction;
import nbbrd.service.ServiceProvider;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import java.util.Set;

/**
 * @author Philippe Charles
 */
@ServiceProvider(Processor.class)
@SupportedAnnotationTypes("demetra.desktop.design.SwingAction")
public final class SwingActionProcessor extends CustomProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ElementFilter
                .fieldsIn(roundEnv.getElementsAnnotatedWith(SwingAction.class))
                .stream()
                .filter(field -> !skipProcessing(field))
                .forEach(this::checkSwingAction);
        return true;
    }

    private void checkSwingAction(VariableElement property) {
        if (!isPublicStaticFinal(property)) {
            error("Property must be public, static and final", property);
            return;
        }

        if (!isStringType(property)) {
            error("Property must be of type String", property);
            return;
        }

        String propertyID = getName(property);

        if (!isValidID(propertyID, "_ACTION")) {
            error("Property constant must be all uppercase and named *_ACTION", property);
            return;
        }

        String fieldName = (String) property.getConstantValue();

        if (!isValidFieldName(propertyID, fieldName, "_ACTION")) {
            error("Property must have a coherent field name", property);
            return;
        }
    }
}
