/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tools;

import demetra.ui.nodes.ControlNode;
import demetra.ui.components.JTsTable;
import java.awt.BorderLayout;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//ec.nbdemetra.ui.tools//List//EN",
        autostore = false)
@TopComponent.Description(preferredID = "ListTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "tsnavigator", openAtStartup = false)
@ActionID(category = "Window", id = "ec.nbdemetra.ui.tools.ListTopComponent")
@ActionReference(path = "Menu/Tools/Container", position = 400)
@TopComponent.OpenActionRegistration(displayName = "#CTL_ListAction")
@Messages({
    "CTL_ListAction=List",
    "CTL_ListTopComponent=List",
    "HINT_ListTopComponent=This is a List window"
})
public final class ListTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager mgr = new ExplorerManager();

    public ListTopComponent() {
        initComponents();
        setName(Bundle.CTL_ListTopComponent());
        setToolTipText(Bundle.HINT_ListTopComponent());
        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));
        add(new JTsTable(), BorderLayout.CENTER);
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

    @Override
    public void open() {
        super.open();
        WindowManager.getDefault().getModes();
        Mode mode = WindowManager.getDefault().findMode("tsnavigator");
        if (mode != null) {
            mode.dockInto(this);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        ControlNode.onComponentOpened(mgr, getList());
    }

    @Override
    public void componentClosed() {
        mgr.setRootContext(Node.EMPTY);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        ToolsPersistence.writeTsCollection(getList(), p);
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        ToolsPersistence.readTsCollection(getList(), p);
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    public JTsTable getList() {
        return (JTsTable) getComponent(0);
    }
}
