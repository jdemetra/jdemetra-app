/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramoseats.ui;

import demetra.desktop.tramoseats.documents.TramoSeatsDocumentManager;
import demetra.desktop.ui.processing.TsProcessingViewer;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.ui.WorkspaceTsTopComponent;
import jdplus.tramoseats.TramoSeatsDocument;
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
@TopComponent.Description(
        preferredID = "TramoSeatsTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Seasonal Adjustment", id = "demetra.desktop.tramoseats.ui.TramoSeatsTopComponent")
@ActionReference(path = "Menu/Statistical methods/Seasonal Adjustment/Single Analysis", position = 1000)
@TopComponent.OpenActionRegistration(displayName = "#CTL_TramoSeatsAction")
@NbBundle.Messages({
    "CTL_TramoSeatsAction=TramoSeats",
    "CTL_TramoSeatsTopComponent=TramoSeats Window",
    "HINT_TramoSeatsTopComponent=This is a TramoSeats window"
})
public final class TramoSeatsTopComponent extends WorkspaceTsTopComponent<TramoSeatsDocument> {

    private final ExplorerManager mgr = new ExplorerManager();

    private static TramoSeatsDocumentManager manager() {
        return WorkspaceFactory.getInstance().getManager(TramoSeatsDocumentManager.class);
    }

    public TramoSeatsTopComponent() {
        this(null);
    }

    public TramoSeatsTopComponent(WorkspaceItem<TramoSeatsDocument> doc) {
        super(doc);
        initComponents();
        setToolTipText(NbBundle.getMessage(TramoSeatsTopComponent.class, "HINT_TramoSeatsTopComponent"));
        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    @Override
    public WorkspaceItem<TramoSeatsDocument> newDocument() {
        return manager().create(WorkspaceFactory.getInstance().getActiveWorkspace());
    }

    @Override
    protected TsProcessingViewer initViewer() {
        return TsProcessingViewer.create(getElement(), DocumentUIServices.forDocument(TramoSeatsDocument.class));
    }


    private void initComponents() {
        setLayout(new java.awt.BorderLayout());
    }

    @Override
    protected String getContextPath() {
        return TramoSeatsDocumentManager.CONTEXTPATH;
    }
}
