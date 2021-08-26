/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.variables;

import ec.nbdemetra.ws.AbstractWorkspaceItemManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 * @author Jean Palate
 */
@ServiceProvider(service = IWorkspaceItemManager.class, position = 9910)
public class VariablesDocumentManager extends AbstractWorkspaceItemManager<TsVariables> {

    public static final LinearId ID = new LinearId("Utilities", "Variables");
    public static final String PATH = "variables";
    public static final String ITEMPATH = "variables.item";
    public static final String CONTEXTPATH = "variables.context";
    
    @Override
   public WorkspaceItem<TsVariables> create(Workspace ws) {
        WorkspaceItem<TsVariables> nvars = super.create(ws);
        ProcessingContext.getActiveContext().getTsVariableManagers().set(nvars.getDisplayName(), nvars.getElement());
        return nvars;
   }

    @Override
    protected String getItemPrefix() {
        return "Vars";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected TsVariables createNewObject() {
        return new TsVariables();
    }

    @Override
    public IWorkspaceItemManager.ItemType getItemType() {
        return IWorkspaceItemManager.ItemType.Doc;
    }

    @Override
    public String getActionsPath() {
        return PATH;
    }

    @Override
    public IWorkspaceItemManager.Status getStatus() {
        return IWorkspaceItemManager.Status.Experimental;
    }

    @Override
    public Action getPreferredItemAction(final Id child) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorkspaceItem<TsVariables> doc = (WorkspaceItem<TsVariables>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    openDocument(doc);
                }
            }
        };
    }

    public void openDocument(WorkspaceItem<TsVariables> doc) {
        if (doc.isOpen()) {
            doc.getView().requestActive();
        } else {
            VariablesTopComponent view = new VariablesTopComponent(doc);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public List<WorkspaceItem<TsVariables>> getDefaultItems() {
//        List<WorkspaceItem<TsVariables>> result = new ArrayList<>();
//        NameManager<TsVariables> manager = ProcessingContext.getActiveContext().getTsVariableManagers();
//        for (String o : manager.getNames()) {
//            result.add(systemItem(o, manager.get(o)));
//        }
//        return result;
        return Collections.emptyList();
    }

    @Override
    public Class<TsVariables> getItemClass() {
        return TsVariables.class;
    }

    @Override
    public Icon getManagerIcon() {
        return null;
    }

    @Override
    public Icon getItemIcon(WorkspaceItem<TsVariables> doc) {
        return null;
    }

    public static WorkspaceItem<TsVariables> systemItem(String name, TsVariables p) {
        return WorkspaceItem.system(ID, name, p);
    }

    @Override
    public boolean isAutoLoad() {
        return true;
    }
}
