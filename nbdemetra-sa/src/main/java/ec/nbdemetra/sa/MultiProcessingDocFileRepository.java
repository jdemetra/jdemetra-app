/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.sa.SaProcessing;
import java.text.DateFormat;
import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class MultiProcessingDocFileRepository extends AbstractFileItemRepository<MultiProcessingDocument> {

    @Deprecated
    public static final String REPOSITORY = "SAProcessing";

    @Override
    public boolean load(WorkspaceItem<MultiProcessingDocument> item) {
        return loadFile(item, (SaProcessing o) -> {
            item.setElement(MultiProcessingDocument.open(o));
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<MultiProcessingDocument> item) {
        SaProcessing o = item.getElement().getCurrent();
        o.getMetaData().put(SaProcessing.TIMESTAMP, DateFormat.getDateTimeInstance().format(new Date()));
        return storeFile(item, o, item::resetDirty);
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
