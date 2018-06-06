/*
 * Copyright 2018 National Bank of Belgium
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
package internal.ui;

import demetra.tsprovider.Ts;
import demetra.ui.TsAction;
import demetra.ui.TsActionSpi;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.ns.INamedService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = TsAction.class)
public final class DefaultTsAction implements TsAction {

    @Override
    public List<? extends INamedService> getTsActions() {
        return lookupAll().collect(Collectors.toList());
    }

    @Override
    public void openWith(Ts ts, String name) {
        Objects.requireNonNull(ts);

        String target = name != null ? name : DemetraUI.getDefault().getTsActionName();

        Optional<? extends TsActionSpi> action = lookupAll().filter(o -> o.getName().equals(target)).findFirst();
        if (action.isPresent()) {
            failSafeOpen(action.get(), ts);
        } else {
            reportMissingAction(name);
        }
    }

    private void failSafeOpen(TsActionSpi action, Ts ts) {
        try {
            action.open(ts);
        } catch (RuntimeException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void reportMissingAction(String name) {
    }

    private Stream<? extends TsActionSpi> lookupAll() {
        return Lookup.getDefault().lookupAll(TsActionSpi.class).stream();
    }
}
