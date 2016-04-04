/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tss.xml.tramoseats.XmlTramoSeatsSpecification;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class TramoSeatsSpecFileRepository extends AbstractFileItemRepository<TramoSeatsSpecification>{
    
    public static final String REPOSITORY = "TramoSeatsSpec";

    @Override
    public boolean load(WorkspaceItem<TramoSeatsSpecification> item) {
       String sfile = fullName(item, REPOSITORY, false);
        if (sfile == null) {
            return false;
        }
        TramoSeatsSpecification doc = loadInfo(sfile, TramoSeatsSpecification.class);
        if (doc == null) {
            doc = loadLegacy(sfile, XmlTramoSeatsSpecification.class);
        }
        item.setElement(doc);
        item.resetDirty();
        return doc != null;
    }

    @Override
    public boolean save(WorkspaceItem<TramoSeatsSpecification> item) {
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
    public boolean delete(WorkspaceItem<TramoSeatsSpecification> doc) {
        return super.delete(doc, REPOSITORY);
    }

    @Override
    public Class<TramoSeatsSpecification> getSupportedType() {
        return TramoSeatsSpecification.class;
    }
   
}

