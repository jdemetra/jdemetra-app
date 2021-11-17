/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace;

/**
 *
 * @author Jean Palate
 * @param <D>
 */
public abstract class AbstractWorkspaceItemRepository<D> implements WorkspaceItemRepository<D> {

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
