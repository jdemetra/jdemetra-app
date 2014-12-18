/*
 * Copyright 2013 National Bank of Belgium
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
package ec.nbdemetra.benchmarking;

import ec.nbdemetra.benchmarking.descriptors.CholetteSpecUI;
import ec.nbdemetra.benchmarking.ui.CholetteViewFactory;
import ec.nbdemetra.ui.DocumentUIServices;
import ec.nbdemetra.ws.AbstractWorkspaceItemManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.disaggregation.documents.CholetteDocument;
import ec.tss.disaggregation.documents.UniCholetteSpecification;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.IProcDocumentView;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(
        service = IWorkspaceItemManager.class,
position = 1500)
public class CholetteDocumentManager extends AbstractWorkspaceItemManager<CholetteDocument> {

   static {
        DocumentUIServices.getDefault().register(CholetteDocument.class, new DocumentUIServices.AbstractUIFactory<UniCholetteSpecification, CholetteDocument>() {
            @Override
            public IObjectDescriptor<UniCholetteSpecification> getSpecificationDescriptor(CholetteDocument document) {
                 return new CholetteSpecUI(document.getSpecification().clone());
            }

            @Override
            public IProcDocumentView<CholetteDocument> getDocumentView(CholetteDocument document) {
                return CholetteViewFactory.getDefault().create(document);
            }

        });
    }
    
    public static final LinearId ID = new LinearId("Benchmarking", "Univariate", "Cholette");
    public static final String PATH = "cholette";
    public static final String ITEMPATH = "cholette.item";
    public static final String CONTEXTPATH = "cholette.context";

    @Override
    protected String getItemPrefix() {
        return "Cholette";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected CholetteDocument createNewObject() {
        return new CholetteDocument();
    }

    @Override
    public IWorkspaceItemManager.ItemType getItemType() {
        return IWorkspaceItemManager.ItemType.Doc;
    }

    @Override
    public String getActionsPath() {
        return PATH;
    }

    @Override
    public IWorkspaceItemManager.Status getStatus() {
        return IWorkspaceItemManager.Status.Experimental;
    }

    @Override
    public Action getPreferredItemAction(final Id child) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorkspaceItem<CholetteDocument> doc = (WorkspaceItem<CholetteDocument>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    openDocument(doc);
                }
            }
        };
    }

    @Override
    public Class getItemClass() {
        return CholetteDocument.class;
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/benchmarking/resource-monitor_16x16.png", false);
    }

    public void openDocument(WorkspaceItem<CholetteDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            CholetteTopComponent view = new CholetteTopComponent(item);
            view.open();
            view.requestActive();
        }
    }
}
