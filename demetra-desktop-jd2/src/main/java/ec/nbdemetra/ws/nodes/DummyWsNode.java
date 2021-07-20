/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.nodes;

import ec.nbdemetra.ws.Workspace;
import ec.tstoolkit.utilities.Id;
import java.awt.Image;

/**
 *
 * @author Philippe Charles
 */
public class DummyWsNode extends WsNode {

    public DummyWsNode(Workspace ws, Id id) {
        super(createItems(ws, id), ws, id);
    }

    @Override
    public Image getIcon(int type) {
        return super.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return super.getOpenedIcon(type);
    }
}
