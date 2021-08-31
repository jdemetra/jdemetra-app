package internal.desktop.design;

import nbbrd.design.SkipProcessing;

import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Locale;
import java.util.Set;

import static javax.lang.model.element.Modifier.*;

abstract class CustomProcessor extends AbstractProcessor {

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    protected void error(CharSequence msg, Element e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
    }

    protected Types types() {
        return processingEnv.getTypeUtils();
    }

    protected Elements elements() {
        return processingEnv.getElementUtils();
    }

    protected TypeElement getTypeElement(Class<?> type) {
        return elements().getTypeElement(type.getCanonicalName());
    }

    protected static boolean skipProcessing(Element e) {
        SkipProcessing annotation = e.getAnnotation(SkipProcessing.class);
        if (annotation == null) {
            return false;
        }
        // TODO: improve check to handle target
        return true;
    }

    static boolean isPublicStaticFinal(VariableElement property) {
        Set<Modifier> modifiers = property.getModifiers();
        return modifiers.contains(PUBLIC)
                && modifiers.contains(STATIC)
                && modifiers.contains(FINAL);
    }

    protected boolean isStringType(VariableElement property) {
        return types().isSameType(property.asType(), getTypeElement(String.class).asType());
    }

    protected static boolean isValidID(String id, String suffix) {
        return id.endsWith(suffix)
                && id.toUpperCase(Locale.ROOT).equals(id);
    }

    protected String getName(Element property) {
        return property.getSimpleName().toString();
    }

    protected static boolean isValidFieldName(String id, String fieldName, String suffix) {
        return getFieldNameFromID(id, suffix).equals(fieldName);
    }

    private static String getFieldNameFromID(String id, String suffix) {
        StringBuilder result = new StringBuilder();
        boolean uppercase = false;
        for (char c : id.replace(suffix, "").toCharArray()) {
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
}
