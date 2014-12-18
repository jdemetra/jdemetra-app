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
package ec.nbdemetra.anomalydetection.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Mats Maggi
 */
public class CheckLastReportManager implements LookupListener {

    private static final CheckLastReportManager instance_ = new CheckLastReportManager();
    private Lookup.Result<ICheckLastReportFactory> lookup;
    private final ArrayList<ICheckLastReportFactory> factories = new ArrayList<>();

    public static CheckLastReportManager getInstance() {
        return instance_;
    }

    public CheckLastReportManager() {
        lookup = Lookup.getDefault().lookupResult(ICheckLastReportFactory.class);
        factories.addAll(lookup.allInstances());
    }
    
    public List<ICheckLastReportFactory> getFactories(){
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
