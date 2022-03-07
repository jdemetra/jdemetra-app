/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.variables;

import demetra.desktop.workspace.AbstractFileItemRepository;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemRepository;
import demetra.timeseries.regression.ModellingContext;
import demetra.timeseries.regression.TsDataSuppliers;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemRepository.class)
public class VariablesFileRepository extends AbstractFileItemRepository<TsDataSuppliers> {

    @Override
    public boolean load(WorkspaceItem<TsDataSuppliers> item) {
        return loadFile(item, (TsDataSuppliers o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<TsDataSuppliers> item) {
        return storeFile(item, item.getElement(), item::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<TsDataSuppliers> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<TsDataSuppliers> getSupportedType() {
        return TsDataSuppliers.class;
    }
    
}
