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

import com.google.common.collect.ImmutableMap;
import ec.tstoolkit.design.ServiceDefinition;
import ec.tstoolkit.utilities.Id;
import java.awt.Component;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * @author Philippe Charles
 */
@ServiceDefinition
public abstract class DemoComponentFactory {

    abstract public Map<Id, Callable<Component>> getComponents();

    protected static ImmutableMap.Builder<Id, Callable<Component>> builder() {
        return new ImmutableMap.Builder<>();
    }

    public static String idOf(String label, int order, boolean highlight) {
        return "<html><span class='" + order + "'>" + (highlight ? "<u>" : "") + label;
    }
}
