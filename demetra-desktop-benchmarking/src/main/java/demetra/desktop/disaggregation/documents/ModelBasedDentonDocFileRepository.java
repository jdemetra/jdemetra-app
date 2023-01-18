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

import demetra.DemetraVersion;
import demetra.desktop.workspace.AbstractFileItemRepository;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemRepository;
import demetra.tsprovider.TsMeta;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import jdplus.benchmarking.univariate.DentonDocument;
import jdplus.tempdisagg.univariate.ModelBasedDentonDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author palatej
 */
@ServiceProvider(service = WorkspaceItemRepository.class)
public final class ModelBasedDentonDocFileRepository extends AbstractFileItemRepository< ModelBasedDentonDocument> {

    @Override
    public boolean load(WorkspaceItem<ModelBasedDentonDocument> item) {
        return loadFile(item, (ModelBasedDentonDocument o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<ModelBasedDentonDocument> doc, DemetraVersion version) {
        ModelBasedDentonDocument element = doc.getElement();

        Map<String, String> meta = new HashMap<>(element.getMetadata());
        TsMeta.TIMESTAMP.store(meta, LocalDateTime.now());
        element.updateMetadata(meta);
        return storeFile(doc, element, version, doc::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<ModelBasedDentonDocument> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<ModelBasedDentonDocument> getSupportedType() {
        return ModelBasedDentonDocument.class;
    }

}
