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
package internal.ui.components.parts;

import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.ui.TsEvent;
import demetra.ui.TsListener;
import demetra.ui.beans.PropertyChangeBroadcaster;
import demetra.ui.components.parts.HasTsCollection;
import java.util.List;
import java.util.Objects;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import demetra.ui.TsManager;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class HasTsCollectionImpl implements HasTsCollection, TsListener {

    @lombok.NonNull
    private final PropertyChangeBroadcaster broadcaster;

    TsCollection tsCollection = TsCollection.EMPTY;
    ListSelectionModel selectionModel = new DefaultListSelectionModel();
    TsUpdateMode updateMode = DEFAULT_UPDATEMODE;
    boolean freezeOnImport = DEFAULT_FREEZE_ON_IMPORT;
    TsCollection dropContent = TsCollection.EMPTY;

    public HasTsCollectionImpl register(TsManager manager) {
        manager.addWeakListener(this);
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
    public void tsUpdated(TsEvent event) {
        TsCollection col = getTsCollection();
        if (event.getMoniker().equals(col.getMoniker())) {
            TsCollection newData = event.getSource().makeTsCollection(event.getMoniker(), col.getType());
            setTsCollection(newData);
        } else {
            int index = col.indexOf(ts -> ts.getMoniker().equals(event.getMoniker()));
            if (index != -1) {
                Ts oldData = col.get(index);
                Ts newData = event.getSource().makeTs(oldData.getMoniker(), oldData.getType());
                List<Ts> list = col.toList();
                list.set(index, newData);
                setTsCollection(col.toBuilder().clearItems().items(list).build());
            }
        }
    }
}
