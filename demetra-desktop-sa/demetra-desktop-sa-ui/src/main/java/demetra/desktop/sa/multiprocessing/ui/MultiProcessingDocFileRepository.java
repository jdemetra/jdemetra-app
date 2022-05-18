/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.ui;

import demetra.DemetraVersion;
import demetra.desktop.workspace.AbstractFileItemRepository;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemRepository;
import demetra.sa.SaItems;
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
public class MultiProcessingDocFileRepository extends AbstractFileItemRepository<MultiProcessingDocument> {

    @Override
    public boolean load(WorkspaceItem<MultiProcessingDocument> item) {
        return loadFile(item, (SaItems o) -> {
            item.setElement(MultiProcessingDocument.open(o));
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<MultiProcessingDocument> doc, DemetraVersion version) {
        MultiProcessingDocument element = doc.getElement();
        Map<String, String> meta=new HashMap<>(element.getMetadata());
        TsMeta.TIMESTAMP.store(meta, LocalDateTime.now());
        SaItems current = element.current(meta);
        return storeFile(doc, current, version, doc::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<MultiProcessingDocument> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<MultiProcessingDocument> getSupportedType() {
        return MultiProcessingDocument.class;
    }
}
