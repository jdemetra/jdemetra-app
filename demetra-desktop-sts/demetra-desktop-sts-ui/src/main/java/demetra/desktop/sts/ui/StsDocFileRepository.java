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

import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class StsDocFileRepository extends AbstractFileItemRepository<StmDocument> {

    public static final String REPOSITORY = "StmDoc";

    @Override
    public boolean load(WorkspaceItem<StmDocument> item) {
        String sfile = fullName(item, REPOSITORY, false);
        if (sfile == null) {
            return false;
        }
        StmDocument doc = loadInfo(sfile, StmDocument.class);
        item.setElement(doc);
        item.resetDirty();
        return doc != null;
    }

    @Override
    public boolean save(WorkspaceItem<StmDocument> item) {
        StmDocument element = item.getElement();
        element.getMetaData().put(MetaData.DATE, new Date().toString());
        String sfile = fullName(item, REPOSITORY, true);
        if (sfile == null) {
            return false;
        }
        if (AbstractFileItemRepository.saveInfo(sfile, element)) {
            item.resetDirty();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean delete(WorkspaceItem<StmDocument> doc) {
        return delete(doc, REPOSITORY);
    }

    @Override
    public Class<StmDocument> getSupportedType() {
        return StmDocument.class;
    }
}
