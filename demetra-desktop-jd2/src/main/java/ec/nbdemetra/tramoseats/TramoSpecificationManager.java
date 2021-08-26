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
package ec.nbdemetra.tramoseats;

import ec.nbdemetra.tramoseats.actions.EditTramoSpec;
import ec.nbdemetra.tramoseats.descriptors.TramoSpecUI;
import ec.nbdemetra.ui.properties.l2fprod.PropertiesDialog;
import ec.nbdemetra.ws.*;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.tss.modelling.documents.TramoDocument;
import ec.tstoolkit.algorithm.implementation.TramoProcessingFactory;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

/**
 *
 * @author Jean Palate
 * @author Mats Maggi
 */
@ServiceProvider(service = IWorkspaceItemManager.class,
        position = 10)
public class TramoSpecificationManager extends AbstractWorkspaceItemManager<TramoSpecification> {

    public static final LinearId ID = new LinearId(TramoProcessingFactory.DESCRIPTOR.family, "specifications", TramoProcessingFactory.DESCRIPTOR.name);
    public static final String PATH = "tramo.spec";
    public static final String ITEMPATH = "tramo.spec.item";

    @Override
    protected String getItemPrefix() {
        return "TramoSpec";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected TramoSpecification createNewObject() {
        return TramoSpecification.TR5.clone();
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
        final EditTramoSpec obj = new EditTramoSpec(tmp);
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obj.actionPerformed(e);
            }
        };
    }

    @Override
    public List<WorkspaceItem<TramoSpecification>> getDefaultItems() {
        ArrayList<WorkspaceItem<TramoSpecification>> defspecs = new ArrayList<>();
        defspecs.add(WorkspaceItem.system(ID, "TR0", TramoSpecification.TR0));
        defspecs.add(WorkspaceItem.system(ID, "TR1", TramoSpecification.TR1));
        defspecs.add(WorkspaceItem.system(ID, "TR2", TramoSpecification.TR2));
        defspecs.add(WorkspaceItem.system(ID, "TR3", TramoSpecification.TR3));
        defspecs.add(WorkspaceItem.system(ID, "TR4", TramoSpecification.TR4));
        defspecs.add(WorkspaceItem.system(ID, "TR5", TramoSpecification.TR5));
        defspecs.add(WorkspaceItem.system(ID, "TRfull", TramoSpecification.TRfull));
        return defspecs;
    }

    public void createDocument(final Workspace ws, final WorkspaceItem<TramoSpecification> xdoc) {
        TramoDocumentManager dmgr = (TramoDocumentManager) WorkspaceFactory.getInstance().getManager(TramoDocumentManager.ID);
        WorkspaceItem<TramoDocument> doc = dmgr.create(ws);
        doc.setComments(xdoc.getComments());
        doc.getElement().setSpecification(xdoc.getElement());
        TramoTopComponent view = new TramoTopComponent(doc);
        view.open();
        view.requestActive();
    }

    public void edit(final WorkspaceItem<TramoSpecification> xdoc) {
        if (xdoc == null || xdoc.getElement() == null) {
            return;

        }
        final TramoSpecUI ui = new TramoSpecUI(xdoc.getElement().clone(), xdoc.isReadOnly());
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
        return TramoSpecification.class;
    }
}
