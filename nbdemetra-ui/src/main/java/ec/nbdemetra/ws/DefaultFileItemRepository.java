/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws;

import ec.tstoolkit.information.InformationSetSerializable;
import ec.tstoolkit.utilities.IModifiable;

/**
 *
 * @author Jean
 */
public abstract class DefaultFileItemRepository<D extends IModifiable & InformationSetSerializable> extends AbstractFileItemRepository<D>{

   public abstract String getRepository();
    
    @Override
    public boolean load(WorkspaceItem<D> item) {
        String sfile= this.fullName(item, getRepository(), false);
        if (sfile == null)
            return false;
        D doc=AbstractFileItemRepository.loadInfo(sfile, getSupportedType());
        item.setElement(doc);
        item.resetDirty();
        return doc != null;
    }

    @Override
    public boolean save(WorkspaceItem<D> item) {
        String sfile= this.fullName(item, getRepository(), true);
        if (sfile == null)
            return false;
        if(saveInfo(sfile, item.getElement())){
            item.resetDirty();
            item.getElement().resetDirty();
            return true;
        }else
            return false;
     }

    @Override
    public boolean delete(WorkspaceItem<D> doc) {
        return delete(doc, getRepository());
    }

  
}

