/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace;

import demetra.processing.ProcSpecification;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author Jean Palate
 * @param <S>
 * @param <D>
 */
public abstract class AbstractWorkspaceTsItemManager<S extends ProcSpecification, D extends TsDocument<S,?>> extends AbstractWorkspaceItemManager<D>{

  
    @Override
    public abstract D createNewObject();

    
    @Override
    public WorkspaceItem<D> create(Workspace ws){
        return super.create(ws);
    }
    
}
