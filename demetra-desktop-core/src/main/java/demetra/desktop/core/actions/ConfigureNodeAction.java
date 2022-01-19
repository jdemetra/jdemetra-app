/*
 * Copyright 2013 National Bank of Belgium
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
package demetra.desktop.core.actions;

import demetra.desktop.actions.AbilityNodeAction;
import demetra.desktop.actions.Configurable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Philippe Charles
 */
@ActionID(category = "File", id = ConfigureNodeAction.ID)
@ActionRegistration(displayName = "#ConfigureAction", lazy = false)
@NbBundle.Messages("ConfigureAction=Configure")
public final class ConfigureNodeAction extends AbilityNodeAction<Configurable> {

    public static final String ID = "demetra.desktop.core.actions.ConfigureNodeAction";

    public ConfigureNodeAction() {
        super(Configurable.class);
    }

    // FIXME
    static boolean isProvidersNode(Node[] activatedNodes) {
        return activatedNodes != null && activatedNodes.length == 0;
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return isProvidersNode(activatedNodes) || (activatedNodes.length == 1 && super.enable(activatedNodes));
    }

    @Override
    protected void performAction(Stream<Configurable> items) {
        configure(items.collect(Collectors.toList()));
    }

    @Override
    public String getName() {
        return Bundle.ConfigureAction();
    }

    private void configure(List<Configurable> list) {
        if (list.isEmpty()) {
            // FIXME: move DemetraUI from jd2 to core
//            DemetraUI.getDefault().configure();
        } else {
            list.forEach(Configurable::configure);
        }
    }
}
