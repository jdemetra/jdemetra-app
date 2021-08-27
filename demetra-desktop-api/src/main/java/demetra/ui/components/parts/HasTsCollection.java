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
import demetra.ui.design.SwingProperty;
import ec.util.list.swing.JLists;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Philippe Charles
 */
public interface HasTsCollection {

    @SwingProperty
    String TS_COLLECTION_PROPERTY = "tsCollection";

    @NonNull
    TsCollection getTsCollection();

    void setTsCollection(@NonNull TsCollection tsCollection);

    @SwingProperty
    String TS_SELECTION_MODEL_PROPERTY = "tsSelectionModel";

    @NonNull
    ListSelectionModel getTsSelectionModel();

    void setTsSelectionModel(@Nullable ListSelectionModel selectionModel);

    @SwingProperty
    String TS_UPDATE_MODE_PROPERTY = "tsUpdateMode";

    @NonNull
    TsUpdateMode getTsUpdateMode();

    void setTsUpdateMode(@Nullable TsUpdateMode updateMode);

    @SwingProperty
    String FREEZE_ON_IMPORT_PROPERTY = "freezeOnImport";

    boolean isFreezeOnImport();

    void setFreezeOnImport(boolean freezeOnImport);

    @SwingProperty
    String DROP_CONTENT_PROPERTY = "dropContent";

    @NonNull
    TsCollection getDropContent();

    void setDropContent(@NonNull TsCollection dropContent);

    @NonNull
    default IntStream getTsSelectionIndexStream() {
        int size = getTsCollection().size();
        return JLists
                .getSelectionIndexStream(getTsSelectionModel())
                .filter(o -> o < size);
    }

    @NonNull
    default Stream<Ts> getTsSelectionStream() {
        TsCollection col = getTsCollection();
        return JLists
                .getSelectionIndexStream(getTsSelectionModel())
                .filter(o -> o < col.size())
                .mapToObj(col::get);
    }

    default void replaceTsCollection(@NonNull TsCollection tsCollection) {
        setTsCollection(getTsCollection().replaceAll(tsCollection));
    }

    enum TsUpdateMode {

        None, Single, Replace, Append;

        public boolean isReadOnly() {
            return this == None;
        }
    }

    String COPY_ALL_ACTION = "copyAll";
    String RENAME_ACTION = "rename";
    String OPEN_ACTION = "open";
    String COPY_ACTION = "copy";
    String PASTE_ACTION = "paste";
    String DELETE_ACTION = "delete";
    String CLEAR_ACTION = "clear";
    String SELECT_ALL_ACTION = "selectAll";
    String FREEZE_ACTION = "freeze";
    String SPLIT_ACTION = "splitIntoYearlyComponents";
}
