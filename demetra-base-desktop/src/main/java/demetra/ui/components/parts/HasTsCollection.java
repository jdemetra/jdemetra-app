/*
 * Copyright 2015 National Bank of Belgium
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
package demetra.ui.components.parts;

import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsSeq;
import demetra.ui.NextTsManager;
import demetra.ui.beans.PropertyChangeBroadcaster;
import ec.util.list.swing.JLists;
import internal.ui.components.parts.HasTsCollectionImpl;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Philippe Charles
 */
public interface HasTsCollection {

    static final String TS_COLLECTION_PROPERTY = "tsCollection";

    @NonNull
    TsCollection getTsCollection();

    void setTsCollection(@NonNull TsCollection tsCollection);

    static final String TS_SELECTION_MODEL_PROPERTY = "tsSelectionModel";

    @NonNull
    ListSelectionModel getTsSelectionModel();

    void setTsSelectionModel(@Nullable ListSelectionModel selectionModel);

    public enum TsUpdateMode {

        None, Single, Replace, Append;

        public boolean isReadOnly() {
            return this == None;
        }
    }

    static final String UDPATE_MODE_PROPERTY = "tsUpdateMode";
    static final TsUpdateMode DEFAULT_UPDATEMODE = TsUpdateMode.Append;

    @NonNull
    TsUpdateMode getTsUpdateMode();

    void setTsUpdateMode(@Nullable TsUpdateMode updateMode);

    static final String FREEZE_ON_IMPORT_PROPERTY = "freezeOnImport";
    static final boolean DEFAULT_FREEZE_ON_IMPORT = false;

    boolean isFreezeOnImport();

    void setFreezeOnImport(boolean freezeOnImport);

    static final String DROP_CONTENT_PROPERTY = "dropContent";

    @NonNull
    TsCollection getDropContent();

    void setDropContent(@NonNull TsCollection dropContent);

    @NonNull
    default IntStream getTsSelectionIndexStream() {
        int size = getTsCollection().getData().size();
        return JLists
                .getSelectionIndexStream(getTsSelectionModel())
                .filter(o -> o < size);
    }

    @NonNull
    default Stream<Ts> getTsSelectionStream() {
        TsSeq tss = getTsCollection().getData();
        return JLists
                .getSelectionIndexStream(getTsSelectionModel())
                .filter(o -> o < tss.size())
                .mapToObj(tss::get);
    }

    @NonNull
    static HasTsCollection of(@NonNull PropertyChangeBroadcaster broadcaster, @NonNull NextTsManager manager) {
        return new HasTsCollectionImpl(broadcaster).register(manager);
    }
}
