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
package demetra.ui;

import demetra.timeseries.TsData;
import demetra.tsprovider.Ts;
import demetra.tsprovider.TsCollection;
import demetra.ui.beans.PropertyChangeSource;
import ec.nbdemetra.core.GlobalService;
import ec.nbdemetra.ui.ns.INamedService;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.util.various.swing.OnEDT;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openide.util.Lookup;

/**
 * A support class that deals with the clipboard. It allows the user to get/set
 * time series, collections, matrixes and tables from/to any transferable. The
 * actual conversion is done by TssTransferHandler.
 * <p>
 * Note that this class can be extended to modify its behavior.
 *
 * @author Philippe Charles
 */
@GlobalService
public interface DataTransfer extends PropertyChangeSource {

    /**
     * A convenient method to get the current single instance of
     * DataTransfer. You could use the default lookup to get the same
     * result.
     *
     * @return a non-null DataTransfer
     */
    @Nonnull
    static DataTransfer getDefault() {
        return Lookup.getDefault().lookup(DataTransfer.class);
    }

    String VALID_CLIPBOARD_PROPERTY = "validClipboard";

    @OnEDT
    boolean canImport(@Nonnull DataFlavor... dataFlavors);

    @OnEDT
    boolean canImport(@Nonnull Transferable transferable);

    /**
     * Creates a Transferable from a Matrix.
     *
     * @param matrix a non-null {@link Matrix}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @Nonnull
    Transferable fromMatrix(@Nonnull Matrix matrix);

    /**
     * Creates a Transferable from a Table.
     *
     * @param table a non-null {@link Table}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @Nonnull
    Transferable fromTable(@Nonnull Table<?> table);

    /**
     * Creates a Transferable from a time series.
     *
     * @param ts a non-null {@link Ts}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @Nonnull
    Transferable fromTs(@Nonnull Ts ts);

    /**
     * Creates a Transferable from a collection of time series.
     *
     * @param col a non-null {@link TsCollection}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @Nonnull
    Transferable fromTsCollection(@Nonnull TsCollection col);

    /**
     * Creates a Transferable from a TsData.
     *
     * @param data a non-null {@link TsData}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @Nonnull
    Transferable fromTsData(@Nonnull TsData data);

    /**
     * Checks if a transferable represents time series (to avoid useless loading
     * of Ts).
     *
     * @param transferable
     * @return
     */
    boolean isTssTransferable(@Nonnull Transferable transferable);

    /**
     * Checks if the clipboard currently contains data that can be imported.
     *
     * @return true if the data in the clipboard is importable; false otherwise
     */
    @OnEDT
    boolean isValidClipboard();

    /**
     * Retrieves a list of all available TssTransferHandler.
     *
     * @return a non-null stream of INamedService
     */
    @Nonnull
    List<? extends INamedService> getProviders();

    /**
     * Retrieves a Matrix from a transferable.
     *
     * @param transferable a non-null object
     * @return a {@link Matrix} if possible, <code>null</code> otherwise
     */
    @OnEDT
    @Nullable
    Matrix toMatrix(@Nonnull Transferable transferable);

    /**
     * Retrieves a Table from a transferable.
     *
     * @param transferable a non-null object
     * @return a {@link Table} if possible, <code>null</code> otherwise
     */
    @OnEDT
    @Nullable
    Table<?> toTable(@Nonnull Transferable transferable);

    /**
     * Retrieves a time series from a transferable.
     * <p>
     * Note that the content of this {@link Ts} might be asynchronous.
     * Therefore, you should call {@link Ts#load(ec.tss.TsInformationType)} or
     * {@link Ts#query(ec.tss.TsInformationType)} after this method.
     *
     * @param transferable a non-null object
     * @return a {@link Ts} if possible, <code>null</code> otherwise
     */
    @OnEDT
    @Nullable
    Ts toTs(@Nonnull Transferable transferable);

    /**
     * Retrieves a collection of time series from a transferable.
     * <p>
     * Note that the content of this {@link TsCollection} might be asynchronous.
     * Therefore, you should call
     * {@link TsCollection#load(ec.tss.TsInformationType)} or
     * {@link TsCollection#query(ec.tss.TsInformationType)} after this method.
     *
     * @param transferable a non-null object
     * @return a {@link TsCollection} if possible, <code>null</code> otherwise
     */
    @OnEDT
    @Nullable
    TsCollection toTsCollection(@Nonnull Transferable transferable);

    @OnEDT
    @Nonnull
    Stream<TsCollection> toTsCollectionStream(@Nonnull Transferable transferable);

    /**
     * Retrieves a TsData from a transferable.
     *
     * @param transferable a non-null object
     * @return a {@link TsData} if possible, <code>null</code> otherwise
     */
    @OnEDT
    @Nullable
    TsData toTsData(@Nonnull Transferable transferable);
}
