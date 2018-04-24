/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.sa.documents.X13Document;
import ec.tstoolkit.MetaData;
import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class X13DocFileRepository extends AbstractFileItemRepository<X13Document> {

    @Override
    public boolean load(WorkspaceItem<X13Document> item) {
        return loadFile(item, (X13Document o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<X13Document> item) {
        X13Document o = item.getElement();
        o.getMetaData().put(MetaData.DATE, new Date().toString());
        return storeFile(item, o, () -> {
            item.resetDirty();
            item.getElement().resetDirty();
        });
    }

    @Override
    public boolean delete(WorkspaceItem<X13Document> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<X13Document> getSupportedType() {
        return X13Document.class;
    }
}
