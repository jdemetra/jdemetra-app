/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.documents;

import demetra.DemetraVersion;
import demetra.desktop.workspace.AbstractFileItemRepository;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemRepository;
import demetra.tsprovider.TsMeta;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import jdplus.x13.regarima.RegArimaDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemRepository.class)
public final class RegArimaDocFileRepository extends AbstractFileItemRepository< RegArimaDocument > {

    @Override
    public boolean load(WorkspaceItem<RegArimaDocument> item) {
        return loadFile(item, (RegArimaDocument o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<RegArimaDocument> doc, DemetraVersion version) {
        RegArimaDocument element = doc.getElement();
       
        Map<String, String> meta=new HashMap<>(element.getMetadata());
        TsMeta.TIMESTAMP.store(meta, LocalDateTime.now());
        element.updateMetadata(meta);
        
        return storeFile(doc, element, version, doc::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<RegArimaDocument> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<RegArimaDocument> getSupportedType() {
        return RegArimaDocument.class;
    }

}
