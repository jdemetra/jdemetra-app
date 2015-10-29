/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.sa.documents.TramoSeatsDocument;
import ec.tss.xml.tramoseats.XmlTramoSeatsDocument;
import ec.tstoolkit.MetaData;
import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class TramoSeatsDocFileRepository extends AbstractFileItemRepository<TramoSeatsDocument> {

    public static final String REPOSITORY = "TramoSeatsDoc";

    @Override
    public boolean load(WorkspaceItem<TramoSeatsDocument> item) {
        String sfile = fullName(item, REPOSITORY, false);
        if (sfile == null) {
            return false;
        }
        TramoSeatsDocument doc = loadInfo(sfile, TramoSeatsDocument.class);
        if (doc == null) {
            doc = loadLegacy(sfile, XmlTramoSeatsDocument.class);
        }
        item.setElement(doc);
        item.resetDirty();
        return doc != null;
    }

    @Override
    public boolean save(WorkspaceItem<TramoSeatsDocument> item) {
        TramoSeatsDocument element = item.getElement();
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
    public boolean delete(WorkspaceItem<TramoSeatsDocument> doc) {
        return delete(doc, REPOSITORY);
    }

    @Override
    public Class<TramoSeatsDocument> getSupportedType() {
        return TramoSeatsDocument.class;
    }
}
