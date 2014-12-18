/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    
    private Lookup.Result<INbOutputFactory> outputLookup;
    private List<INbOutputFactory> outputs = new ArrayList<>();
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
