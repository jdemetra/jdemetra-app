/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.satoolkit.x13.X13Specification;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class X13SpecFileRepository extends AbstractFileItemRepository<X13Specification> {

    @Override
    public boolean load(WorkspaceItem<X13Specification> item) {
        return loadFile(item, (X13Specification o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<X13Specification> item) {
        return storeFile(item, item.getElement(), item::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<X13Specification> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<X13Specification> getSupportedType() {
        return X13Specification.class;
    }
}
