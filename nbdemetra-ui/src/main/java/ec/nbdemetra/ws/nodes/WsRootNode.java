/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.nodes;

import ec.nbdemetra.ui.NbUtilities;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ws.Workspace;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;

/**
 *
 * @author pcuser
 */
public class WsRootNode extends AbstractNode {

    Workspace ws_;

    public WsRootNode(Workspace ws) {
        super(Children.create(new WsRootChildFactory(ws), false));
        ws_ = ws;
    }

    @Override
    public String getDisplayName() {
        return ws_.getName();

    }

    @Override
    public Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set identification = Sheet.createPropertiesSet();
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Active workspace");
        b.with(String.class).select("Name", ws_.getName()).add();
        sheet.put(b.build());
        sheet.put(NbUtilities.creatDataSourcePropertiesSet(ws_.getDataSource()));
        return sheet;
    }
}
