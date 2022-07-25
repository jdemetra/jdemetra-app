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
package demetra.desktop.benchmarking.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "ec.nbdemetra.benchmarking.actions.CloneMCholetteAction")
@ActionRegistration(
        displayName = "#CTL_CloneMCholetteAction")
@ActionReferences({
    @ActionReference(path = MultiCholetteDocumentManager.ITEMPATH, position = 1000, separatorAfter=1090)
})
@Messages("CTL_CloneMCholetteAction=Clone")
public final class CloneMCholetteAction implements ActionListener {

    private final WsNode context;

    public CloneMCholetteAction(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<MultiCholetteDocument> xdoc = context.getWorkspace().searchDocument(context.lookup(), MultiCholetteDocument.class);
        MultiCholetteDocumentManager mgr = WorkspaceFactory.getInstance().getManager(MultiCholetteDocumentManager.class);
        WorkspaceItem<MultiCholetteDocument> ndoc = WorkspaceItem.newItem(xdoc.getFamily(), mgr.getNextItemName(null), xdoc.getElement().clone());
        context.getWorkspace().add(ndoc);
    }
}