/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.ui.processing;

import demetra.desktop.components.parts.HasTs;
import java.beans.PropertyVetoException;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;

/**
 *
 * @author PALATEJ
 */
public abstract class TsTopComponent extends TopComponent implements HasTs, ExplorerManager.Provider {

    protected final ExplorerManager mgr = new ExplorerManager();

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    protected TsTopComponent() {
    }

    protected Node internalNode() {
        return null;
    }

    @Override
    protected void componentOpened() {
        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));
        Node node = internalNode();
        if (node != null) {
            try {
                mgr.setRootContext(node);
                mgr.setSelectedNodes(new Node[]{node});
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    protected void componentClosed() {
        mgr.setRootContext(Node.EMPTY);
    }

//    @Override
//    public boolean fill(JMenu menu) {
//        return true;
//    }
//
//    @Override
//    public void componentActivated(){
//        ActiveViewManager.getInstance().set(this);
//    }
//    
//    @Override
//    public void componentDeactivated(){
//        ActiveViewManager.getInstance().set(null);
//    }
}
