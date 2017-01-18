/*
 * Copyright 2017 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
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
 * @author Mats Maggi
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
                WorkspaceItem<?> witem = WorkspaceItem.item(new LinearId(item.family.split(SEP)),
                        item.name, 
                        item.file,
                        item.comments);
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
                cur.comments = witem.getComments();
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
