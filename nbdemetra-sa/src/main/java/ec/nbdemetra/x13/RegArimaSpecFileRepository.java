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
public class RegArimaSpecFileRepository extends AbstractFileItemRepository<RegArimaSpecification>{

   public static final String REPOSITORY = "RegArimaSpec";
    
    @Override
    public boolean load(WorkspaceItem<RegArimaSpecification> item) {
        String sfile= this.fullName(item, REPOSITORY, false);
        if (sfile == null)
            return false;
       RegArimaSpecification spec = AbstractFileItemRepository.loadInfo(sfile, RegArimaSpecification.class);
        item.setElement(spec);
        item.resetDirty();
        return spec != null;
    }

    @Override
    public boolean save(WorkspaceItem<RegArimaSpecification> item) {
        String sfile= this.fullName(item, REPOSITORY, true);
        if (sfile == null)
            return false;
        if(saveInfo(sfile, item.getElement())){
            item.resetDirty();
            return true;
        }else
            return false;
    }

    @Override
    public boolean delete(WorkspaceItem<RegArimaSpecification> doc) {
        return delete(doc, REPOSITORY);
    }

    @Override
    public Class<RegArimaSpecification> getSupportedType() {
        return RegArimaSpecification.class;
    }
  
}
