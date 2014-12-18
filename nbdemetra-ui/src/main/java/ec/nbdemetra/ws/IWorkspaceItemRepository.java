/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws;

/**
 *
 * @author pcuser
 */
public interface IWorkspaceItemRepository<D> {
    
    Class<D> getSupportedType();
    
    boolean load(WorkspaceItem<D> item);

    boolean save(WorkspaceItem<D> item);

    boolean delete(WorkspaceItem<D> doc);
}
