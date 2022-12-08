/*
 * Copyright 2016 National Bank of Belgium
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
package demetra.desktop.sa.output;

import demetra.sa.SaOutputFactory;
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
public class OutputFactoryBuddies implements LookupListener {
    
    public static OutputFactoryBuddies getInstance() {
        if (instance == null) {
            instance = new OutputFactoryBuddies();
        }
        return instance;
    }
    
    private final Lookup.Result<OutputFactoryBuddy> outputLookup;
    private final List<OutputFactoryBuddy> outputs = new ArrayList<>();
    private static OutputFactoryBuddies instance;
    
    private OutputFactoryBuddies() {
        outputLookup = Lookup.getDefault().lookupResult(OutputFactoryBuddy.class);
        outputs.addAll(outputLookup.allInstances());
    }
    
    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(outputLookup)) {
            outputs.clear();
            outputs.addAll(outputLookup.allInstances());
        }
    }
    
    public List<OutputFactoryBuddy> getFactories() {
        return Collections.unmodifiableList(outputs);
    }
    
    Node createNodeFor(SaOutputFactory factory) {
        for (OutputFactoryBuddy fac : outputs){
            Node node=fac.createNodeFor(factory.getConfiguration());
            if (node != null)
                return node;
        }
        return Node.EMPTY;
    }
}
