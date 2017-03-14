/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class RegArimaSpecFileRepository extends AbstractFileItemRepository<RegArimaSpecification> {

    @Deprecated
    public static final String REPOSITORY = "RegArimaSpec";

    @Override
    public boolean load(WorkspaceItem<RegArimaSpecification> item) {
        return loadFile(item, (RegArimaSpecification o) -> {
            item.setElement(o);
            item.resetDirty();
        });
    }

    @Override
    public boolean save(WorkspaceItem<RegArimaSpecification> item) {
        return storeFile(item, item.getElement(), item::resetDirty);
    }

    @Override
    public boolean delete(WorkspaceItem<RegArimaSpecification> doc) {
        return deleteFile(doc);
    }

    @Override
    public Class<RegArimaSpecification> getSupportedType() {
        return RegArimaSpecification.class;
    }
}
