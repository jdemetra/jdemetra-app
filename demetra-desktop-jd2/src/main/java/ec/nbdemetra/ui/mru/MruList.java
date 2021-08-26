/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.nbdemetra.ui.mru;

import demetra.ui.beans.PropertyChangeSource;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Philippe Charles
 */
public final class MruList implements PropertyChangeSource.WithWeakListeners, Iterable<SourceId> {

    public static final String CONTENT_PROPERTY = "123";
    private static final MruList PROVIDERS = new MruList();
    private static final MruList WORKSPACES = new MruList();

    public static MruList getProvidersInstance() {
        return PROVIDERS;
    }

    public static MruList getWorkspacesInstance() {
        return WORKSPACES;
    }

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    private final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);

    //
    private final List<SourceId> list;
    private final int maxSize;

    private MruList() {
        maxSize = 9; // default is 9
        list = new ArrayList<>(maxSize);
    }

    public void clear() {
        list.clear();
        broadcaster.firePropertyChange(CONTENT_PROPERTY, null, list);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void add(SourceId item) {
        // remove the old
        list.remove(item);

        // add to the top
        list.add(0, item);
        while (list.size() > maxSize) {
            list.remove(list.size() - 1);
        }
        broadcaster.firePropertyChange(CONTENT_PROPERTY, null, list);
    }

    @Override
    public Iterator<SourceId> iterator() {
        return list.iterator();
    }
}
