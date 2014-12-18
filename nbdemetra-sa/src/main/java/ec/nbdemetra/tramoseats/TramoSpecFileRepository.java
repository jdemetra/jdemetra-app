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
 * @author pcuser
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class TramoSpecFileRepository extends AbstractFileItemRepository<TramoSpecification> {

    public static final String REPOSITORY = "TramoSpec";

    @Override
    public boolean load(WorkspaceItem<TramoSpecification> item) {
        String sfile = this.fullName(item, REPOSITORY, false);
        if (sfile == null) {
            return false;
        }
        TramoSpecification spec = AbstractFileItemRepository.loadInfo(sfile, TramoSpecification.class);
        item.setElement(spec);
        item.resetDirty();
        return spec != null;

    }

    @Override
    public boolean save(WorkspaceItem<TramoSpecification> item) {
        String sfile = this.fullName(item, REPOSITORY, true);
        if (sfile == null) {
            return false;
        }
        if(saveInfo(sfile, item.getElement())){
            item.resetDirty();
            return true;
        }else
            return false;
    }

    @Override
    public boolean delete(WorkspaceItem<TramoSpecification> doc) {
        return delete(doc, REPOSITORY);
    }

    @Override
    public Class<TramoSpecification> getSupportedType() {
        return TramoSpecification.class;
    }
}
