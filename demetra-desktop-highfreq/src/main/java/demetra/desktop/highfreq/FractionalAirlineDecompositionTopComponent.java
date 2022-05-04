/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.highfreq;

import demetra.desktop.workspace.DocumentUIServices;
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
@ConvertAsProperties(dtd = "-//demetra.desktop.highfreq//FractionalAirlineDecomposition//EN",
        autostore = false)
@TopComponent.Description(preferredID = "FractionalAirlineDecompositionTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Seasonal Adjustment", id = "demetra.desktop.highfreq.FractionalAirlineDecompositionTopComponent")
@ActionReference(path = "Menu/Statistical methods/Seasonal Adjustment/Single Analysis", position = 2000)
@TopComponent.OpenActionRegistration(displayName = "#CTL_FractionalAirlineDecompositionAction")
@NbBundle.Messages({
    "CTL_FractionalAirlineDecompositionAction=Fractional airline decomposition",
    "CTL_FractionalAirlineDecompositionTopComponent=Fractional Airline Decomposition Window",
    "HINT_FractionalAirlineDecompositionTopComponent=This is a Fractional Airline Decomposition window"
})
public final class FractionalAirlineDecompositionTopComponent extends WorkspaceTsTopComponent<FractionalAirlineDecompositionDocument> {

    private final ExplorerManager mgr = new ExplorerManager();

    private static FractionalAirlineDecompositionDocumentManager manager() {
        return WorkspaceFactory.getInstance().getManager(FractionalAirlineDecompositionDocumentManager.class);
    }

    public FractionalAirlineDecompositionTopComponent() {
        this(manager().create(WorkspaceFactory.getInstance().getActiveWorkspace()));
    }

    public FractionalAirlineDecompositionTopComponent(WorkspaceItem<FractionalAirlineDecompositionDocument> doc) {
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
        setToolTipText(NbBundle.getMessage(FractionalAirlineDecompositionTopComponent.class, "HINT_FractionalAirlineDecompositionTopComponent"));
        setName(getDocument().getDisplayName());
        panel = TsProcessingViewer.create(getDocument().getElement(), DocumentUIServices.forDocument(FractionalAirlineDecompositionDocument.class));
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
        return FractionalAirlineDecompositionDocumentManager.CONTEXTPATH;
    }
}
