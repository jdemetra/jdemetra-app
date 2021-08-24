package internal.ui.design;

import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

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
}
