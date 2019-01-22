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
package ec.nbdemetra.ui;

import demetra.ui.properties.PropertySheetDialogBuilder;
import com.google.common.base.Converter;
import demetra.ui.properties.IBeanEditor;
import java.beans.IntrospectionException;
import javax.annotation.Nonnull;
import org.openide.util.Exceptions;

/**
 *
 * @author Philippe Charles
 * @param <B> the type of the bean
 * @param <R> the type of the resource
 */
public abstract class BeanHandler<B, R> {

    @Nonnull
    public abstract B loadBean(@Nonnull R resource);

    public abstract void storeBean(@Nonnull R resource, @Nonnull B bean);

    @Nonnull
    public Configurator<R> toConfigurator(@Nonnull Converter<B, Config> converter) {
        return new ConfiguratorImpl(this, converter, DEFAULT_EDITOR);
    }

    @Nonnull
    public Configurator<R> toConfigurator(@Nonnull Converter<B, Config> converter, @Nonnull IBeanEditor editor) {
        return new ConfiguratorImpl(this, converter, editor);
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private static final IBeanEditor DEFAULT_EDITOR = o -> new PropertySheetDialogBuilder().editBean(o);

    private static final class ConfiguratorImpl<B, R> extends Configurator<R> {

        private final BeanHandler<B, R> beanHandler;
        private final Converter<B, Config> converter;
        private final IBeanEditor editor;

        public ConfiguratorImpl(BeanHandler<B, R> beanHandler, Converter<B, Config> converter, IBeanEditor editor) {
            this.beanHandler = beanHandler;
            this.converter = converter;
            this.editor = editor;
        }

        @Override
        public Config getConfig(R resource) {
            return converter.convert(beanHandler.loadBean(resource));
        }

        @Override
        public void setConfig(R resource, Config config) throws IllegalArgumentException {
            beanHandler.storeBean(resource, converter.reverse().convert(config));
        }

        @Override
        public Config editConfig(Config config) throws IllegalArgumentException {
            B bean = converter.reverse().convert(config);
            try {
                if (editor.editBean(bean)) {
                    return converter.convert(bean);
                }
            } catch (IntrospectionException ex) {
                Exceptions.printStackTrace(ex);
            }
            return config;
        }
    }
    //</editor-fold>
}
