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
package demetra.desktop.benchmarking;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(
        service = IWorkspaceItemManager.class,
        position = 1000)
public class DentonDocumentManager extends AbstractWorkspaceItemManager<DentonDocument> {

   static {
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(BasicSpecUI.AggregationType.class);
  
        DocumentUIServices.getDefault().register(DentonDocument.class, new DocumentUIServices.AbstractUIFactory<DentonSpecification, DentonDocument>() {
            @Override
            public IObjectDescriptor<DentonSpecification> getSpecificationDescriptor(DentonDocument document) {
                 return new DentonSpecUI(document.getSpecification().clone());
            }

            @Override
            public IProcDocumentView<DentonDocument> getDocumentView(DentonDocument document) {
                return DentonViewFactory.getDefault().create(document);
            }

        });
    }
    public static final LinearId ID = new LinearId("Benchmarking", "Univariate", "Denton");
    public static final String PATH = "denton";
    public static final String ITEMPATH = "denton.item";
    public static final String CONTEXTPATH = "denton.context";

    @Override
    protected String getItemPrefix() {
        return "Denton";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected DentonDocument createNewObject() {
        return new DentonDocument();
    }

    @Override
    public ItemType getItemType() {
        return ItemType.Doc;
    }

    @Override
    public String getActionsPath() {
        return PATH;
    }

    @Override
    public Status getStatus() {
        return Status.Experimental;
    }

    @Override
    public Action getPreferredItemAction(final Id child) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorkspaceItem<DentonDocument> doc = (WorkspaceItem<DentonDocument>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    openDocument(doc);
                }
            }
        };
    }

    public void openDocument(WorkspaceItem<DentonDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            DentonTopComponent view = new DentonTopComponent(item);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public Class getItemClass() {
        return DentonDocument.class;
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/benchmarking/resource-monitor_16x16.png", false);
    }
}
