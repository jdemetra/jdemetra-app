/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Jean Palate
 */
public class SaReportManager implements LookupListener {

    private static final SaReportManager instance_ = new SaReportManager();
    private Lookup.Result<ISaReportFactory> lookup;
    private final ArrayList<ISaReportFactory> factories = new ArrayList<>();

    public static SaReportManager getInstance() {
        return instance_;
    }

    public SaReportManager() {
        lookup = Lookup.getDefault().lookupResult(ISaReportFactory.class);
        factories.addAll(lookup.allInstances());
    }
    
    public List<ISaReportFactory> getFactories(){
        return Collections.unmodifiableList(factories);
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(lookup)) {
            factories.clear();
            factories.addAll(lookup.allInstances());
        }
    }
}
