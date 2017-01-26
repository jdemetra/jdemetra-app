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

import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import ec.nbdemetra.core.GlobalService;
import ec.nbdemetra.ui.awt.ListenableBean;
import ec.tss.*;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.util.various.swing.OnEDT;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.MultiTransferObject;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(TssTransferSupport.class);

    /**
     * A convenient method to get the current single instance of
     * TssTransferSupport. You could use the default lookup to get the same
     * result.
     *
     * @return a non-null TssTransferSupport
     */
    @Nonnull
    public static TssTransferSupport getDefault() {
        return Lookup.getDefault().lookup(TssTransferSupport.class);
    }

    @Deprecated
    @Nonnull
    public static TssTransferSupport getInstance() {
        return getDefault();
    }

    private boolean validClipboard;

    public TssTransferSupport() {
        this.validClipboard = false;
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        systemClipboard.addFlavorListener(event -> {
            try {
                boolean old = validClipboard;
                validClipboard = canImport(((Clipboard) event.getSource()).getAvailableDataFlavors());
                firePropertyChange(VALID_CLIPBOARD_PROPERTY, old, validClipboard);
            } catch (Exception ex) {
                LOGGER.debug("While getting content from clipboard", ex);
            }
        });
        validClipboard = canImport(systemClipboard.getAvailableDataFlavors());
    }

    /**
     * Checks if the clipboard currently contains data that can be imported.
     *
     * @return true if the data in the clipboard is importable; false otherwise
     */
    public boolean isValidClipboard() {
        return validClipboard;
    }

    /**
     * Retrieves a list of all available TssTransferHandler.
     *
     * @return a non-null list of TssTransferHandler
     * @deprecated use {@link #streamAll()} instead
     */
    @Deprecated
    @Nonnull
    public FluentIterable<? extends TssTransferHandler> all() {
        return FluentIterable.from(Iterables.concat(Lookup.getDefault().lookupAll(TssTransferHandler.class), ATsCollectionFormatter.getLegacyHandlers()));
    }

    /**
     * Retrieves a list of all available TssTransferHandler.
     *
     * @return a non-null stream of TssTransferHandler
     */
    @Nonnull
    public Stream<? extends TssTransferHandler> stream() {
        return Stream.concat(Lookup.getDefault().lookupAll(TssTransferHandler.class).stream(), ATsCollectionFormatter.getLegacyHandlers().stream());
    }

    /**
     * Gets a mutable thread-safe list of formatters.<p>
     * Note that order matters.
     *
     * @return a non-null list of formatters
     * @deprecated use {@link #all()} instead
     */
    @Deprecated
    @Nonnull
    public List<ITsCollectionFormatter> getFormatters() {
        return new ArrayList<>();
    }

    @OnEDT
    public boolean canImport(@Nonnull DataFlavor... dataFlavors) {
        // multiFlavor means "maybe", not "yes"
        return DataTransfers.isMultiFlavor(dataFlavors) || stream().anyMatch(onDataFlavors(dataFlavors));
    }

    /**
     * Creates a Transferable from a TsData.
     *
     * @param data a non-null {@link TsData}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @Nonnull
    public Transferable fromTsData(@Nonnull TsData data) {
        Preconditions.checkNotNull(data);
        return fromTs(TsFactory.instance.createTs("", null, data));
    }

    /**
     * Creates a Transferable from a time series.
     *
     * @param ts a non-null {@link Ts}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @Nonnull
    public Transferable fromTs(@Nonnull Ts ts) {
        Preconditions.checkNotNull(ts);
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
    @Nonnull
    public Transferable fromTsCollection(@Nonnull TsCollection col) {
        Preconditions.checkNotNull(col);
        return asTransferable(col, stream(), TsCollectionHelper.INSTANCE);
    }

    /**
     * Creates a Transferable from a Matrix.
     *
     * @param matrix a non-null {@link Matrix}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @Nonnull
    public Transferable fromMatrix(@Nonnull Matrix matrix) {
        Preconditions.checkNotNull(matrix);
        return asTransferable(matrix, stream(), MatrixHelper.INSTANCE);
    }

    /**
     * Creates a Transferable from a Table.
     *
     * @param table a non-null {@link Table}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @Nonnull
    public Transferable fromTable(@Nonnull Table<?> table) {
        Preconditions.checkNotNull(table);
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
    public TsData toTsData(@Nonnull Transferable transferable) {
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
    public Ts toTs(@Nonnull Transferable transferable) {
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
    public TsCollection toTsCollection(@Nonnull Transferable transferable) {
        Preconditions.checkNotNull(transferable);
        Optional<MultiTransferObject> multi = DataTransfers.getMultiTransferObject(transferable);
        if (multi.isPresent()) {
            List<TssTransferHandler> all = stream().collect(Collectors.toList());
            return DataTransfers.asTransferableStream(multi.get())
                    .flatMap(o -> findTsCollection(all.stream(), o).map(TsCollection::stream).orElse(Stream.empty()))
                    .collect(TsFactory.toTsCollection());
        }
        return findTsCollection(stream(), transferable).orElse(null);
    }

    /**
     * Retrieves a Matrix from a transferable.
     *
     * @param transferable a non-null object
     * @return a {@link Matrix} if possible, <code>null</code> otherwise
     */
    @OnEDT
    @Nullable
    public Matrix toMatrix(@Nonnull Transferable transferable) {
        Preconditions.checkNotNull(transferable);
        return stream()
                .filter(onDataFlavors(transferable.getTransferDataFlavors()))
                .map(o -> toMatrix(o, transferable))
                .filter(o -> o != null)
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
    public Table<?> toTable(@Nonnull Transferable transferable) {
        Preconditions.checkNotNull(transferable);
        return stream()
                .filter(onDataFlavors(transferable.getTransferDataFlavors()))
                .map(o -> toTable(o, transferable))
                .findFirst()
                .orElse(null);
    }

    @Deprecated
    public static boolean isMultiFlavor(@Nonnull DataFlavor[] dataFlavors) {
        return DataTransfers.isMultiFlavor(dataFlavors);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static Optional<TsCollection> findTsCollection(Stream<? extends TssTransferHandler> stream, Transferable t) {
        return stream
                .filter(onDataFlavors(t.getTransferDataFlavors()))
                .map(o -> importTsCollection(o, t))
                .filter(o -> o != null)
                .findFirst();
    }

    private static TsCollection importTsCollection(TssTransferHandler o, Transferable t) {
        try {
            Object transferData = t.getTransferData(o.getDataFlavor());
            if (o.canImportTsCollection(transferData)) {
                LOGGER.debug("Getting collection using '{}'", o.getName());
                return o.importTsCollection(transferData);
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            LOGGER.error("While getting collection using '" + o.getName() + "'", ex);
        }
        return null;
    }

    private static Matrix toMatrix(TssTransferHandler o, Transferable t) {
        try {
            Object transferData = t.getTransferData(o.getDataFlavor());
            if (o.canImportMatrix(transferData)) {
                LOGGER.debug("Getting matrix using '{}'", o.getName());
                return o.importMatrix(transferData);
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            LOGGER.error("While getting matrix using '" + o.getName() + "'", ex);
        }
        return null;
    }

    private static Table<?> toTable(TssTransferHandler o, Transferable t) {
        try {
            Object transferData = t.getTransferData(o.getDataFlavor());
            if (o.canImportTable(transferData)) {
                LOGGER.debug("Getting table using '{}'", o.getName());
                return o.importTable(transferData);
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            LOGGER.error("While getting table using '" + o.getName() + "'", ex);
        }
        return null;
    }

    private static <T> Transferable asTransferable(T data, Stream<? extends TssTransferHandler> allHandlers, TransferHelper<T> helper) {
        return new CustomAdapter(data, allHandlers, helper);
    }

    private static final class CustomAdapter<T> implements Transferable {

        private final T data;
        private final Map<DataFlavor, TssTransferHandler> handlers;
        private final TransferHelper<T> helper;
        private final Map<DataFlavor, Object> cache;

        CustomAdapter(T data, Stream<? extends TssTransferHandler> allHandlers, TransferHelper<T> helper) {
            this.data = data;
            this.handlers = new HashMap<>();
            allHandlers
                    .filter(o -> helper.canTransferData(data, o))
                    .forEach(o -> {
                        if (!handlers.containsKey(o.getDataFlavor())) {
                            handlers.put(o.getDataFlavor(), o);
                        }
                    });
            this.helper = helper;
            this.cache = new HashMap<>();
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return Iterables.toArray(handlers.keySet(), DataFlavor.class);
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return handlers.containsKey(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            Object result = cache.get(flavor);
            if (result == null) {
                TssTransferHandler handler = handlers.get(flavor);
                if (handler == null) {
                    throw new UnsupportedFlavorException(flavor);
                }
                LOGGER.debug("Getting transfer data using '{}'", handler.getName());
                result = helper.getTransferData(data, handler);
                cache.put(flavor, result);
            }
            return result;
        }
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
        final Set<DataFlavor> list = Sets.newHashSet(dataFlavors);
        return o -> list.contains((DataFlavor) (o != null ? o.getDataFlavor() : null));
    }
    //</editor-fold>
}
