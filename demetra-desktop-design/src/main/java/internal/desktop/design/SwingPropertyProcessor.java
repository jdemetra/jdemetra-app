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

import demetra.desktop.design.SwingProperty;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

import static javax.lang.model.element.Modifier.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import nbbrd.service.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(Processor.class)
@SupportedAnnotationTypes("demetra.desktop.design.SwingProperty")
public final class SwingPropertyProcessor extends CustomProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ElementFilter
                .fieldsIn(roundEnv.getElementsAnnotatedWith(SwingProperty.class))
                .stream()
                .filter(field -> !skipProcessing(field))
                .forEach(this::checkSwingProperty);
        return true;
    }

    private void checkSwingProperty(VariableElement property) {
        if (!isPublicStaticFinal(property)) {
            error("Property must be public, static and final", property);
            return;
        }

        if (!isStringType(property)) {
            error("Property must be of type String", property);
            return;
        }

        String propertyID = getName(property);

        if (!isValidPropertyID(propertyID)) {
            error("Property constant must be all uppercase and named *_PROPERTY", property);
            return;
        }

        String fieldName = (String) property.getConstantValue();

        if (!isValidFieldName(propertyID, fieldName)) {
            error("Property must have a coherent field name", property);
            return;
        }

        Optional<ExecutableElement> setter = findSetter(property, fieldName);

        if (!setter.isPresent()) {
            error("Missing setter for '" + fieldName + "'", property);
            return;
        }

        TypeMirror fieldType = getFirstParameterType(setter);

        Optional<ExecutableElement> getter = findGetter(property, fieldName, fieldType.getKind().equals(TypeKind.BOOLEAN));

        if (!getter.isPresent()) {
            error("Missing getter for '" + fieldName + "'", property);
            return;
        }

        Optional<VariableElement> field = findField(property, fieldName);

        if (field.isPresent()) {
            if (!isPrivateNotFinalNotStatic(field.get())) {
                error("Property field must be private and not final nor static", property);
                return;
            }
            if (!types().isSameType(fieldType, field.get().asType())) {
                error("Property field must have the same type as its setter and getter", property);
                return;
            }
        }
    }

    private static TypeMirror getFirstParameterType(Optional<ExecutableElement> setter) {
        return setter.get().getParameters().get(0).asType();
    }

    private String getName(Element property) {
        return property.getSimpleName().toString();
    }

    private static boolean isPrivateNotFinalNotStatic(VariableElement field) {
        Set<Modifier> modifiers = field.getModifiers();
        return modifiers.contains(PRIVATE)
                && !modifiers.contains(FINAL)
                && !modifiers.contains(STATIC);
    }

    private static boolean isPublicStaticFinal(VariableElement property) {
        Set<Modifier> modifiers = property.getModifiers();
        return modifiers.contains(PUBLIC)
                && modifiers.contains(STATIC)
                && modifiers.contains(FINAL);
    }

    private boolean isStringType(VariableElement property) {
        return types().isSameType(property.asType(), getTypeElement(String.class).asType());
    }

    private Optional<VariableElement> findField(VariableElement property, String fieldName) {
        return ElementFilter
                .fieldsIn(property.getEnclosingElement().getEnclosedElements())
                .stream()
                .filter(field -> getName(field).equals(fieldName))
                .findFirst();
    }

    private Optional<ExecutableElement> findSetter(VariableElement property, String fieldName) {
        String setterName = toSetter(fieldName);
        return ElementFilter
                .methodsIn(property.getEnclosingElement().getEnclosedElements())
                .stream()
                .filter(method -> getName(method).equals(setterName))
                .filter(method -> method.getReturnType().getKind().equals(TypeKind.VOID))
                .filter(method -> method.getParameters().size() == 1)
                .findFirst();
    }

    private Optional<ExecutableElement> findGetter(VariableElement property, String fieldName, boolean booleanType) {
        String getterName = toGetter(fieldName, booleanType);
        return ElementFilter
                .methodsIn(property.getEnclosingElement().getEnclosedElements())
                .stream()
                .filter(method -> getName(method).equals(getterName))
                .filter(method -> !method.getReturnType().getKind().equals(TypeKind.VOID))
                .filter(method -> method.getParameters().isEmpty())
                .findFirst();
    }

    private static boolean isValidPropertyID(String propertyID) {
        return propertyID.endsWith("_PROPERTY")
                && propertyID.toUpperCase(Locale.ROOT).equals(propertyID);
    }

    private static boolean isValidFieldName(String propertyID, String fieldName) {
        return getFieldNameFromPropertyID(propertyID).equals(fieldName);
    }

    private static String getFieldNameFromPropertyID(String propertyID) {
        StringBuilder result = new StringBuilder();
        boolean uppercase = false;
        for (char c : propertyID.replace("_PROPERTY", "").toCharArray()) {
            if (c == '_') {
                uppercase = true;
                continue;
            }
            if (uppercase) {
                uppercase = false;
                result.append(Character.toUpperCase(c));
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }

    private static String toGetter(String fieldName, boolean booleanType) {
        return (booleanType ? "is" : "get") + toCamelCase(fieldName);
    }

    private static String toSetter(String fieldName) {
        return "set" + toCamelCase(fieldName);
    }

    private static String toCamelCase(String fieldName) {
        return fieldName.isEmpty() ? "" : (Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1));
    }
}
