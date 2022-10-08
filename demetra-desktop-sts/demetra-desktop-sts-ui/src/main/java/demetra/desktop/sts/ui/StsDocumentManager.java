/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.sts.ui;

import demetra.desktop.workspace.AbstractWorkspaceTsItemManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.io.ResourceWatcher.Id;
import demetra.util.LinearId;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IWorkspaceItemManager.class,
position = 1800)
public class StsDocumentManager extends AbstractWorkspaceTsItemManager<StsSpecification, StsDocument> {

    static {
        SaManager.instance.add(new StsProcessor());
        DocumentUIServices.getDefault().register(StsDocument.class, new DocumentUIServices.AbstractUIFactory<StsSpecification, StsDocument>() {

            @Override
            public IProcDocumentView<StsDocument> getDocumentView(StsDocument document) {
                return StructuralModelViewFactory.getDefault().create(document);
            }

            @Override
            public IObjectDescriptor<StsSpecification> getSpecificationDescriptor(StsDocument doc) {
                return new StsSpecUI(doc.getSpecification().clone());
            }
        });
    }
    public static final LinearId ID = new LinearId(StsProcessor.DESCRIPTOR.family, "documents", StsProcessor.DESCRIPTOR.name);
    public static final String PATH = "sts.doc";
    public static final String ITEMPATH = "sts.doc.item";
    public static final String CONTEXTPATH = "sts.doc.context";

    @Override
    protected String getItemPrefix() {
        return "StsDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected StsDocument createNewObject() {
        return new StsDocument();
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
        return Status.Acceptable;
    }

    @Override
    public void openDocument(WorkspaceItem<StsDocument> doc) {
        if (doc.isOpen()) {
            doc.getView().requestActive();
        } else {
            StsTopComponent view = new StsTopComponent(doc);
            doc.setView(view);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public Class<StsDocument> getItemClass() {
        return StsDocument.class;
    }
}
