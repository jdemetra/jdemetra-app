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

import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public final class TsDisaggregationModelDocFileRepository extends DefaultFileItemRepository<TsDisaggregationModelDocument> {

    @Deprecated
    public static final String REPOSITORY = TsDisaggregationDocHandler.REPOSITORY;

    @Override
    public String getRepository() {
        return REPOSITORY;
    }

    @Override
    public boolean load(WorkspaceItem<TsDisaggregationModelDocument> item) {
        return loadFile(item, (TsDisaggregationModelDocument o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<TsDisaggregationModelDocument> item) {
        TsDisaggregationModelDocument element = item.getElement();
        element.getMetaData().put(MetaData.DATE, new Date().toString());
        return storeFile(item, element, item::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<TsDisaggregationModelDocument> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<TsDisaggregationModelDocument> getSupportedType() {
        return TsDisaggregationModelDocument.class;
    }
}
