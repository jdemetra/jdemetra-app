/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.x13.ui;

import demetra.desktop.workspace.AbstractFileItemRepository;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemRepository;
import demetra.x13.X13Spec;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemRepository.class)
public class X13SpecFileRepository extends AbstractFileItemRepository<X13Spec> {

    @Override
    public boolean load(WorkspaceItem<X13Spec> item) {
        return loadFile(item, (X13Spec o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<X13Spec> item) {
        return storeFile(item, item.getElement(), item::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<X13Spec> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<X13Spec> getSupportedType() {
        return X13Spec.class;
    }
}
