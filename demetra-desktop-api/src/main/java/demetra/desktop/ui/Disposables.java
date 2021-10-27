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
package demetra.desktop.ui;

import demetra.desktop.interfaces.Disposable;
import java.awt.Component;
import java.awt.Container;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class Disposables {

    public <C extends Container> C disposeAndRemoveAll(C c) {
        disposeAll(c.getComponents());
        c.removeAll();
        return c;
    }

    public void disposeAll(Component... list) {
        for (Component o : list) {
            dispose(o);
        }
    }

    public void dispose(Component c) {
        if (c instanceof Disposable) {
            ((Disposable) c).dispose();
        }
    }
}
