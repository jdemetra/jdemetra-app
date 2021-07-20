/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats;

import demetra.tsprovider.TsMeta;
import ec.nbdemetra.ws.DefaultFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.modelling.documents.TramoDocument;
import java.time.LocalDateTime;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public final class TramoDocFileRepository extends DefaultFileItemRepository<TramoDocument> {

    @Override
    public boolean load(WorkspaceItem<TramoDocument> item) {
        return loadFile(item, (TramoDocument o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<TramoDocument> doc) {
        TramoDocument element = doc.getElement();
        TsMeta.TIMESTAMP.store(element.getMetaData(), LocalDateTime.now());
        return storeFile(doc, element, () -> {
            doc.resetDirty();
            doc.getElement().resetDirty();
        });
    }

    @Override
    public boolean delete(WorkspaceItem<TramoDocument> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<TramoDocument> getSupportedType() {
        return TramoDocument.class;
    }
}
