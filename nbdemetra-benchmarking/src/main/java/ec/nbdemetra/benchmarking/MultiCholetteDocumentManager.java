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

import ec.nbdemetra.ws.AbstractWorkspaceItemManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.disaggregation.documents.MultiCholetteDocument;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
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
position = 3000)
public class MultiCholetteDocumentManager extends AbstractWorkspaceItemManager<MultiCholetteDocument> {

//    static {
//        DocumentUIServices.getDefault().register(TsDisaggregationModelDocument.class, new DocumentUIServices.AbstractUIFactory<DisaggregationSpecification, TsDisaggregationModelDocument>() {
//
//            @Override
//            public IObjectDescriptor<DisaggregationSpecification> getSpecificationDescriptor(TsDisaggregationModelDocument document) {
//                TsData[] input = document.getInput();
//                TsDomain domain=null;
//                if (input != null && input.length >0)
//                    domain=input[0].getDomain();
//                return new DefaultTsDisaggregationSpecUI(document.getSpecification(), domain, false);
//            }
//
//            @Override
//            public IProcDocumentView<TsDisaggregationModelDocument> getDocumentView(TsDisaggregationModelDocument document) {
//                return TsDisaggregationViewFactory.getDefault().create(document);
//            }
//        });
//    }
    
    public static final LinearId ID = new LinearId("Benchmarking", "Multivariate", "Cholette");
    public static final String PATH = "mcholette";
    public static final String ITEMPATH = "mcholette.item";
    public static final String CONTEXTPATH = "mcholette.context";

    @Override
    protected String getItemPrefix() {
        return "MultiCholette";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected MultiCholetteDocument createNewObject() {
        return new MultiCholetteDocument();
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
                WorkspaceItem<MultiCholetteDocument> doc = (WorkspaceItem<MultiCholetteDocument>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    openDocument(doc);
                }
            }
        };
    }

    @Override
    public Class getItemClass() {
        return MultiCholetteDocument.class;
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/benchmarking/resource-monitor_16x16.png", false);
    }

    public void openDocument(WorkspaceItem<MultiCholetteDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            MultiCholetteTopComponent view = new MultiCholetteTopComponent(item);
            view.open();
            view.requestActive();
        }
    }
}
