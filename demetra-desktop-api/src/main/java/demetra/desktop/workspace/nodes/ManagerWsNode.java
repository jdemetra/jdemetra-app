/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace.nodes;

import demetra.desktop.workspace.Workspace;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.util.Id;
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

    public WorkspaceItemManager<?> getManager() {
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
