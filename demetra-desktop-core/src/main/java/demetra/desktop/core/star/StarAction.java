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
package demetra.desktop.core.star;

import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.star.StarList;
import demetra.tsprovider.DataSource;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;

import java.util.Optional;

@ActionID(category = "File", id = "demetra.desktop.core.star.StarAction")
@ActionRegistration(lazy = false, displayName = "#starAction.add")
@ActionReferences({
        @ActionReference(path = "Menu/File", position = 800, separatorBefore = 799)
})
@Messages({
        "starAction.add=Add star",
        "starAction.remove=Remove star"
})
public final class StarAction extends SingleNodeAction<Node> {

    public StarAction() {
        super(Node.class);
    }

    @Override
    protected boolean enable(Node activatedNode) {
        Optional<DataSource> dataSource = getDataSource(activatedNode);
        if (dataSource.isPresent()) {
            updateDisplayName(dataSource.get());
            return true;
        }
        return false;
    }

    @Override
    protected void performAction(Node activatedNode) {
        getDataSource(activatedNode).ifPresent(dataSource -> {
            StarList.getDefault().toggle(dataSource);
            updateDisplayName(dataSource);
        });
    }

    @Override
    public String getName() {
        return Bundle.starAction_add();
    }

    private void updateDisplayName(DataSource dataSource) {
        putValue(NAME, StarList.getDefault().isStarred(dataSource) ? Bundle.starAction_remove() : Bundle.starAction_add());
    }

    private static Optional<DataSource> getDataSource(Node node) {
        return Optional.ofNullable(node.getLookup().lookup(DataSource.class));
    }
}
