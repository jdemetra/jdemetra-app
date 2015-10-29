/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats;

import ec.nbdemetra.ws.DefaultFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.modelling.documents.TramoDocument;
import ec.tstoolkit.MetaData;
import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public final class TramoDocFileRepository extends DefaultFileItemRepository<TramoDocument> {

    public static final String REPOSITORY = "TramoDoc";

    @Override
    public String getRepository() {
        return REPOSITORY;
    }

    @Override
    public Class<TramoDocument> getSupportedType() {
        return TramoDocument.class;
    }

    @Override
    public boolean save(WorkspaceItem<TramoDocument> doc) {
        TramoDocument element = doc.getElement();
        element.getMetaData().put(MetaData.DATE, new Date().toString());
        return super.save(doc);
    }
}
