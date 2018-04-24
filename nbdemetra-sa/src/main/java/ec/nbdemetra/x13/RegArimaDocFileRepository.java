/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13;

import ec.nbdemetra.ws.DefaultFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.modelling.documents.RegArimaDocument;
import ec.tstoolkit.MetaData;
import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class RegArimaDocFileRepository extends DefaultFileItemRepository<RegArimaDocument> {

    @Override
    public boolean load(WorkspaceItem<RegArimaDocument> item) {
        return loadFile(item, (RegArimaDocument o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<RegArimaDocument> doc) {
        RegArimaDocument element = doc.getElement();
        element.getMetaData().put(MetaData.DATE, new Date().toString());
        return storeFile(doc, element, () -> {
            doc.resetDirty();
            doc.getElement().resetDirty();
        });
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
