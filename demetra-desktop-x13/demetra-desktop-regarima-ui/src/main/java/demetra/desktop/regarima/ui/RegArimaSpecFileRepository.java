/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.ui;

import demetra.desktop.workspace.AbstractFileItemRepository;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemRepository;
import demetra.regarima.RegArimaSpec;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemRepository.class)
public class RegArimaSpecFileRepository extends AbstractFileItemRepository<RegArimaSpec> {

    @Override
    public boolean load(WorkspaceItem<RegArimaSpec> item) {
        return loadFile(item, (RegArimaSpec o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<RegArimaSpec> item) {
        return storeFile(item, item.getElement(), item::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<RegArimaSpec> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<RegArimaSpec> getSupportedType() {
        return RegArimaSpec.class;
    }
}
