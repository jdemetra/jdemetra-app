/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.ui;

import demetra.desktop.workspace.AbstractFileItemRepository;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemRepository;
import demetra.tsprovider.TsMeta;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import jdplus.regsarima.regular.RegSarimaModel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemRepository.class)
public final class TramoDocFileRepository extends AbstractFileItemRepository< TramoDocument > {

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
       
        Map<String, String> meta=new HashMap<>(element.getMetadata());
        TsMeta.TIMESTAMP.store(meta, LocalDateTime.now());
        element.updateMetadata(meta);
        
        return storeFile(doc, element, () -> {
            doc.resetDirty();
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
