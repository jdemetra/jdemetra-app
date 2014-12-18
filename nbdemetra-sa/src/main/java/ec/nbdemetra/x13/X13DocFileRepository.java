/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.sa.documents.X13Document;
import ec.tss.xml.x13.XmlX13Document;
import ec.tstoolkit.MetaData;
import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pcuser
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class X13DocFileRepository extends AbstractFileItemRepository<X13Document> {

    public static final String REPOSITORY = "X13Doc", REPOSITORY2 = "X12Doc";

    @Override
    public boolean load(WorkspaceItem<X13Document> item) {
        String sfile = fullName(item, REPOSITORY, false);
        if (sfile == null) {
            return false;
        }
        X13Document doc = loadInfo(sfile, X13Document.class);
        if (doc == null) {
            sfile = fullName(item, REPOSITORY2, false);
            if (sfile == null) {
                return false;
            }
            doc = loadLegacy(sfile, XmlX13Document.class);
        }
        item.setElement(doc);
        item.resetDirty();
        return doc != null;
    }

    @Override
    public boolean save(WorkspaceItem<X13Document> item) {
        X13Document element = item.getElement();
        element.getMetaData().put(MetaData.DATE, new Date().toString());
        String sfile = this.fullName(item, REPOSITORY, true);
        if (sfile == null) {
            return false;
        }
        if (saveInfo(sfile, element)) {
            item.resetDirty();
            item.getElement().resetDirty();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean delete(WorkspaceItem<X13Document> doc) {
        if (delete(doc, REPOSITORY)) {
            return true;
        } else {
            return delete(doc, REPOSITORY2);
        }
    }

    @Override
    public Class<X13Document> getSupportedType() {
        return X13Document.class;
    }
}
