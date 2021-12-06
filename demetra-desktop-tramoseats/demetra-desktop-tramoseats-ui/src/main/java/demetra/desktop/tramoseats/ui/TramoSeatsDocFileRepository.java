/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramoseats.ui;

import demetra.desktop.workspace.AbstractFileItemRepository;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemRepository;
import demetra.tsprovider.TsMeta;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemRepository.class)
public final class TramoSeatsDocFileRepository extends AbstractFileItemRepository< TramoSeatsDocument > {

    @Override
    public boolean load(WorkspaceItem<TramoSeatsDocument> item) {
        return loadFile(item, (TramoSeatsDocument o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<TramoSeatsDocument> doc) {
        TramoSeatsDocument element = doc.getElement();
       
        Map<String, String> meta=new HashMap<>(element.getMetadata());
        TsMeta.TIMESTAMP.store(meta, LocalDateTime.now());
        element.updateMetadata(meta);
        
        return storeFile(doc, element, () -> {
            doc.resetDirty();
        });
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
