/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramoseats.documents;

import demetra.DemetraVersion;
import demetra.desktop.workspace.AbstractFileItemRepository;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemRepository;
import demetra.tramoseats.TramoSeatsSpec;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemRepository.class)
public class TramoSeatsSpecFileRepository extends AbstractFileItemRepository<TramoSeatsSpec> {

    @Override
    public boolean load(WorkspaceItem<TramoSeatsSpec> item) {
        return loadFile(item, (TramoSeatsSpec o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<TramoSeatsSpec> item, DemetraVersion version) {
        return storeFile(item, item.getElement(), version, item::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<TramoSeatsSpec> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<TramoSeatsSpec> getSupportedType() {
        return TramoSeatsSpec.class;
    }
}
