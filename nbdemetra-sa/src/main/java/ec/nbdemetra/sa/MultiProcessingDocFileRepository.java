/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.sa.SaProcessing;
import ec.tss.xml.sa.XmlSaProcessing;
import java.text.DateFormat;
import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pcuser
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class MultiProcessingDocFileRepository extends AbstractFileItemRepository<MultiProcessingDocument> {

    public static final String REPOSITORY = "SAProcessing";

    @Override
    public boolean load(WorkspaceItem<MultiProcessingDocument> item) {
        String sfile = fullName(item, REPOSITORY, false);
        if (sfile == null) {
            return false;
        }
        SaProcessing doc = loadInfo(sfile, SaProcessing.class);
        if (doc == null) {
            doc = loadLegacy(sfile, XmlSaProcessing.class);
        }
        if (doc != null){
            
        item.setElement(MultiProcessingDocument.open(doc));
        item.resetDirty();
        }
        return doc != null;
    }

    @Override
    public boolean save(WorkspaceItem<MultiProcessingDocument> item) {
        SaProcessing current = item.getElement().getCurrent();
        current.getMetaData().put(SaProcessing.TIMESTAMP, DateFormat.getDateTimeInstance().format(new Date()));
        String sfile = fullName(item, REPOSITORY, true);
        if (sfile == null) {
            return false;
        }
        if ( saveInfo(sfile, item.getElement().getCurrent())){
            item.resetDirty();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean delete(WorkspaceItem<MultiProcessingDocument> doc) {
        return delete(doc, REPOSITORY);
    }

    @Override
    public Class<MultiProcessingDocument> getSupportedType() {
        return MultiProcessingDocument.class;
    }
}
