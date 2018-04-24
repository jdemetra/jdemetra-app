/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.variables;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tstoolkit.timeseries.regression.TsVariables;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class VariablesFileRepository extends AbstractFileItemRepository<TsVariables> {

    @Override
    public boolean load(WorkspaceItem<TsVariables> item) {
        return loadFile(item, (TsVariables o) -> {
            item.setElement(o);
            item.resetDirty();
            item.getOwner().getContext().getTsVariableManagers().set(item.getDisplayName(), o);
        });
    }

    @Override
    public boolean save(WorkspaceItem<TsVariables> item) {
        return storeFile(item, item.getElement(), () -> {
            item.resetDirty();
            item.getElement().resetDirty();
        });
    }

    @Override
    public boolean delete(WorkspaceItem<TsVariables> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<TsVariables> getSupportedType() {
        return TsVariables.class;
    }
}
