package demetra.ui.beans;

import demetra.ui.Config;
import demetra.ui.ConfigEditor;
import demetra.ui.Converter;
import demetra.ui.properties.BeanEditor;
import java.beans.IntrospectionException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.util.Exceptions;

@lombok.AllArgsConstructor
public final class BeanConfigurator<B, R> implements ConfigEditor {

    @lombok.NonNull
    private final BeanHandler<B, R> beanHandler;

    @lombok.NonNull
    private final Converter<B, Config> converter;

    @lombok.NonNull
    private final BeanEditor editor;

    @NonNull
    public Config getConfig(R resource) {
        return converter.doForward(beanHandler.loadBean(resource));
    }

    public void setConfig(@NonNull R resource, @NonNull Config config) throws IllegalArgumentException {
        beanHandler.storeBean(resource, converter.doBackward(config));
    }

    @Override
    public Config editConfig(Config config) throws IllegalArgumentException {
        B bean = converter.doBackward(config);
        try {
            if (editor.editBean(bean)) {
                return converter.doForward(bean);
            }
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return config;
    }
}
