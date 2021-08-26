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
package ec.nbdemetra.sa.output;

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
public class OutputManager implements LookupListener {

    public static OutputManager getInstance() {
        if (instance == null) {
            instance = new OutputManager();
        }
        return instance;
    }
    
    private final Lookup.Result<INbOutputFactory> outputLookup;
    private final List<INbOutputFactory> outputs = new ArrayList<>();
    private static OutputManager instance;

    private OutputManager() {
        outputLookup = Lookup.getDefault().lookupResult(INbOutputFactory.class);
        outputs.addAll(outputLookup.allInstances());
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(outputLookup)) {
            outputs.clear();
            outputs.addAll(outputLookup.allInstances());
        }
    }
    
    public List<INbOutputFactory> getFactories(){
        return Collections.unmodifiableList(outputs);
    }

    Node createNodeFor(Object properties) {
        for (INbOutputFactory fac : outputs){
            Node node=fac.createNodeFor(properties);
            if (node != null)
                return node;
        }
        return Node.EMPTY;
    }
    
}
