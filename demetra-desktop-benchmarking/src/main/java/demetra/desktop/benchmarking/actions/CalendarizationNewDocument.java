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
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Window", id = "ec.nbdemetra.benchmarking.actions.CalendarizationNewDocument")
@ActionRegistration(displayName = "#CTL_CalendarizationNewDocument")
@ActionReference(path = "Menu/Statistical methods/Benchmarking", position = 4000, separatorBefore = 3999)
@Messages("CTL_CalendarizationNewDocument=Calendarization")
public final class CalendarizationNewDocument implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        CalendarizationDocumentManager mgr = WorkspaceFactory.getInstance().getManager(CalendarizationDocumentManager.class);
        if (mgr != null) {
            Workspace ws = WorkspaceFactory.getInstance().getActiveWorkspace();
            WorkspaceItem<CalendarizationDocument> ndoc = mgr.create(ws);
            mgr.openDocument(ndoc);
        }
    }
}
