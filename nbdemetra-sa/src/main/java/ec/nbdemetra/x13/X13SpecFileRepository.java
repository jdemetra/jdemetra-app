/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.satoolkit.x13.X13Specification;
import ec.tss.xml.x13.XmlX13Specification;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class X13SpecFileRepository extends AbstractFileItemRepository<X13Specification>{
    
    public static final String REPOSITORY = "X13Spec", REPOSITORY2="X12Spec";

    @Override
    public boolean load(WorkspaceItem<X13Specification> item) {
       String sfile = fullName(item, REPOSITORY, false);
        if (sfile == null) {
            return false;
        }
        X13Specification spec = loadInfo(sfile, X13Specification.class);
        if (spec == null) {
            sfile = fullName(item, REPOSITORY2, false);
            if (sfile == null) {
                return false;
            }
            spec = loadLegacy(sfile, XmlX13Specification.class);
        }
        item.setElement(spec);
        item.resetDirty();
        return spec != null;
    }

    @Override
    public boolean save(WorkspaceItem<X13Specification> item) {
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
    public boolean delete(WorkspaceItem<X13Specification> doc) {
        return delete(doc, REPOSITORY);
    }

    @Override
    public Class<X13Specification> getSupportedType() {
        return X13Specification.class;
    }
   
}


