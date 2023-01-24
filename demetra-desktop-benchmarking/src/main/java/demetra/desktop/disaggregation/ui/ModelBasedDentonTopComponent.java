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


import demetra.desktop.benchmarking.documents.DentonDocumentManager;
import demetra.desktop.disaggregation.documents.ModelBasedDentonDocumentManager;
import demetra.desktop.ui.processing.TsRegressionProcessingViewer;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.ui.WorkspaceTsRegressionTopComponent;
import jdplus.tempdisagg.univariate.ModelBasedDentonDocument;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
        preferredID = "ModelBasedDentonTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "demetra.desktop.disaggregation.ui.ModelBasedDentonTopComponent")
@ActionReference(path = "Menu/Statistical methods/Temporal Disaggregation", position = 1100)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ModelBasedDentonAction",
        preferredID = "ModelBasedDentonTopComponent")
@NbBundle.Messages({
    "CTL_ModelBasedDentonAction=Model-based Denton",
    "CTL_ModelBasedDentonTopComponent=Model-based Denton Window",
    "HINT_ModelBasedDentonTopComponent=This is a Model-based Denton window"
})
public final class ModelBasedDentonTopComponent extends WorkspaceTsRegressionTopComponent<ModelBasedDentonDocument> {

    private final ExplorerManager mgr = new ExplorerManager();

    private static ModelBasedDentonDocumentManager manager() {
        return WorkspaceFactory.getInstance().getManager(ModelBasedDentonDocumentManager.class);
    }

    public ModelBasedDentonTopComponent() {
        this(null);
    }

    public ModelBasedDentonTopComponent(WorkspaceItem<ModelBasedDentonDocument> doc) {
        super(doc);
        initComponents();
        setToolTipText(Bundle.CTL_ModelBasedDentonTopComponent());
    }

    @Override
    protected TsRegressionProcessingViewer initViewer() {
        //       node=new InternalNode();
        return TsRegressionProcessingViewer.create(this.getElement(), DocumentUIServices.forDocument(ModelBasedDentonDocument.class), true);
    }

    @Override
    public WorkspaceItem<ModelBasedDentonDocument> newDocument() {
        return manager().create(WorkspaceFactory.getInstance().getActiveWorkspace());
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>                        


    @Override
    protected String getContextPath() {
        return DentonDocumentManager.CONTEXTPATH; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

}
