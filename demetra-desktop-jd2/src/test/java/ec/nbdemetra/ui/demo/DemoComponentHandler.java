/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package ec.nbdemetra.ui.demo;

import ec.tstoolkit.design.ServiceDefinition;
import java.awt.Component;
import javax.swing.JToolBar;

/**
 *
 * @author Philippe Charles
 */
@ServiceDefinition
public abstract class DemoComponentHandler {

    public boolean canHandle(Component c) {
        return true;
    }

    public void configure(Component c) {
    }

    public void fillToolBar(JToolBar toolBar, Component c) {
    }

    public abstract static class InstanceOf<T> extends DemoComponentHandler {

        private final Class<T> clazz;

        public InstanceOf(Class<T> clazz) {
            this.clazz = clazz;
        }

        public void doConfigure(T c) {
        }

        public void doFillToolBar(JToolBar toolBar, T c) {
        }

        @Override
        final public boolean canHandle(Component c) {
            return clazz.isInstance(c);
        }

        @Override
        final public void configure(Component c) {
            doConfigure(clazz.cast(c));
        }

        @Override
        final public void fillToolBar(JToolBar toolBar, Component c) {
            doFillToolBar(toolBar, clazz.cast(c));
        }
    }
}
