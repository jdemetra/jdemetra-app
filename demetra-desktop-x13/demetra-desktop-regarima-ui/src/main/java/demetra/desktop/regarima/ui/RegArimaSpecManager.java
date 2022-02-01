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
package demetra.desktop.regarima.ui;

import demetra.desktop.regarima.descriptors.RegArimaSpecUI;
import demetra.desktop.regarima.ui.actions.EditRegArimaSpec;
import demetra.desktop.ui.properties.l2fprod.PropertiesDialog;
import demetra.desktop.workspace.AbstractWorkspaceItemManager;
import demetra.desktop.workspace.Workspace;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.regarima.RegArimaSpec;
import demetra.util.Id;
import demetra.util.LinearId;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import jdplus.x13.regarima.RegArimaDocument;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

/**
 *
 * @author Jean Palate
 * @author Mats Maggi
 */
@ServiceProvider(service = WorkspaceItemManager.class,
        position = 10)
public class RegArimaSpecManager extends AbstractWorkspaceItemManager<RegArimaSpec> {

    public static final LinearId ID = new LinearId(RegArimaSpec.FAMILY, "specifications", RegArimaSpec.METHOD);
    public static final String PATH = "regarima.spec";
    public static final String ITEMPATH = "regarima.spec.item";

    @Override
    protected String getItemPrefix() {
        return "RegarimaSpec";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public RegArimaSpec createNewObject() {
        return RegArimaSpec.RG4;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.Spec;
    }

    @Override
    public String getActionsPath() {
        return PATH;
    }

    @Override
    public Status getStatus() {
        return Status.Certified;
    }

    @Override
    public Action getPreferredItemAction(Id child) {
        ItemWsNode tmp = new ItemWsNode(WorkspaceFactory.getInstance().getActiveWorkspace(), child);
        final EditRegArimaSpec obj = new EditRegArimaSpec(tmp);
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obj.actionPerformed(e);
            }
        };
    }

    @Override
    public List<WorkspaceItem<RegArimaSpec>> getDefaultItems() {
        ArrayList<WorkspaceItem<RegArimaSpec>> defspecs = new ArrayList<>();
        defspecs.add(WorkspaceItem.system(ID, "RG0", RegArimaSpec.RG0));
        defspecs.add(WorkspaceItem.system(ID, "RG1", RegArimaSpec.RG1));
        defspecs.add(WorkspaceItem.system(ID, "RG2", RegArimaSpec.RG2));
        defspecs.add(WorkspaceItem.system(ID, "RG3", RegArimaSpec.RG3));
        defspecs.add(WorkspaceItem.system(ID, "RG4", RegArimaSpec.RG4));
        defspecs.add(WorkspaceItem.system(ID, "RG5", RegArimaSpec.RG5));
        return defspecs;
    }

    public void createDocument(final Workspace ws, final WorkspaceItem<RegArimaSpec> xdoc) {
        RegArimaDocumentManager dmgr = (RegArimaDocumentManager) WorkspaceFactory.getInstance().getManager(RegArimaDocumentManager.ID);
        WorkspaceItem<RegArimaDocument> doc = dmgr.create(ws);
        doc.setComments(xdoc.getComments());
        doc.getElement().set(xdoc.getElement());
        RegArimaTopComponent view = new RegArimaTopComponent(doc);
        view.open();
        view.requestActive();
    }

    public void edit(final WorkspaceItem<RegArimaSpec> xdoc) {
        if (xdoc == null || xdoc.getElement() == null) {
            return;

        }
        final RegArimaSpecUI ui = new RegArimaSpecUI(xdoc.getElement(), xdoc.isReadOnly());
        PropertiesDialog propDialog =
                new PropertiesDialog(WindowManager.getDefault().getMainWindow(), true, ui,
                new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                xdoc.setElement(ui.getCore());
            }
        });
        propDialog.setTitle(xdoc.getDisplayName());
        propDialog.setVisible(true);
    }

    @Override
    public Class getItemClass() {
        return RegArimaSpec.class;
    }
}
