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
package ec.nbdemetra.ui.actions;

import com.google.common.collect.Lists;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.tsproviders.ProvidersNode;
import java.util.List;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = "ec.nbdemetra.ui.actions.ConfigureAction")
@ActionRegistration(displayName = "#ConfigureAction", lazy = false)
@NbBundle.Messages("ConfigureAction=Configure")
public class ConfigureAction extends AbilityAction<IConfigurable> {

    public ConfigureAction() {
        super(IConfigurable.class);
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return ProvidersNode.isProvidersNode(activatedNodes) || (activatedNodes.length == 1 && super.enable(activatedNodes));
    }

    @Override
    protected void performAction(Iterable<IConfigurable> items) {
        configure(Lists.newArrayList(items));
    }

    @Override
    public String getName() {
        return Bundle.ConfigureAction();
    }

    private void configure(List<IConfigurable> list) {
        if (list.isEmpty()) {
            configure(DemetraUI.getDefault());
        } else {
            for (IConfigurable o : list) {
                configure(o);
            }
        }
    }

    private void configure(IConfigurable o) {
        o.setConfig(o.editConfig(o.getConfig()));
    }
}
