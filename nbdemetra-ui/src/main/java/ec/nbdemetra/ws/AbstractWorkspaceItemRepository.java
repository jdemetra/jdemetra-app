/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws;

/**
 *
 * @author Jean Palate
 */
public abstract class AbstractWorkspaceItemRepository<D> implements IWorkspaceItemRepository<D> {

    @Override
    public boolean load(WorkspaceItem<D> item) {
        return false;
    }

    @Override
    public boolean save(WorkspaceItem<D> item) {
        return false;
    }

    @Override
    public boolean delete(WorkspaceItem<D> doc) {
        return false;
    }

}
