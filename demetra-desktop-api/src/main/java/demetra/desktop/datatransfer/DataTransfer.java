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
package demetra.desktop.datatransfer;

import demetra.desktop.TsManager;
import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.design.GlobalService;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.util.CollectionSupplier;
import demetra.desktop.util.LazyGlobalService;
import demetra.timeseries.*;
import demetra.util.Table;
import ec.util.various.swing.OnEDT;
import nbbrd.design.VisibleForTesting;
import nbbrd.io.function.IOFunction;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.*;
import java.awt.datatransfer.*;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import demetra.math.matrices.Matrix;

/**
 * A support class that deals with the clipboard. It allows the user to get/set
 * time series, collections, matrices and tables from/to any transferable. The
 * actual conversion is done by TssTransferHandler.
 *
 * @author Philippe Charles
 */
@lombok.extern.java.Log
@GlobalService
public final class DataTransfer implements PropertyChangeSource.WithWeakListeners {

    @NonNull
    public static DataTransfer getDefault() {
        return LazyGlobalService.get(DataTransfer.class, DataTransfer::new);
    }

    @SwingProperty
    public static final String VALID_CLIPBOARD_PROPERTY = "validClipboard";

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    private final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);

    private final ClipboardValidator clipboardValidator;
    private final CollectionSupplier<DataTransferSpi> providers;
    private final Logger logger;
    private boolean validClipboard;

    private DataTransfer() {
        this(DataTransferSpiLoader::get, log, false);
        clipboardValidator.register(Toolkit.getDefaultToolkit().getSystemClipboard());
    }

    @VisibleForTesting
    DataTransfer(CollectionSupplier<DataTransferSpi> providers, Logger logger, boolean validClipboard) {
        this.clipboardValidator = new ClipboardValidator();
        this.providers = providers;
        this.logger = logger;
        this.validClipboard = validClipboard;
    }

    private void setValidClipboard(boolean validClipboard) {
        boolean old = this.validClipboard;
        this.validClipboard = validClipboard;
        broadcaster.firePropertyChange(VALID_CLIPBOARD_PROPERTY, old, this.validClipboard);
    }

    @NonNull
    public Collection<? extends DataTransferSpi> getProviders() {
        return providers.get();
    }

    @OnEDT
    public boolean canImport(@NonNull DataFlavor... dataFlavors) {
        // multiFlavor means "maybe", not "yes"
        return DataTransfers.isMultiFlavor(dataFlavors) || providers.stream().anyMatch(onDataFlavors(dataFlavors));
    }

    @OnEDT
    public boolean canImport(@NonNull Transferable transferable) {
        Set<DataFlavor> dataFlavors = DataTransfers.getMultiDataFlavors(transferable).collect(Collectors.toSet());
        return providers.stream().anyMatch(onDataFlavors(dataFlavors));
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
        requireNonNull(ts);
        return fromTsCollection(TsCollection.of(Collections.singletonList(ts)));
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
        requireNonNull(col);
        return asTransferable(col, providers.stream(), TsCollectionHelper.INSTANCE);
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
        requireNonNull(data);
        return fromTs(Ts.builder().data(data).build());
    }

    /**
     * Creates a Transferable from a matrix.
     *
     * @param data a non-null {@link Matrix}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @NonNull
    public Transferable fromMatrix(@NonNull Matrix data) {
        requireNonNull(data);
        return asTransferable(data, providers.stream(), MatrixHelper.INSTANCE);
    }

    /**
     * Creates a Transferable from a Table.
     *
     * @param data a non-null {@link Table}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @NonNull
    public Transferable fromTable(@NonNull Table<?> data) {
        requireNonNull(data);
        return asTransferable(data, providers.stream(), TableHelper.INSTANCE);
    }

    /**
     * Checks if a {@link Transferable} represents time series (to avoid useless loading
     * of Ts).
     *
     * @param transferable
     * @return
     */
    public boolean isTssTransferable(@NonNull Transferable transferable) {
        return transferable.isDataFlavorSupported(LocalObjectDataTransfer.DATA_FLAVOR);
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

    /**
     * Retrieves a time series from a {@link Transferable}.
     * <p>
     * Note that the content of this {@link Ts} might be asynchronous.
     * Therefore, you should call {@link Ts#load(TsInformationType, TsFactory)} after this method.
     *
     * @param transferable a non-null object
     * @return an optional {@link Ts}
     */
    @OnEDT
    @NonNull
    public Optional<Ts> toTs(@NonNull Transferable transferable) {
        return toTsCollection(transferable)
                .map(TsCollection::getItems)
                .filter(o -> !o.isEmpty())
                .map(o -> o.get(0));
    }

    /**
     * Retrieves a collection of time series from a {@link Transferable}.
     * <p>
     * Note that the content of this {@link TsCollection} might be asynchronous.
     * Therefore, you should call
     * {@link TsCollection#load(TsInformationType, TsFactory)} after this method.
     *
     * @param transferable a non-null object
     * @return an optional {@link TsCollection}
     */
    @OnEDT
    @NonNull
    public Optional<TsCollection> toTsCollection(@NonNull Transferable transferable) {
        requireNonNull(transferable);
        return providers.stream()
                .filter(onDataFlavors(transferable.getTransferDataFlavors()))
                .map(o -> getTsCollectionOrNull(o, transferable, logger))
                .filter(Objects::nonNull)
                .findFirst();
    }

    @OnEDT
    @NonNull
    public Stream<TsCollection> toTsCollectionStream(@NonNull Transferable transferable) {
        return DataTransfers.getMultiTransferables(transferable)
                .map(this::toTsCollection)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    /**
     * Retrieves a TsData from a transferable.
     *
     * @param transferable a non-null object
     * @return an optional {@link TsData}
     */
    @OnEDT
    @NonNull
    public Optional<TsData> toTsData(@NonNull Transferable transferable) {
        return toTs(transferable)
                .map(ts -> ts.load(TsInformationType.Data, TsManager.getDefault()).getData());
    }

    /**
     * Retrieves a matrix from a transferable.
     *
     * @param transferable a non-null object
     * @return an optional {@link Matrix}
     */
    @OnEDT
    @NonNull
    public Optional<Matrix> toMatrix(@NonNull Transferable transferable) {
        return providers.stream()
                .filter(onDataFlavors(transferable.getTransferDataFlavors()))
                .map(o -> getMatrixOrNull(o, transferable, logger))
                .filter(Objects::nonNull)
                .findFirst();
    }

    /**
     * Retrieves a Table from a transferable.
     *
     * @param transferable a non-null object
     * @return an optional {@link Table}
     */
    @OnEDT
    @NonNull
    public Optional<Table<?>> toTable(@NonNull Transferable transferable) {
        Predicate<DataTransferSpi> predicate = onDataFlavors(transferable.getTransferDataFlavors());
        for (DataTransferSpi o : providers.get()) {
            if (predicate.test(o)) {
                Table<?> table = getTableOrNull(o, transferable, logger);
                if (table != null) {
                    return Optional.of(table);
                }
            }
        }
        return Optional.empty();
    }

    private final class ClipboardValidator implements FlavorListener {

        private boolean isValid(Clipboard clipboard) {
            try {
                return canImport(clipboard.getAvailableDataFlavors());
            } catch (IllegalStateException ex) {
                logger.log(Level.FINE, "While getting content from clipboard", ex);
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

    private static void logUnexpected(Logger logger, DataTransferSpi o, RuntimeException unexpected, String context) {
        logger.log(Level.INFO, "Unexpected exception while " + context + " using '" + getIdOrClassName(o) + "'", unexpected);
    }

    private static void logExpected(Logger logger, DataTransferSpi o, Exception expected, String context) {
        logger.log(Level.FINE, "While " + context + " using '" + getIdOrClassName(o) + "'", expected);
    }

    private static String getIdOrClassName(DataTransferSpi handler) {
        try {
            return requireNonNull(handler.getName());
        } catch (RuntimeException unexpected) {
            return handler.getClass().getName();
        }
    }

    private static DataFlavor getDataFlavorOrNull(DataTransferSpi handler) {
        try {
            return handler.getDataFlavor();
        } catch (RuntimeException unexpected) {
            return null;
        }
    }

    private static TsCollection getTsCollectionOrNull(DataTransferSpi o, Transferable t, Logger logger) {
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

    private static Matrix getMatrixOrNull(DataTransferSpi o, Transferable t, Logger logger) {
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

    private static Table<?> getTableOrNull(DataTransferSpi o, Transferable t, Logger logger) {
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
    private static <T> Transferable asTransferable(T data, Stream<? extends DataTransferSpi> allHandlers, TypeHelper<T> helper) {
        return new MultiTransferable<>(
                getHandlersByFlavor(data, allHandlers, helper),
                getTransferDataLoader(data, helper));
    }

    private static <T> Map<DataFlavor, List<DataTransferSpi>> getHandlersByFlavor(T data, Stream<? extends DataTransferSpi> allHandlers, TypeHelper<T> helper) {
        return allHandlers
                .filter(o -> helper.canTransferData(data, o))
                .collect(Collectors.groupingBy(DataTransfer::getDataFlavorOrNull));
    }

    private static <T> IOFunction<DataTransferSpi, Object> getTransferDataLoader(T data, TypeHelper<T> helper) {
        return o -> helper.getTransferData(data, o);
    }

    private interface TypeHelper<T> {

        boolean canTransferData(T data, DataTransferSpi handler);

        Object getTransferData(T data, DataTransferSpi handler) throws IOException;
    }

    private static final class TsCollectionHelper implements TypeHelper<TsCollection> {

        private static final TsCollectionHelper INSTANCE = new TsCollectionHelper();

        @Override
        public boolean canTransferData(TsCollection data, DataTransferSpi handler) {
            return handler.canExportTsCollection(data);
        }

        @Override
        public Object getTransferData(TsCollection data, DataTransferSpi handler) throws IOException {
            return handler.exportTsCollection(data);
        }
    }

    private static final class MatrixHelper implements TypeHelper<Matrix> {

        private static final MatrixHelper INSTANCE = new MatrixHelper();

        @Override
        public boolean canTransferData(Matrix data, DataTransferSpi handler) {
            return handler.canExportMatrix(data);
        }

        @Override
        public Object getTransferData(Matrix data, DataTransferSpi handler) throws IOException {
            return handler.exportMatrix(data);
        }
    }

    private static final class TableHelper implements TypeHelper<Table<?>> {

        private static final TableHelper INSTANCE = new TableHelper();

        @Override
        public boolean canTransferData(Table<?> data, DataTransferSpi handler) {
            return handler.canExportTable(data);
        }

        @Override
        public Object getTransferData(Table<?> data, DataTransferSpi handler) throws IOException {
            return handler.exportTable(data);
        }
    }

    private static Predicate<DataTransferSpi> onDataFlavors(DataFlavor[] dataFlavors) {
        return onDataFlavors(new HashSet<>(Arrays.asList(dataFlavors)));
    }

    private static Predicate<DataTransferSpi> onDataFlavors(Set<DataFlavor> dataFlavors) {
        return o -> dataFlavors.contains(o != null ? getDataFlavorOrNull(o) : null);
    }
}
