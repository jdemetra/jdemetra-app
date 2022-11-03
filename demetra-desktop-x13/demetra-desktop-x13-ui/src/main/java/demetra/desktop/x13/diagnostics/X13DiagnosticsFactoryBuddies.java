/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.x13.diagnostics;

import demetra.sa.SaDiagnosticsFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Jean Palate
 */
public class X13DiagnosticsFactoryBuddies implements LookupListener {
    
    public static X13DiagnosticsFactoryBuddies getInstance() {
        if (instance == null) {
            instance = new X13DiagnosticsFactoryBuddies();
        }
        return instance;
    }
    
    private final Lookup.Result<X13DiagnosticsFactoryBuddy> outputLookup;
    private final List<X13DiagnosticsFactoryBuddy> outputs = new ArrayList<>();
    private static X13DiagnosticsFactoryBuddies instance;
    
    private X13DiagnosticsFactoryBuddies() {
        outputLookup = Lookup.getDefault().lookupResult(X13DiagnosticsFactoryBuddy.class);
        outputs.addAll(outputLookup.allInstances());
    }
    
    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(outputLookup)) {
            outputs.clear();
            outputs.addAll(outputLookup.allInstances());
        }
    }
    
    public List<X13DiagnosticsFactoryBuddy> getFactories() {
        return Collections.unmodifiableList(outputs);
    }
    
    public Node createNodeFor(SaDiagnosticsFactory factory) {
        for (X13DiagnosticsFactoryBuddy fac : outputs){
            Node node=fac.createNodeFor(factory);
            if (node != null)
                return node;
        }
        return Node.EMPTY;
    }
}
