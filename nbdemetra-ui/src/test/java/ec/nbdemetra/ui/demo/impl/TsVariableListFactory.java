/*
 * Copyright 2015 National Bank of Belgium
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
package ec.nbdemetra.ui.demo.impl;

import com.google.common.collect.ImmutableMap;
import ec.nbdemetra.ui.demo.DemoComponentFactory;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.list.JTsVariableList;
import java.awt.Component;
import java.util.Map;
import java.util.concurrent.Callable;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = DemoComponentFactory.class)
public final class TsVariableListFactory extends DemoComponentFactory {

    @Override
    public Map<Id, Callable<Component>> getComponents() {
        Id id = new LinearId("(1) Main", "TsVariableList");
        Callable<Component> callable = () -> new JTsVariableList(new TsVariables());
        return ImmutableMap.of(id, callable);
    }
}
