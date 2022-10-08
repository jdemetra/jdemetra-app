/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.sts.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "be.nbb.nbdemetra.sts.OpenStsDoc")
@ActionRegistration(displayName = "#CTL_OpenStsDoc")
@ActionReferences({
    @ActionReference(path = StsDocumentManager.ITEMPATH, position = 1600, separatorBefore = 1300)
 })
@NbBundle.Messages("CTL_OpenStsDoc=Open")
public class OpenStsDoc implements ActionListener {

    private final WsNode context;

    public OpenStsDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<StsDocument> doc = context.getWorkspace().searchDocument(context.lookup(), StsDocument.class);
        StsDocumentManager manager = WorkspaceFactory.getInstance().getManager(StsDocumentManager.class);
        manager.openDocument(doc);
    }
}
