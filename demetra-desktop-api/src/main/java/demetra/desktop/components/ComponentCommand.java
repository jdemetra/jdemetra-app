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
package demetra.desktop.components;

import ec.util.various.swing.JCommand;

import java.awt.*;

/**
 *
 * @author Philippe Charles
 */
public abstract class ComponentCommand<C> extends JCommand<C> {

    private final String[] properties;

    public ComponentCommand(String... properties) {
        this.properties = properties;
    }

    @Override
    public JCommand.ActionAdapter toAction(C c) {
        JCommand.ActionAdapter result = super.toAction(c);
        return c instanceof Component ? result.withWeakPropertyChangeListener((Component) c, properties) : result;
    }
}
