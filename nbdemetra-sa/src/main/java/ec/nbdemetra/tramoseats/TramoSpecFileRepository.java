/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class TramoSpecFileRepository extends AbstractFileItemRepository<TramoSpecification> {

    @Deprecated
    public static final String REPOSITORY = "TramoSpec";

    @Override
    public boolean load(WorkspaceItem<TramoSpecification> item) {
        return loadFile(item, (TramoSpecification o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<TramoSpecification> item) {
        return storeFile(item, item.getElement(), item::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<TramoSpecification> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<TramoSpecification> getSupportedType() {
        return TramoSpecification.class;
    }
}
