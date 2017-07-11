/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.nodes;

import com.google.common.base.Strings;
import ec.nbdemetra.ui.NbUtilities;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import ec.tstoolkit.IDocumented;
import ec.tstoolkit.MetaData;
import ec.tstoolkit.utilities.Id;
import java.awt.Image;
import javax.swing.Action;
import javax.swing.Icon;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Philippe Charles
 */
public class ItemWsNode extends WsNode {

    public static boolean isItem(Workspace ws, Id id) {
        return ws.searchDocument(id) != null;
    }

    public ItemWsNode(Workspace ws, Id id) {
        super(Children.LEAF, ws, id);
    }

    public WorkspaceItem<?> getItem() {
        return workspace_.searchDocument(lookup());
    }

    public <T> WorkspaceItem<T> getItem(Class<T> tclass) {
        return workspace_.searchDocument(lookup(), tclass);
    }

    @Override
    public String getDisplayName() {
        WorkspaceItem<?> item = getItem();
        return item == null ? "" : item.getDisplayName();
    }

    @Override
    public String getShortDescription() {
        WorkspaceItem<?> item = getItem();
        if (!Strings.isNullOrEmpty(item.getComments())) {
            return MultiLineNameUtil.toHtml(item.getComments());
        } else {
            return null;
        }
    }

    @Override
    public boolean canDestroy() {
        WorkspaceItem<?> item = getItem();
        return item != null && !item.isReadOnly();
    }

    @Override
    public void destroy() {
        WorkspaceItem<?> item = getItem();
        if (item != null) {
            workspace_.remove(item);
        }
    }

    @Override
    public Image getIcon(int type) {
        IWorkspaceItemManager manager = WorkspaceFactory.getInstance().getManager(lookup().parent());
        Icon result = manager.getItemIcon(getItem());
        return result != null ? ImageUtilities.icon2Image(result) : super.getIcon(type);
    }

    @Override
    public Action getPreferredAction() {
        IWorkspaceItemManager<?> manager = WorkspaceFactory.getInstance().getManager(lookup().parent());
        return manager != null ? manager.getPreferredItemAction(lookup()) : null;
    }

    @Override
    public Sheet createSheet() {
        Sheet sheet = super.createSheet();
        WorkspaceItem<IDocumented> doc = workspace_.searchDocument(lookup(), IDocumented.class);
        if (doc == null) {
            return sheet;
        }
        final MetaData metaData = doc.getElement().getMetaData();
        if (MetaData.isNullOrEmpty(metaData)) {
            return sheet;
        }

        Sheet.Set info = NbUtilities.createMetadataPropertiesSet(metaData);
        sheet.put(info);
        return sheet;
    }
}
