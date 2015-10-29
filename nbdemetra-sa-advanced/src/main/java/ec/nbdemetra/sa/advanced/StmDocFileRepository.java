/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.sa.documents.StmDocument;
import ec.tstoolkit.MetaData;
import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class StmDocFileRepository extends AbstractFileItemRepository<StmDocument> {

    public static final String REPOSITORY = "StmDoc";

    @Override
    public boolean load(WorkspaceItem<StmDocument> item) {
        String sfile = fullName(item, REPOSITORY, false);
        if (sfile == null) {
            return false;
        }
        StmDocument doc = loadInfo(sfile, StmDocument.class);
        item.setElement(doc);
        item.resetDirty();
        return doc != null;
    }

    @Override
    public boolean save(WorkspaceItem<StmDocument> item) {
        StmDocument element = item.getElement();
        element.getMetaData().put(MetaData.DATE, new Date().toString());
        String sfile = fullName(item, REPOSITORY, true);
        if (sfile == null) {
            return false;
        }
        if (AbstractFileItemRepository.saveInfo(sfile, element)) {
            item.resetDirty();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean delete(WorkspaceItem<StmDocument> doc) {
        return delete(doc, REPOSITORY);
    }

    @Override
    public Class<StmDocument> getSupportedType() {
        return StmDocument.class;
    }
}
