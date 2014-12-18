/*
 * Copyright 2013-2014 National Bank of Belgium
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

package ec.nbdemetra.sa.advanced;

import ec.nbdemetra.sa.advanced.descriptors.mixedfrequencies.MixedFrequenciesArimaSpecUI;
import ec.nbdemetra.sa.advanced.ui.MixedFrequenciesArimaViewFactory;
import ec.nbdemetra.ui.DocumentUIServices;
import ec.nbdemetra.ws.AbstractWorkspaceItemManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.sa.documents.MixedFrequenciesArimaDocument;
import ec.tss.sa.processors.MixedFrequenciesArimaProcessor;
import ec.tstoolkit.arima.special.mixedfrequencies.MixedFrequenciesSpecification;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.IProcDocumentView;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemManager.class,
position = 1910)
public class MixedFrequenciesArimaDocumentManager extends AbstractWorkspaceItemManager<MixedFrequenciesArimaDocument> {

    static {
        DocumentUIServices.getDefault().register(MixedFrequenciesArimaDocument.class, new DocumentUIServices.AbstractUIFactory<MixedFrequenciesSpecification, MixedFrequenciesArimaDocument>() {

            @Override
            public IProcDocumentView<MixedFrequenciesArimaDocument> getDocumentView(MixedFrequenciesArimaDocument document) {
                return MixedFrequenciesArimaViewFactory.getDefault().create(document);
            }

            @Override
            public IObjectDescriptor<MixedFrequenciesSpecification> getSpecificationDescriptor(MixedFrequenciesArimaDocument doc) {
                return new MixedFrequenciesArimaSpecUI(doc.getSpecification().clone());
            }
        });
    }
    public static final LinearId ID = new LinearId(MixedFrequenciesArimaProcessor.DESCRIPTOR.family, "documents", MixedFrequenciesArimaProcessor.DESCRIPTOR.name);
    public static final String PATH = "mfreqarima.doc";
    public static final String ITEMPATH = "mfreqarima.doc.item";
    public static final String CONTEXTPATH = "mfreqarima.doc.context";

    @Override
    protected String getItemPrefix() {
        return "MixedFrequenciesArimaDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected MixedFrequenciesArimaDocument createNewObject() {
        return new MixedFrequenciesArimaDocument();
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

     public void openDocument(WorkspaceItem<MixedFrequenciesArimaDocument> doc) {
        if (doc.isOpen()) {
            doc.getView().requestActive();
        } else {
            MixedFrequenciesArimaTopComponent view = new MixedFrequenciesArimaTopComponent(doc);
            doc.setView(view);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public Class<MixedFrequenciesArimaDocument> getItemClass() {
        return MixedFrequenciesArimaDocument.class;
    }

    @Override
    public Action getPreferredItemAction(final Id child) {
       return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorkspaceItem<MixedFrequenciesArimaDocument> doc = (WorkspaceItem<MixedFrequenciesArimaDocument>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    openDocument(doc);
                }
            }
        };
    }
}
