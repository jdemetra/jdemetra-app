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
package ec.nbdemetra.sa.revisionanalysis;

import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.sa.revisions.RevisionAnalysisDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Action creating a new Revision Analysis window in the workspace
 *
 * @author Mats Maggi
 */
//@ActionID(category = "Window", id = "ec.nbdemetra.sa.revisionanalysis.RevisionAnalysisNewDocument")
//@ActionRegistration(displayName = "#CTL_RevisionAnalysisNewDocument")
//@ActionReference(path = "Menu/Statistical methods/Seasonal Adjustment/Tools", position = 360)
//@Messages("CTL_RevisionAnalysisNewDocument=Revision Analysis")
public final class RevisionAnalysisNewDocument implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        RevisionAnalysisDocumentManager mgr = WorkspaceFactory.getInstance().getManager(RevisionAnalysisDocumentManager.class);
        if (mgr != null) {
            Workspace ws = WorkspaceFactory.getInstance().getActiveWorkspace();
            WorkspaceItem<RevisionAnalysisDocument> ndoc = mgr.create(ws);
            mgr.openDocument(ndoc);
        }
    }

}
