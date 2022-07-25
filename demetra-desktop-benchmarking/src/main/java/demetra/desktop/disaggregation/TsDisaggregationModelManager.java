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
package demetra.desktop.disaggregation;

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
public class TsDisaggregationModelManager extends AbstractWorkspaceItemManager<TsDisaggregationModelDocument> {

    static {
        DocumentUIServices.getDefault().register(TsDisaggregationModelDocument.class, new DocumentUIServices.AbstractUIFactory<DisaggregationSpecification, TsDisaggregationModelDocument>() {
            @Override
            public IObjectDescriptor<DisaggregationSpecification> getSpecificationDescriptor(TsDisaggregationModelDocument document) {
                Ts[] input = document.getInput();
                TsDomain domain = null;
                if (input != null && input.length > 0) {
                    TsData data = input[0].getTsData();
                    if (data != null) {
                        domain = data.getDomain();
                    }
                }
                return new DefaultTsDisaggregationSpecUI(document.getSpecification().clone(), domain, false);
            }

            @Override
            public IProcDocumentView<TsDisaggregationModelDocument> getDocumentView(TsDisaggregationModelDocument document) {
                return TsDisaggregationViewFactory.getDefault().create(document);
            }
        });
    }
    public static final LinearId ID = new LinearId("Temporal disaggregation", "Regression model");
    public static final String PATH = "tsdisaggregationmodel.doc";
    public static final String ITEMPATH = "tsdisaggregationmodel.doc.item";
    public static final String CONTEXTPATH = "tsdisaggregationmodel.doc.context";

    @Override
    protected String getItemPrefix() {
        return "TsDisaggregationModel";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected TsDisaggregationModelDocument createNewObject() {
        return new TsDisaggregationModelDocument();
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
                WorkspaceItem<TsDisaggregationModelDocument> doc = (WorkspaceItem<TsDisaggregationModelDocument>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    openDocument(doc);
                }
            }
        };
    }

    @Override
    public Class getItemClass() {
        return TsDisaggregationModelDocument.class;
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/benchmarking/resource-monitor_16x16.png", false);
    }

    public void openDocument(WorkspaceItem<TsDisaggregationModelDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            TsDisaggregationModelTopComponent view = new TsDisaggregationModelTopComponent(item);
            view.open();
            view.requestActive();
        }
    }
}
