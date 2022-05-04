/*
 * Copyright 2017 National Bank of Belgium
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
package demetra.desktop.x13.ui.actions;

import demetra.desktop.x13.documents.X13SpecManager;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.WsNode;
import demetra.desktop.x13.documents.X13DocumentManager;
import demetra.desktop.x13.ui.X13TopComponent;
import demetra.x13.X13Spec;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import jdplus.x13.X13Document;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "demetra.desktop.x13.ui.actions.CreateX13Doc")
@ActionRegistration(displayName = "#CTL_CreateX13Doc")
@ActionReferences({
    @ActionReference(path = X13SpecManager.ITEMPATH, position = 1620, separatorBefore = 1300)
})
@Messages("CTL_CreateX13Doc=Create Document")
public final class CreateX13Doc implements ActionListener {

    private final WsNode context;

    public CreateX13Doc(WsNode context) {
        this.context = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent ev) {
        final WorkspaceItem<X13Spec> xdoc = context.getWorkspace().searchDocument(context.lookup(), X13Spec.class);
        if (xdoc == null || xdoc.getElement() == null) {
            return;
        }
        X13DocumentManager dmgr = (X13DocumentManager) WorkspaceFactory.getInstance().getManager(X13DocumentManager.ID);
        WorkspaceItem<X13Document> doc = dmgr.create(context.getWorkspace());
        doc.setComments(xdoc.getComments());
        doc.getElement().set(xdoc.getElement());
        X13TopComponent view = new X13TopComponent(doc);
        view.open();
        view.requestActive();
    }
}

