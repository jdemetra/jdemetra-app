/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.ui.processing;

import demetra.desktop.TsDynamicProvider;
import demetra.desktop.TsManager;
import demetra.desktop.components.parts.HasTs;
import demetra.desktop.ui.ActiveView;
import demetra.desktop.ui.ActiveViewManager;
import demetra.desktop.ui.properties.l2fprod.UserInterfaceContext;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.timeseries.Ts;
import demetra.timeseries.TsInformationType;
import java.util.Collection;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;

/**
 *
 * @author PALATEJ
 */
public abstract class TsTopComponent extends TopComponent implements HasTs, ActiveView, ExplorerManager.Provider{

    @Override
    public ExplorerManager getExplorerManager() {
        return ActiveViewManager.getInstance().getExplorerManager();
    }
    

    protected TsTopComponent() {
    }

    @Override
    public boolean fill(JMenu menu) {
        return true;
    }

    @Override
    public void componentActivated(){
        ActiveViewManager.getInstance().set(this);
    }
    
    @Override
    public void componentDeactivated(){
        ActiveViewManager.getInstance().set(null);
    }
}
