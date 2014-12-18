/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13;

import ec.nbdemetra.ws.DefaultFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.modelling.documents.RegArimaDocument;
import ec.tstoolkit.MetaData;
import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class RegArimaDocFileRepository extends DefaultFileItemRepository<RegArimaDocument> {

    public static final String REPOSITORY = "RegArimaDoc";

    @Override
    public boolean save(WorkspaceItem<RegArimaDocument> doc) {
        RegArimaDocument element = doc.getElement();
        element.getMetaData().put(MetaData.DATE, new Date().toString());
        return super.save(doc);
    }

    @Override
    public Class<RegArimaDocument> getSupportedType() {
        return RegArimaDocument.class;
    }

    @Override
    public String getRepository() {
        return REPOSITORY;
    }
}
