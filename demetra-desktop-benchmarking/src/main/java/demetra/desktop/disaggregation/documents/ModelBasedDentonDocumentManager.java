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
package demetra.desktop.disaggregation.documents;

import demetra.desktop.workspace.AbstractWorkspaceItemManager;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.util.Id;
import demetra.util.LinearId;
import javax.swing.Icon;
import jdplus.tempdisagg.univariate.ModelBasedDentonDocument;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(
        service = WorkspaceItemManager.class,
        position = 1100)
public class ModelBasedDentonDocumentManager extends AbstractWorkspaceItemManager<ModelBasedDentonDocument> {

    public static final LinearId ID = new LinearId("Temporal disaggregation", "Univariate", "Model-Based Denton");
    public static final String PATH = "mbdenton";
    public static final String ITEMPATH = "mbdenton.item";
    public static final String CONTEXTPATH = "mbdenton.context";

    @Override
    protected String getItemPrefix() {
        return "ModelBasedDenton";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public ModelBasedDentonDocument createNewObject() {
        return new ModelBasedDentonDocument();
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
        return Status.Certified;
    }

    @Override
    public Class<ModelBasedDentonDocument> getItemClass() {
        return ModelBasedDentonDocument.class;
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("demetra/desktop/benchmarking/resource-monitor_16x16.png", false);
    }
}
