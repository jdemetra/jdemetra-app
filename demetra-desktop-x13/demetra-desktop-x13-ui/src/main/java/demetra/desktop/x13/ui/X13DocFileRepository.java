/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.x13.ui;

import demetra.desktop.workspace.AbstractFileItemRepository;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemRepository;
import demetra.tsprovider.TsMeta;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import jdplus.x13.X13Document;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemRepository.class)
public final class X13DocFileRepository extends AbstractFileItemRepository< X13Document > {

    @Override
    public boolean load(WorkspaceItem<X13Document> item) {
        return loadFile(item, (X13Document o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<X13Document> doc) {
        X13Document element = doc.getElement();
       
        Map<String, String> meta=new HashMap<>(element.getMetadata());
        TsMeta.TIMESTAMP.store(meta, LocalDateTime.now());
        element.updateMetadata(meta);
        
        return storeFile(doc, element, () -> {
            doc.resetDirty();
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
