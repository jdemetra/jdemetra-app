/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.ws;

import demetra.tsprovider.DataSource;
import ec.tstoolkit.design.ServiceDefinition;
import java.util.Collection;

/**
 *
 * @author Jean Palate
 * @since 1.0.0
 */
@ServiceDefinition
public interface IWorkspaceRepository {

    Collection<Class> getSupportedTypes();

    void initialize();

    String getName();

    default Object getProperties() {
        return null;
    }

    default void setProperties() {
    }

    Workspace open();

    boolean saveAs(Workspace ws);

    boolean load(Workspace ws);

    boolean save(Workspace ws, boolean force);

    boolean delete(Workspace ws);

    <D> boolean canHandleItem(Class<D> dclass);

    boolean loadItem(WorkspaceItem<?> item);

    boolean saveItem(WorkspaceItem<?> item);

    boolean deleteItem(WorkspaceItem<?> item);

    void close(Workspace ws_);

    DataSource getDefaultDataSource();
}
