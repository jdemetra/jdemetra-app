/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class TramoSeatsSpecFileRepository extends AbstractFileItemRepository<TramoSeatsSpecification> {

    @Deprecated
    public static final String REPOSITORY = "TramoSeatsSpec";

    @Override
    public boolean load(WorkspaceItem<TramoSeatsSpecification> item) {
        return loadFile(item, (TramoSeatsSpecification o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<TramoSeatsSpecification> item) {
        return storeFile(item, item.getElement(), item::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<TramoSeatsSpecification> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<TramoSeatsSpecification> getSupportedType() {
        return TramoSeatsSpecification.class;
    }
}
