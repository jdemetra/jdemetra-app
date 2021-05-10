/*
 * Copyright 2018 National Bank of Belgium
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
package internal.ui.components;

import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsMoniker;
import demetra.ui.NextTsManager;
import demetra.ui.TsManager;
import demetra.ui.beans.PropertyChangeSource;
import demetra.ui.components.HasTsCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class HasTsCollectionImpl implements HasTsCollection, TsManager.UpdateListener {

    @lombok.NonNull
    private final PropertyChangeSource.Broadcaster broadcaster;

    private TsCollection tsCollection = TsCollection.EMPTY;
    private ListSelectionModel selectionModel = new DefaultListSelectionModel();
    private TsUpdateMode updateMode = DEFAULT_UPDATEMODE;
    private boolean freezeOnImport = DEFAULT_FREEZE_ON_IMPORT;
    private TsCollection dropContent = TsCollection.EMPTY;

    public HasTsCollectionImpl register(NextTsManager manager) {
        manager.addWeakUpdateListener(this);
        return this;
    }

    @Override
    public TsCollection getTsCollection() {
        return tsCollection;
    }

    @Override
    public void setTsCollection(TsCollection tsCollection) {
        Objects.requireNonNull(tsCollection);
        TsCollection old = this.tsCollection;
        this.tsCollection = tsCollection;
        broadcaster.firePropertyChange(TS_COLLECTION_PROPERTY, old, this.tsCollection);
    }

    @Override
    public ListSelectionModel getTsSelectionModel() {
        return selectionModel;
    }

    @Override
    public void setTsSelectionModel(ListSelectionModel selectionModel) {
        ListSelectionModel old = this.selectionModel;
        this.selectionModel = selectionModel != null ? selectionModel : new DefaultListSelectionModel();
        broadcaster.firePropertyChange(TS_SELECTION_MODEL_PROPERTY, old, this.selectionModel);
    }

    @Override
    public TsUpdateMode getTsUpdateMode() {
        return updateMode;
    }

    @Override
    public void setTsUpdateMode(TsUpdateMode updateMode) {
        TsUpdateMode old = this.updateMode;
        this.updateMode = updateMode != null ? updateMode : DEFAULT_UPDATEMODE;
        broadcaster.firePropertyChange(UDPATE_MODE_PROPERTY, old, this.updateMode);
    }

    @Override
    public boolean isFreezeOnImport() {
        return freezeOnImport;
    }

    @Override
    public void setFreezeOnImport(boolean freezeOnImport) {
        boolean old = this.freezeOnImport;
        this.freezeOnImport = freezeOnImport;
        broadcaster.firePropertyChange(FREEZE_ON_IMPORT_PROPERTY, old, this.freezeOnImport);
    }

    @Override
    public TsCollection getDropContent() {
        return dropContent;
    }

    @Override
    public void setDropContent(TsCollection dropContent) {
        Objects.requireNonNull(dropContent);
        TsCollection old = this.dropContent;
        this.dropContent = dropContent;
        broadcaster.firePropertyChange(DROP_CONTENT_PROPERTY, old, this.dropContent);
    }

    @Override
    public void accept(NextTsManager manager, TsMoniker moniker) {
        TsCollection col = getTsCollection();
        if (moniker.equals(col.getMoniker())) {
            TsCollection newData = manager.lookupTsCollection2(col.getName(), moniker, col.getType());
            setTsCollection(newData);
        } else {
            int index = indexOf(col.getData(), moniker);
            if (index != -1) {
                Ts newData = manager.lookupTs2(moniker);
                List<Ts> list = new ArrayList<>(col.getData());
                list.set(index, newData);
                setTsCollection(col.toBuilder().clearData().data(list).build());
            }
        }
    }

    private static int indexOf(List<Ts> list, TsMoniker moniker) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMoniker().equals(moniker)) {
                return i;
            }
        }
        return -1;
    }
}
