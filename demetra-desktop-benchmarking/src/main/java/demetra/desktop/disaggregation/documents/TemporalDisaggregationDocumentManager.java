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
package demetra.desktop.disaggregation.documents;

import demetra.desktop.workspace.AbstractWorkspaceItemManager;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.util.Id;
import demetra.util.LinearId;
import jdplus.tempdisagg.univariate.TemporalDisaggregationDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemManager.class,
        position = 500)
public class TemporalDisaggregationDocumentManager extends AbstractWorkspaceItemManager<TemporalDisaggregationDocument> {

    public static final LinearId ID = new LinearId("Temporal disaggregation", "Univariate", "Regression model");
    public static final String PATH = "temporaldisaggregation.doc";
    public static final String ITEMPATH = "temporaldisaggregation.doc.item";
    public static final String CONTEXTPATH = "temporaldisaggregation.doc.context";

    @Override
    protected String getItemPrefix() {
        return "TemporalDisaggregationDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public TemporalDisaggregationDocument createNewObject() {
        return new TemporalDisaggregationDocument();
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
    public Class<TemporalDisaggregationDocument> getItemClass() {
        return TemporalDisaggregationDocument.class;
    }

}
