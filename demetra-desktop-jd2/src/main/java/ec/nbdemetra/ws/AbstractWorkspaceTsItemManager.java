/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws;

import ec.tss.documents.TsDocument;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.utilities.Id;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author Jean Palate
 */
public abstract class AbstractWorkspaceTsItemManager<S extends IProcSpecification, D extends TsDocument<S,?>> extends AbstractWorkspaceItemManager<D>{

    @Override
    public Action getPreferredItemAction(final Id child) {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                WorkspaceItem<D> doc = (WorkspaceItem<D>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    openDocument(doc);
                }
            }
        };
    }
  
    public abstract void openDocument(WorkspaceItem<D> doc);
    
    @Override
    public WorkspaceItem<D> create(Workspace ws){
        return (WorkspaceItem<D>) super.create(ws);
    }
    
}
