/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.ui;

import demetra.desktop.ui.processing.DocumentUIServices;
import jdplus.tramo.TramoDocument;
import demetra.desktop.ui.processing.TsProcessingViewer;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.ui.WorkspaceTsTopComponent;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.NbBundle;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//demetra.desktop.tramo.ui//Tramo//EN",
        autostore = false)
@TopComponent.Description(preferredID = "TramoTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Modelling", id = "demetra.desktop.tramo.ui.TramoTopComponent")
@ActionReference(path = "Menu/Statistical methods/Modelling/Single Analysis", position = 1000)
@TopComponent.OpenActionRegistration(displayName = "#CTL_TramoAction")
@NbBundle.Messages({
    "CTL_TramoAction=Tramo",
    "CTL_TramoTopComponent=Tramo Window",
    "HINT_TramoTopComponent=This is a Tramo window"
})
public final class TramoTopComponent extends WorkspaceTsTopComponent<TramoDocument> {

    private final ExplorerManager mgr = new ExplorerManager();

    private static TramoDocumentManager manager() {
        return WorkspaceFactory.getInstance().getManager(TramoDocumentManager.class);
    }

    public TramoTopComponent() {
        this(manager().create(WorkspaceFactory.getInstance().getActiveWorkspace()));
    }

    public TramoTopComponent(WorkspaceItem<TramoDocument> doc) {
        super(doc);
        initDocument();
        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    private void initDocument() {
        initComponents();
        setToolTipText(NbBundle.getMessage(TramoTopComponent.class, "HINT_TramoTopComponent"));
        setName(getDocument().getDisplayName());
        panel = TsProcessingViewer.create(getDocument().getElement(), DocumentUIServices.forDocument(TramoDocument.class));
        this.add(panel);
        panel.refreshHeader();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

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
        return TramoDocumentManager.CONTEXTPATH;
    }
}
