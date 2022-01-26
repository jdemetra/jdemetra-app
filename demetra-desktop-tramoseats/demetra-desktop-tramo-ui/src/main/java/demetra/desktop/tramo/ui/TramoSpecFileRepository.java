/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.ui;

import demetra.desktop.workspace.AbstractFileItemRepository;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemRepository;
import demetra.tramo.TramoSpec;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemRepository.class)
public class TramoSpecFileRepository extends AbstractFileItemRepository<TramoSpec> {

    @Override
    public boolean load(WorkspaceItem<TramoSpec> item) {
        return loadFile(item, (TramoSpec o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<TramoSpec> item) {
        return storeFile(item, item.getElement(), item::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<TramoSpec> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<TramoSpec> getSupportedType() {
        return TramoSpec.class;
    }
}
