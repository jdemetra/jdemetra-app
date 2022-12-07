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
package demetra.desktop.sa.util;

import demetra.sa.SaProcessingFactory;
import demetra.sa.SaSpecification;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Jean Palate
 */
public class ActionsHelpers implements LookupListener {

    public static ActionsHelpers getInstance() {
        if (instance == null) {
            instance = new ActionsHelpers();
        }
        return instance;
    }

    private final Lookup.Result<ActionsHelper> actionsLookup;
    private final List<ActionsHelper> helpers = new ArrayList<>();
    private static ActionsHelpers instance;

    private ActionsHelpers() {
        actionsLookup = Lookup.getDefault().lookupResult(ActionsHelper.class);
        helpers.addAll(actionsLookup.allInstances());
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(actionsLookup)) {
            helpers.clear();
            helpers.addAll(actionsLookup.allInstances());
        }
    }

    public List<ActionsHelper> getHelpers() {
        return Collections.unmodifiableList(helpers);
    }

    public ActionsHelper getHelperFor(SaSpecification spec) {
        return helpers.stream().filter(h -> h.match(spec)).findFirst().orElse(null);
    }

    public ActionsHelper getHelperFor(SaProcessingFactory fac) {
        return helpers.stream().filter(h -> h.match(fac)).findFirst().orElse(null);
    }

    public static List<String> merge(List<String> l1, List<String> l2) {
        Set set = new HashSet<>(l1);
        ArrayList<String> all = new ArrayList<>(l1);
        for (String s : l2) {
            if (!set.contains(s)) {
                set.add(s);
                // could be softer. TODO
                all.add(s);
            }
        }
        return all;
    }

    public List<String> merge(Collection<SaProcessingFactory> factories, Function<ActionsHelper, List<String>> selector) {
        List<String> common = Collections.emptyList();
        Set<ActionsHelper> all = new LinkedHashSet<>();
        for (SaProcessingFactory fac : factories) {
            ActionsHelper helper = getHelperFor(fac);
            List<String> cur = selector.apply(helper);
            if (all.add(helper)) {
                if (common.isEmpty()) {
                    common = cur;
                } else {
                    common = merge(common, cur);
                }
            }
        }
        return common;
    }
}
