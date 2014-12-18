/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.variables;

import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.xml.regression.XmlTsVariables;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.regression.TsVariables;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class VariablesFileRepository extends AbstractFileItemRepository<TsVariables> {

    public static final String REPOSITORY = "Variables";

    @Override
    public boolean load(WorkspaceItem<TsVariables> item) {
        String sfile = this.fullName(item, REPOSITORY, false);
        if (sfile == null) {
            return false;
        }
        TsVariables doc = AbstractFileItemRepository.loadLegacy(sfile, XmlTsVariables.class);
        if (doc == null) {// try Demetra+ format
            doc = AbstractFileItemRepository.loadLegacy(sfile, ec.tss.xml.legacy.XmlTsVariables.class);
        }
        item.setElement(doc);
        item.resetDirty();
        if (doc != null) {
            item.getOwner().getContext().getTsVariableManagers().set(item.getDisplayName(), doc);
        }
        return doc != null;
    }

    @Override
    public boolean save(WorkspaceItem<TsVariables> item) {
        String sfile = this.fullName(item, REPOSITORY, true);
        if (sfile == null) {
            return false;
        }
        if (AbstractFileItemRepository.saveLegacy(sfile, item, XmlTsVariables.class)) {
            item.resetDirty();
            item.getElement().resetDirty();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean delete(WorkspaceItem<TsVariables> doc) {
        return delete(doc, REPOSITORY);
    }

    @Override
    public Class<TsVariables> getSupportedType() {
        return TsVariables.class;
    }
}
