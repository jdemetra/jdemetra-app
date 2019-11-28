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
package ec.tss.datatransfer;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import ec.nbdemetra.core.GlobalService;
import ec.nbdemetra.ui.awt.ListenableBean;
import ec.tss.*;
import ec.tss.datatransfer.impl.LocalObjectTssTransferHandler;
import ec.tss.tsproviders.utils.FunctionWithIO;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.design.VisibleForTesting;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.util.various.swing.OnEDT;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.*;
import static java.util.Objects.requireNonNull;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@ServiceProvider(service = TssTransferSupport.class)
public class TssTransferSupport extends ListenableBean {

    public static final String VALID_CLIPBOARD_PROPERTY = "validClipboard";

    /**
     * A convenient method to get the current single instance of
     * TssTransferSupport. You could use the default lookup to get the same
     * result.
     *
     * @return a non-null TssTransferSupport
     */
    @NonNull
    public static TssTransferSupport getDefault() {
        return Lookup.getDefault().lookup(TssTransferSupport.class);
    }

    @Deprecated
    @NonNull
    public static TssTransferSupport getInstance() {
        return getDefault();
    }

    private final ClipboardValidator clipboardValidator;
    private final Lookup lookup;
    private final Logger logger;
    private boolean validClipboard;

    public TssTransferSupport() {
        this(Lookup.getDefault(), LoggerFactory.getLogger(TssTransferSupport.class), false);
        clipboardValidator.register(Toolkit.getDefaultToolkit().getSystemClipboard());
    }

    @VisibleForTesting
    TssTransferSupport(Lookup lookup, Logger logger, boolean validClipboard) {
        this.clipboardValidator = new ClipboardValidator();
        this.lookup = lookup;
        this.logger = logger;
        this.validClipboard = validClipboard;
    }

    /**
     * Checks if the clipboard currently contains data that can be imported.
     *
     * @return true if the data in the clipboard is importable; false otherwise
     */
    @OnEDT
    public boolean isValidClipboard() {
        return validClipboard;
    }

    private void setValidClipboard(boolean validClipboard) {
        boolean old = this.validClipboard;
        this.validClipboard = validClipboard;
        firePropertyChange(VALID_CLIPBOARD_PROPERTY, old, this.validClipboard);
    }

    /**
     * Retrieves a list of all available TssTransferHandler.
     *
     * @return a non-null list of TssTransferHandler
     * @deprecated use {@link #streamAll()} instead
     */
    @Deprecated
    @NonNull
    public FluentIterable<? extends TssTransferHandler> all() {
        return FluentIterable.from(Iterables.concat(lookup.lookupAll(TssTransferHandler.class), ATsCollectionFormatter.getLegacyHandlers()));
    }

    /**
     * Retrieves a list of all available TssTransferHandler.
     *
     * @return a non-null stream of TssTransferHandler
     */
    @NonNull
    public Stream<? extends TssTransferHandler> stream() {
        return Stream.concat(lookup.lookupAll(TssTransferHandler.class).stream(), ATsCollectionFormatter.getLegacyHandlers().stream());
    }

    /**
     * Gets a mutable thread-safe list of formatters.<p>
     * Note that order matters.
     *
     * @return a non-null list of formatters
     * @deprecated use {@link #all()} instead
     */
    @Deprecated
    @NonNull
    public List<ITsCollectionFormatter> getFormatters() {
        return new ArrayList<>();
    }

    @OnEDT
    public boolean canImport(@NonNull DataFlavor... dataFlavors) {
        // multiFlavor means "maybe", not "yes"
        return DataTransfers.isMultiFlavor(dataFlavors) || stream().anyMatch(onDataFlavors(dataFlavors));
    }

    @OnEDT
    public boolean canImport(@NonNull Transferable transferable) {
        Set<DataFlavor> dataFlavors = DataTransfers.getMultiDataFlavors(transferable).collect(Collectors.toSet());
        return stream().anyMatch(onDataFlavors(dataFlavors));
    }

    /**
     * Creates a Transferable from a TsData.
     *
     * @param data a non-null {@link TsData}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @NonNull
    public Transferable fromTsData(@NonNull TsData data) {
        Objects.requireNonNull(data);
        return fromTs(TsFactory.instance.createTs("", null, data));
    }

    /**
     * Creates a Transferable from a time series.
     *
     * @param ts a non-null {@link Ts}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @NonNull
    public Transferable fromTs(@NonNull Ts ts) {
        Objects.requireNonNull(ts);
        TsCollection col = TsFactory.instance.createTsCollection();
        col.quietAdd(ts);
        return fromTsCollection(col);
    }

    /**
     * Creates a Transferable from a collection of time series.
     *
     * @param col a non-null {@link TsCollection}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @NonNull
    public Transferable fromTsCollection(@NonNull TsCollection col) {
        Objects.requireNonNull(col);
        return asTransferable(col, stream(), TsCollectionHelper.INSTANCE);
    }

    /**
     * Creates a Transferable from a Matrix.
     *
     * @param matrix a non-null {@link Matrix}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @NonNull
    public Transferable fromMatrix(@NonNull Matrix matrix) {
        Objects.requireNonNull(matrix);
        return asTransferable(matrix, stream(), MatrixHelper.INSTANCE);
    }

    /**
     * Creates a Transferable from a Table.
     *
     * @param table a non-null {@link Table}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @NonNull
    public Transferable fromTable(@NonNull Table<?> table) {
        Objects.requireNonNull(table);
        return asTransferable(table, stream(), TableHelper.INSTANCE);
    }

    /**
     * Retrieves a TsData from a transferable.
     *
     * @param transferable a non-null object
     * @return a {@link TsData} if possible, <code>null</code> otherwise
     */
    @OnEDT
    @Nullable
    public TsData toTsData(@NonNull Transferable transferable) {
        Ts ts = toTs(transferable);
        if (ts != null) {
            ts.load(TsInformationType.Data);
            if (ts.hasData() == TsStatus.Valid) {
                return ts.getTsData();
            }
        }
        return null;
    }

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
    public Ts toTs(@NonNull Transferable transferable) {
        TsCollection col = toTsCollection(transferable);
        return col != null && !col.isEmpty() ? col.get(0) : null;
    }

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
    public TsCollection toTsCollection(@NonNull Transferable transferable) {
        Objects.requireNonNull(transferable);
        return stream()
                .filter(onDataFlavors(transferable.getTransferDataFlavors()))
                .map(o -> toTsCollection(o, transferable, logger))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @OnEDT
    @NonNull
    public Stream<TsCollection> toTsCollectionStream(@NonNull Transferable transferable) {
        return DataTransfers.getMultiTransferables(transferable)
                .map(this::toTsCollection)
                .filter(Objects::nonNull);
    }

    /**
     * Retrieves a Matrix from a transferable.
     *
     * @param transferable a non-null object
     * @return a {@link Matrix} if possible, <code>null</code> otherwise
     */
    @OnEDT
    @Nullable
    public Matrix toMatrix(@NonNull Transferable transferable) {
        Objects.requireNonNull(transferable);
        return stream()
                .filter(onDataFlavors(transferable.getTransferDataFlavors()))
                .map(o -> toMatrix(o, transferable, logger))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a Table from a transferable.
     *
     * @param transferable a non-null object
     * @return a {@link Table} if possible, <code>null</code> otherwise
     */
    @OnEDT
    @Nullable
    public Table<?> toTable(@NonNull Transferable transferable) {
        Objects.requireNonNull(transferable);
        return stream()
                .filter(onDataFlavors(transferable.getTransferDataFlavors()))
                .map(o -> toTable(o, transferable, logger))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if a transferable represents time series (to avoid useless loading
     * of Ts).
     *
     * @param transferable
     * @return
     */
    public boolean isTssTransferable(@NonNull Transferable transferable) {
        return transferable.isDataFlavorSupported(LocalObjectTssTransferHandler.DATA_FLAVOR);
    }

    @Deprecated
    public static boolean isMultiFlavor(@NonNull DataFlavor[] dataFlavors) {
        return DataTransfers.isMultiFlavor(dataFlavors);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private final class ClipboardValidator implements FlavorListener {

        private boolean isValid(Clipboard clipboard) {
            try {
                return canImport(clipboard.getAvailableDataFlavors());
            } catch (IllegalStateException ex) {
                logger.debug("While getting content from clipboard", ex);
                return true; // means "maybe", not "yes"
            }
        }

        @Override
        public void flavorsChanged(FlavorEvent e) {
            setValidClipboard(isValid((Clipboard) e.getSource()));
        }

        public void register(Clipboard clipboard) {
            clipboard.addFlavorListener(this);
            validClipboard = isValid(clipboard);
        }
    }

    private static void logUnexpected(Logger logger, TssTransferHandler o, RuntimeException unexpected, String context) {
        logger.info("Unexpected exception while " + context + " using '" + getIdOrClassName(o) + "'", unexpected);
    }

    private static void logExpected(Logger logger, TssTransferHandler o, Exception expected, String context) {
        logger.debug("While " + context + " using '" + getIdOrClassName(o) + "'", expected);
    }

    private static String getIdOrClassName(TssTransferHandler handler) {
        try {
            return requireNonNull(handler.getName());
        } catch (RuntimeException unexpected) {
            return handler.getClass().getName();
        }
    }

    private static DataFlavor getDataFlavorOrNull(TssTransferHandler handler) {
        try {
            return handler.getDataFlavor();
        } catch (RuntimeException unexpected) {
            return null;
        }
    }

    private static TsCollection toTsCollection(TssTransferHandler o, Transferable t, Logger logger) {
        try {
            Object data = t.getTransferData(requireNonNull(o.getDataFlavor()));
            if (o.canImportTsCollection(data)) {
                return requireNonNull(o.importTsCollection(data));
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            logExpected(logger, o, ex, "getting collection");
        } catch (RuntimeException ex) {
            logUnexpected(logger, o, ex, "getting collection");
        }
        return null;
    }

    private static Matrix toMatrix(TssTransferHandler o, Transferable t, Logger logger) {
        try {
            Object data = t.getTransferData(requireNonNull(o.getDataFlavor()));
            if (o.canImportMatrix(data)) {
                return requireNonNull(o.importMatrix(data));
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            logExpected(logger, o, ex, "getting matrix");
        } catch (RuntimeException ex) {
            logUnexpected(logger, o, ex, "getting matrix");
        }
        return null;
    }

    private static Table<?> toTable(TssTransferHandler o, Transferable t, Logger logger) {
        try {
            Object data = t.getTransferData(requireNonNull(o.getDataFlavor()));
            if (o.canImportTable(data)) {
                return requireNonNull(o.importTable(data));
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            logExpected(logger, o, ex, "getting table");
        } catch (RuntimeException ex) {
            logUnexpected(logger, o, ex, "getting table");
        }
        return null;
    }

    @OnEDT
    private static <T> Transferable asTransferable(T data, Stream<? extends TssTransferHandler> allHandlers, TransferHelper<T> helper) {
        return new DataTransfers.CustomAdapter<>(
                getHandlersByFlavor(data, allHandlers, helper),
                getTransferDataLoader(data, helper));
    }

    private static <T> Map<DataFlavor, List<TssTransferHandler>> getHandlersByFlavor(T data, Stream<? extends TssTransferHandler> allHandlers, TransferHelper<T> helper) {
        return allHandlers
                .filter(o -> helper.canTransferData(data, o))
                .collect(Collectors.groupingBy(TssTransferSupport::getDataFlavorOrNull));
    }

    private static <T> FunctionWithIO<TssTransferHandler, Object> getTransferDataLoader(T data, TransferHelper<T> helper) {
        return o -> helper.getTransferData(data, o);
    }

    private interface TransferHelper<T> {

        boolean canTransferData(T data, TssTransferHandler handler);

        Object getTransferData(T data, TssTransferHandler handler) throws IOException;
    }

    private static final class TsCollectionHelper implements TransferHelper<TsCollection> {

        private static final TsCollectionHelper INSTANCE = new TsCollectionHelper();

        @Override
        public boolean canTransferData(TsCollection data, TssTransferHandler handler) {
            return handler.canExportTsCollection(data);
        }

        @Override
        public Object getTransferData(TsCollection data, TssTransferHandler handler) throws IOException {
            return handler.exportTsCollection(data);
        }
    }

    private static final class MatrixHelper implements TransferHelper<Matrix> {

        private static final MatrixHelper INSTANCE = new MatrixHelper();

        @Override
        public boolean canTransferData(Matrix data, TssTransferHandler handler) {
            return handler.canExportMatrix(data);
        }

        @Override
        public Object getTransferData(Matrix data, TssTransferHandler handler) throws IOException {
            return handler.exportMatrix(data);
        }
    }

    private static final class TableHelper implements TransferHelper<Table<?>> {

        private static final TableHelper INSTANCE = new TableHelper();

        @Override
        public boolean canTransferData(Table<?> data, TssTransferHandler handler) {
            return handler.canExportTable(data);
        }

        @Override
        public Object getTransferData(Table<?> data, TssTransferHandler handler) throws IOException {
            return handler.exportTable(data);
        }
    }

    private static Predicate<TssTransferHandler> onDataFlavors(DataFlavor[] dataFlavors) {
        return onDataFlavors(Sets.newHashSet(dataFlavors));
    }

    private static Predicate<TssTransferHandler> onDataFlavors(Set<DataFlavor> dataFlavors) {
        return o -> dataFlavors.contains((DataFlavor) (o != null ? getDataFlavorOrNull(o) : null));
    }
    //</editor-fold>
}
