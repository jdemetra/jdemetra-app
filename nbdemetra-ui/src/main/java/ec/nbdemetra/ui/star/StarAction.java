/*
 * Copyright 2015 National Bank of Belgium
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
package ec.nbdemetra.ui.star;

import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ui.tsproviders.DataSourceNode;
import ec.tss.tsproviders.DataSource;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "File", id = "ec.nbdemetra.ui.star.StarAction")
@ActionRegistration(lazy = false, displayName = "#starAction.add")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 800, separatorBefore = 799)
})
@Messages({
    "starAction.add=Add star",
    "starAction.remove=Remove star"
})
public final class StarAction extends SingleNodeAction<DataSourceNode> {

    public StarAction() {
        super(DataSourceNode.class);
    }

    @Override
    protected boolean enable(DataSourceNode activatedNode) {
        updateDisplayName(activatedNode);
        return true;
    }

    @Override
    protected void performAction(DataSourceNode activatedNode) {
        StarList.getInstance().toggle(activatedNode.getLookup().lookup(DataSource.class));
        updateDisplayName(activatedNode);
    }

    @Override
    public String getName() {
        return Bundle.starAction_add();
    }

    private void updateDisplayName(DataSourceNode activatedNode) {
        putValue(NAME, StarList.getInstance().isStarred(activatedNode.getLookup().lookup(DataSource.class)) ? Bundle.starAction_remove() : Bundle.starAction_add());
    }
}
