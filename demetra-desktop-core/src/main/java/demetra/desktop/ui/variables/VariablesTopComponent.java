/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.variables;

import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.ui.WorkspaceTopComponent;
import demetra.timeseries.regression.TsDataSuppliers;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
    dtd = "-//demetra.desktop.ui.variables//Variables//EN",
autostore = false)
@TopComponent.Description(
    preferredID = "VariablesTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "demetra.desktop.ui.variables.VariablesTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
    displayName = "#CTL_VariablesAction",
preferredID = "VariablesTopComponent")
@Messages({
    "CTL_VariablesAction=Variables",
    "CTL_VariablesTopComponent=Variables Window",
    "HINT_VariablesTopComponent=This is a Variables window"
})
public final class VariablesTopComponent extends TopComponent {
    
    private final WorkspaceItem<TsDataSuppliers> variables;
    
    private JTsVariableList list;

     private static VariablesDocumentManager manager() {
        return WorkspaceFactory.getInstance().getManager(VariablesDocumentManager.class);
    }

    public VariablesTopComponent() {
       this(manager().create(WorkspaceFactory.getInstance().getActiveWorkspace()));
    }

    public VariablesTopComponent(WorkspaceItem<TsDataSuppliers> doc) {
        variables=doc;
        initDocument();
    }

    private void initDocument() {
        initComponents();
        setToolTipText(NbBundle.getMessage(VariablesTopComponent.class, "HINT_VariablesTopComponent"));
        setName(variables.getDisplayName());
     }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
        list=new JTsVariableList(variables.getElement());
        add(list);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

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
    
}