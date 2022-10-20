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

import demetra.DemetraVersion;
import demetra.desktop.workspace.AbstractFileItemRepository;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemRepository;
import demetra.tsprovider.TsMeta;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import jdplus.tempdisagg.univariate.TemporalDisaggregationDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemRepository.class)
public final class TemporalDisaggregationDocFileRepository extends AbstractFileItemRepository< TemporalDisaggregationDocument > {

    @Override
    public boolean load(WorkspaceItem<TemporalDisaggregationDocument> item) {
        return loadFile(item, (TemporalDisaggregationDocument o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<TemporalDisaggregationDocument> doc, DemetraVersion version) {
        TemporalDisaggregationDocument element = doc.getElement();
       
        Map<String, String> meta=new HashMap<>(element.getMetadata());
        TsMeta.TIMESTAMP.store(meta, LocalDateTime.now());
        element.updateMetadata(meta);
        return storeFile(doc, element, version, doc::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<TemporalDisaggregationDocument> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<TemporalDisaggregationDocument> getSupportedType() {
        return TemporalDisaggregationDocument.class;
    }

}
