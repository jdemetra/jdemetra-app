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
package demetra.desktop.disaggregation.ui;

import demetra.desktop.disaggregation.documents.TemporalDisaggregationDocumentManager;
import demetra.desktop.ui.processing.TsRegressionProcessingViewer;
import demetra.desktop.util.NbUtilities;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.ui.WorkspaceTsRegressionTopComponent;
import jdplus.tempdisagg.univariate.TemporalDisaggregationDocument;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//demetra.desktop.disaggregation.ui//TemporalDisaggregationDocument//EN",
        autostore = false)
@TopComponent.Description(preferredID = "TemporalDisaggregationDocumentTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Temporal Disaggregation", id = "demetra.desktop.disaggregation.ui.TemporalDisaggregationDocumentTopComponent")
@ActionReference(path = "Menu/Statistical methods/Temporal Disaggregation", position = 1000)
@TopComponent.OpenActionRegistration(displayName = "#CTL_TemporalDisaggregationDocumentAction")
@NbBundle.Messages({
    "CTL_TemporalDisaggregationDocumentAction=Regression Model",
    "CTL_TemporalDisaggregationDocumentTopComponent=Regression Model Window",
    "HINT_TemporalDisaggregationDocumentTopComponent=This is a Regression Model window"
})
public final class TemporalDisaggregationDocumentTopComponent extends WorkspaceTsRegressionTopComponent<TemporalDisaggregationDocument> {

    private final ExplorerManager mgr = new ExplorerManager();
    protected TsRegressionProcessingViewer panel;

    private static TemporalDisaggregationDocumentManager manager() {
        return WorkspaceFactory.getInstance().getManager(TemporalDisaggregationDocumentManager.class);
    }

    public TemporalDisaggregationDocumentTopComponent() {
        this(null);
    }

    public TemporalDisaggregationDocumentTopComponent(WorkspaceItem<TemporalDisaggregationDocument> doc) {
        super(doc);
        initComponents();
        setToolTipText(Bundle.HINT_TemporalDisaggregationDocumentTopComponent());
      associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));
      }

    @Override
    public void refresh() {
        panel.onDocumentChanged();
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    @Override
    public WorkspaceItem<TemporalDisaggregationDocument> newDocument() {
        return manager().create(WorkspaceFactory.getInstance().getActiveWorkspace());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setTopComponent(jSplitPane2);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    // End of variables declaration//GEN-END:variables

    @Override
    public TsRegressionProcessingViewer initViewer() {
        return TsRegressionProcessingViewer.create(getElement(), DocumentUIServices.forDocument(TemporalDisaggregationDocument.class), false);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String getContextPath() {
        return TemporalDisaggregationDocumentManager.CONTEXTPATH;
    }

    public void editNote() {
        TemporalDisaggregationDocument element = getDocument().getElement();
        NbUtilities.editNote(element);
    }
}
