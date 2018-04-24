/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.nodes;

import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.tstoolkit.utilities.Id;
import java.awt.Image;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Philippe Charles
 */
public class ManagerWsNode extends WsNode {


    public ManagerWsNode(Workspace ws, Id id) {
        super(createFinalItems(ws, id), ws, id);
    }

    public IWorkspaceItemManager<?> getManager() {
        return WorkspaceFactory.getInstance().getManager(lookup());
    }

    @Override
    public Image getIcon(int type) {
        Icon result = getManager().getManagerIcon();
        return result != null ? ImageUtilities.icon2Image(result) : super.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        Icon result = getManager().getManagerIcon();
        return result != null ? ImageUtilities.icon2Image(result) : super.getOpenedIcon(type);
    }   
}
