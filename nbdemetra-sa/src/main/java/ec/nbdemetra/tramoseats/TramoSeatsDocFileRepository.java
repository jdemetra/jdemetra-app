/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats;

import demetra.tsprovider.TsMeta;
import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.sa.documents.TramoSeatsDocument;
import java.time.LocalDateTime;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class TramoSeatsDocFileRepository extends AbstractFileItemRepository<TramoSeatsDocument> {

    @Override
    public boolean load(WorkspaceItem<TramoSeatsDocument> item) {
        return loadFile(item, (TramoSeatsDocument o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<TramoSeatsDocument> item) {
        TramoSeatsDocument o = item.getElement();
        TsMeta.TIMESTAMP.store(o.getMetaData(), LocalDateTime.now());
        return storeFile(item, o, item::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<TramoSeatsDocument> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<TramoSeatsDocument> getSupportedType() {
        return TramoSeatsDocument.class;
    }
}
