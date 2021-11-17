/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace;

import nbbrd.service.ServiceDefinition;


/**
 *
 * @author Jean Palate
 */
@ServiceDefinition
public interface WorkspaceItemRepository<D> {
    
    Class<D> getSupportedType();
    
    boolean load(WorkspaceItem<D> item);

    boolean save(WorkspaceItem<D> item);

    boolean delete(WorkspaceItem<D> doc);
}
