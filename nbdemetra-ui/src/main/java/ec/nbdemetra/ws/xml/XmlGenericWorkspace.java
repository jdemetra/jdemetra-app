/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.xml;

import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tstoolkit.utilities.DefaultIdAggregator;
import ec.tstoolkit.utilities.LinearId;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Jean Palate
 */
@XmlRootElement(name = XmlGenericWorkspace.RNAME)
@XmlType(name = XmlGenericWorkspace.NAME)
public class XmlGenericWorkspace {

    static final String NAME = "demetraGenericWorkspaceType";
    static final String RNAME = "demetraGenericWorkspace";
    @XmlAttribute
    public String name;
    @XmlElementWrapper()
    @XmlElement(name = "item")
    public XmlWorkspaceItem[] items;
    private static final String SEP = "@";

    public boolean to(Workspace ws) {
        ws.setName(name);
        if (items != null) {
            for (XmlWorkspaceItem item : items) {
                WorkspaceItem<?> witem = WorkspaceItem.item(new LinearId(item.family.split(SEP)), item.name, item.file);
                ws.quietAdd(witem);
                IWorkspaceItemManager<?> manager = WorkspaceFactory.getInstance().getManager(witem.getFamily());
                if (manager != null && manager.isAutoLoad())
                {
                    witem.load();
                }
            }
        }
        return true;
    }

    public boolean from(Workspace ws) {
        name = ws.getName();
        List<WorkspaceItem<?>> witems = ws.getItems();
        if (witems.isEmpty()) {
            return true;
        }
        ArrayList<XmlWorkspaceItem> xitems = new ArrayList<>();
        DefaultIdAggregator agg = new DefaultIdAggregator(SEP.charAt(0));
        for (WorkspaceItem<?> witem : witems) {
            if (witem.getStatus() != WorkspaceItem.Status.System) {
                XmlWorkspaceItem cur = new XmlWorkspaceItem();
                cur.family = agg.aggregate(witem.getFamily());
                cur.file = witem.getIdentifier();
                cur.name = witem.getDisplayName();
                xitems.add(cur);
            }
        }
        if (!xitems.isEmpty()) {
            items = new XmlWorkspaceItem[xitems.size()];
            xitems.toArray(items);
        }
        return true;
    }
}
