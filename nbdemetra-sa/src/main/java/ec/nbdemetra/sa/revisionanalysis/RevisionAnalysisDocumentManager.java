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
package ec.nbdemetra.sa.revisionanalysis;

import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ws.AbstractWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.sa.revisions.RevisionAnalysisDocument;
import ec.tss.sa.revisions.RevisionAnalysisProcessor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * Document Manager of the revision analysis
 *
 * @author Mats Maggi
 */
//@ServiceProvider(
//        service = IWorkspaceItemManager.class,
//        position = 5000)
public class RevisionAnalysisDocumentManager extends AbstractWorkspaceItemManager<RevisionAnalysisDocument> {

    public static final LinearId ID = new LinearId(RevisionAnalysisProcessor.DESCRIPTOR.family, RevisionAnalysisProcessor.DESCRIPTOR.name);
    public static final String PATH = "revisionanalysis";
    public static final String ITEMPATH = "revisionanalysis.item";
    public static final String CONTEXTPATH = "revisionanalysis.context";

    @Override
    protected String getItemPrefix() {
        return "RevisionAnalysis";
    }

    @Override
    protected RevisionAnalysisDocument createNewObject() {
        RevisionAnalysisDocument d = new RevisionAnalysisDocument();
        DemetraUI demetraUI = DemetraUI.getDefault();
        d.getSpecification().setSaSpecification(demetraUI.getDefaultSASpec());
        return d;
    }

    @Override
    public Status getStatus() {
        return Status.Experimental;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.Doc;
    }

    @Override
    public Class<RevisionAnalysisDocument> getItemClass() {
        return RevisionAnalysisDocument.class;
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public String getActionsPath() {
        return PATH;
    }

    @Override
    public Action getPreferredItemAction(final Id child) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorkspaceItem<RevisionAnalysisDocument> doc = (WorkspaceItem<RevisionAnalysisDocument>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    openDocument(doc);
                }
            }
        };
    }

    public void openDocument(WorkspaceItem<RevisionAnalysisDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            RevisionAnalysisTopComponent view = new RevisionAnalysisTopComponent(item);
            view.open();
            view.requestActive();
        }
    }
}
