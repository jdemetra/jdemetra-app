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
package demetra.desktop.components.parts;

import demetra.desktop.TsManager;
import demetra.desktop.design.SwingAction;
import demetra.desktop.design.SwingProperty;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsInformationType;
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

    void setTsCollection(@Nullable TsCollection tsCollection);

    default void loadAsync(TsInformationType info) {
        TsCollection tss = getTsCollection();
        if (tss != null && tss.stream().filter(s -> !s.getType().encompass(info)).count() > 0) {
            TsManager.getDefault().loadAsync(tss, info, this::replaceTsCollection);
        }
//        if (tss != null) {
//            if (tss.getMoniker().isProvided())
//                if (!tss.getType().encompass(info))
//                    TsManager.getDefault().loadAsync(tss, info, this::replaceTsCollection);
//            else{
//                if (tss.stream().filter(s->s.getType().encompass(info)).count() ==tss.size())
//                    return;
//                TsCollection.Builder newData = tss.toBuilder().clearItems();
//                for (Ts ts : tss) {
//                    TsManager.getDefault().loadAsync(ts, info, s->replaceTs(ts, s));
//                 }
//            }
//        }
    }

    default void replaceTs(Ts oldTs, Ts newTs) {
        TsCollection coll = getTsCollection();
        TsCollection.Builder builder = coll.toBuilder()
                .clearItems();
        for (Ts s : coll) {
            if (s == oldTs) {
                builder.item(newTs);
            } else {
                builder.item(s);
            }
        }
        setTsCollection(builder.build());

    }

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

    void setDropContent(@Nullable TsCollection dropContent);

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

    @SwingAction
    String COPY_ALL_ACTION = "copyAll";

    @SwingAction
    String RENAME_ACTION = "rename";

    @SwingAction
    String OPEN_ACTION = "open";

    @SwingAction
    String COPY_ACTION = "copy";

    @SwingAction
    String PASTE_ACTION = "paste";

    @SwingAction
    String DELETE_ACTION = "delete";

    @SwingAction
    String CLEAR_ACTION = "clear";

    @SwingAction
    String SELECT_ALL_ACTION = "selectAll";

    @SwingAction
    String FREEZE_ACTION = "freeze";

    @SwingAction
    String SPLIT_ACTION = "split";
}
