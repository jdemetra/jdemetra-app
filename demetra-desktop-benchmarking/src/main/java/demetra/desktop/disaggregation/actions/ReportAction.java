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
package demetra.desktop.disaggregation.actions;

import demetra.desktop.disaggregation.documents.DefaultReportFactory;
import demetra.desktop.disaggregation.documents.TemporalDisaggregationDocumentManager;
import demetra.desktop.workspace.Workspace;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.desktop.workspace.nodes.WsNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;
import jdplus.tempdisagg.univariate.TemporalDisaggregationDocument;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "demetra.desktop.disaggregation.actions.ReportAction")
@ActionRegistration(
        displayName = "#CTL_ReportAction")
@ActionReferences({
    @ActionReference(path = TemporalDisaggregationDocumentManager.PATH, position = 1900),
    @ActionReference(path = "Shortcuts", name = "R")})
@Messages("CTL_ReportAction=CreateReport")
public final class ReportAction implements ActionListener {

    private final WsNode context;

    public ReportAction(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItemManager mgr = WorkspaceFactory.getInstance().getManager(context.lookup());
        if (mgr != null) {
            Workspace ws = context.getWorkspace();
            List<WorkspaceItem<TemporalDisaggregationDocument>> docs = ws.searchDocuments(TemporalDisaggregationDocument.class);
            LinkedHashMap<String, TemporalDisaggregationDocument> map=new LinkedHashMap<>();
            for (WorkspaceItem<TemporalDisaggregationDocument> doc : docs){
                map.put(doc.getDisplayName(), doc.getElement());
            }
            new DefaultReportFactory().createReport(map);
         }
    }
}