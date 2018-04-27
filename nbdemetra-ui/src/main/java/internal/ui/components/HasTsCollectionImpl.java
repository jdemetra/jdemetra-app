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

import demetra.ui.TsManager;
import demetra.ui.beans.PropertyChangeSource;
import demetra.ui.components.HasTsCollection;
import ec.tss.TsCollection;
import ec.tss.TsMoniker;
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

    private TsCollection tsCollection = TsManager.getDefault().newTsCollection();
    private ListSelectionModel selectionModel = new DefaultListSelectionModel();
    private TsUpdateMode updateMode = DEFAULT_UPDATEMODE;
    private boolean freezeOnImport = DEFAULT_FREEZE_ON_IMPORT;
    private TsCollection dropContent = TsManager.getDefault().newTsCollection();

    public HasTsCollectionImpl register(TsManager manager) {
        manager.addWeakUpdateListener(this);
        return this;
    }

    @Override
    public TsCollection getTsCollection() {
        return tsCollection;
    }

    @Override
    public void setTsCollection(TsCollection collection) {
        TsCollection old = this.tsCollection;
        this.tsCollection = tsCollection != null ? tsCollection : TsManager.getDefault().newTsCollection();
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
        TsCollection old = this.dropContent;
        this.dropContent = dropContent != null ? dropContent : TsManager.getDefault().newTsCollection();
        broadcaster.firePropertyChange(DROP_CONTENT_PROPERTY, old, this.dropContent);
    }

    @Override
    public void accept(TsMoniker moniker) {
        TsCollection col = getTsCollection();
        if (moniker.equals(col.getMoniker()) || col.search(moniker) != null) {
            broadcaster.firePropertyChange(TS_COLLECTION_PROPERTY, null, col);
        }
    }
}
