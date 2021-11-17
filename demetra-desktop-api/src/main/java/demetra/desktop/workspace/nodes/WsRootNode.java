/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace.nodes;

import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.util.NbUtilities;
import demetra.desktop.workspace.Workspace;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;

/**
 *
 * @author Jean Palate
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
        b.with(String.class).selectConst("Name", ws_.getName()).add();
        sheet.put(b.build());
        sheet.put(NbUtilities.creatDataSourcePropertiesSet(ws_.getDataSource()));
        return sheet;
    }
}
