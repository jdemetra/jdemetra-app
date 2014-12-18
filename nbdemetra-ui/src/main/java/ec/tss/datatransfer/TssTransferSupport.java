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

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import ec.nbdemetra.core.GlobalService;
import ec.nbdemetra.ui.awt.ListenableBean;
import ec.tss.*;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.timeseries.simplets.TsData;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    //
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
    //
    private boolean validClipboard;

    public TssTransferSupport() {
        this.validClipboard = false;
        Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(new FlavorListener() {
            @Override
            public void flavorsChanged(FlavorEvent e) {
                try {
                    boolean old = validClipboard;
                    validClipboard = canImport(((Clipboard) e.getSource()).getContents(this).getTransferDataFlavors());
                    firePropertyChange(VALID_CLIPBOARD_PROPERTY, old, validClipboard);
                } catch (Exception ex) {
                    LOGGER.debug("While getting content from clipboard", ex);
                }
            }
        });
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
     */
    @Nonnull
    public FluentIterable<? extends TssTransferHandler> all() {
        return FluentIterable.from(Iterables.concat(Lookup.getDefault().lookupAll(TssTransferHandler.class), ATsCollectionFormatter.getLegacyHandlers()));
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

    public boolean canImport(@Nonnull DataFlavor... dataFlavors) {
        return all().anyMatch(onDataFlavors(dataFlavors));
    }

    /**
     * Creates a Transferable from a TsData.
     *
     * @param data a non-null {@link TsData}
     * @return a never-null {@link Transferable}
     */
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
    @Nonnull
    public Transferable fromTsCollection(@Nonnull TsCollection col) {
        Preconditions.checkNotNull(col);
        return new TsCollectionAdapter(col, all());
    }

    /**
     * Creates a Transferable from a Matrix.
     *
     * @param matrix a non-null {@link Matrix}
     * @return a never-null {@link Transferable}
     */
    @Nonnull
    public Transferable fromMatrix(@Nonnull Matrix matrix) {
        Preconditions.checkNotNull(matrix);
        return new MatrixAdapter(matrix, all());
    }

    /**
     * Creates a Transferable from a Table.
     *
     * @param table a non-null {@link Table}
     * @return a never-null {@link Transferable}
     */
    @Nonnull
    public Transferable fromTable(@Nonnull Table<?> table) {
        Preconditions.checkNotNull(table);
        return new TableAdapter(table, all());
    }

    /**
     * Retrieves a TsData from a transferable.
     *
     * @param transferable a non-null object
     * @return a {@link TsData} if possible, <code>null</code> otherwise
     */
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
    @Nullable
    public TsCollection toTsCollection(@Nonnull Transferable transferable) {
        Preconditions.checkNotNull(transferable);
        for (TssTransferHandler o : all().filter(onDataFlavors(transferable.getTransferDataFlavors()))) {
            try {
                Object transferData = transferable.getTransferData(o.getDataFlavor());
                if (o.canImportTsCollection(transferData)) {
                    LOGGER.debug("Getting collection using '{}'", o.getName());
                    return o.importTsCollection(transferData);
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                LOGGER.error("While getting collection using '" + o.getName() + "'", ex);
            }
        }
        return null;
    }

    /**
     * Retrieves a Matrix from a transferable.
     *
     * @param transferable a non-null object
     * @return a {@link Matrix} if possible, <code>null</code> otherwise
     */
    @Nullable
    public Matrix toMatrix(@Nonnull Transferable transferable) {
        Preconditions.checkNotNull(transferable);
        for (TssTransferHandler o : all().filter(onDataFlavors(transferable.getTransferDataFlavors()))) {
            try {
                Object transferData = transferable.getTransferData(o.getDataFlavor());
                if (o.canImportMatrix(transferData)) {
                    LOGGER.debug("Getting matrix using '{}'", o.getName());
                    return o.importMatrix(transferData);
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                LOGGER.error("While getting matrix using '" + o.getName() + "'", ex);
            }
        }
        return null;
    }

    /**
     * Retrieves a Table from a transferable.
     *
     * @param transferable a non-null object
     * @return a {@link Table} if possible, <code>null</code> otherwise
     */
    @Nullable
    public Table<?> toTable(@Nonnull Transferable transferable) {
        Preconditions.checkNotNull(transferable);
        for (TssTransferHandler o : all().filter(onDataFlavors(transferable.getTransferDataFlavors()))) {
            try {
                Object transferData = transferable.getTransferData(o.getDataFlavor());
                if (o.canImportTable(transferData)) {
                    LOGGER.debug("Getting table using '{}'", o.getName());
                    return o.importTable(transferData);
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                LOGGER.error("While getting table using '" + o.getName() + "'", ex);
            }
        }
        return null;
    }

    //<editor-fold defaultstate="collapsed" desc="Adapters for Transferable">
    private static abstract class CustomAdapter<T> implements Transferable {

        private final T data;
        private final List<? extends TssTransferHandler> handlers;

        public CustomAdapter(T data, List<? extends TssTransferHandler> handlers) {
            this.data = data;
            this.handlers = handlers;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return FluentIterable.from(handlers).transform(TO_DATA_FLAVOR).toArray(DataFlavor.class);
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return FluentIterable.from(handlers).anyMatch(onDataFlavor(flavor));
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            Optional<? extends TssTransferHandler> handler = FluentIterable.from(handlers).firstMatch(onDataFlavor(flavor));
            if (handler.isPresent()) {
                LOGGER.debug("Getting transfer data using '{}'", handler.get().getName());
                return getTransferData(data, handler.get());
            }
            throw new UnsupportedFlavorException(flavor);
        }

        abstract protected Object getTransferData(T data, TssTransferHandler handler) throws IOException;
    }

    private static class TsCollectionAdapter extends CustomAdapter<TsCollection> {

        TsCollectionAdapter(TsCollection col, FluentIterable<? extends TssTransferHandler> allHandlers) {
            super(col, allHandlers.filter(onExportability(col)).toList());
        }

        @Override
        protected Object getTransferData(TsCollection data, TssTransferHandler handler) throws IOException {
            return handler.exportTsCollection(data);
        }
    }

    private static class MatrixAdapter extends CustomAdapter<Matrix> {

        MatrixAdapter(Matrix matrix, FluentIterable<? extends TssTransferHandler> allHandlers) {
            super(matrix, allHandlers.filter(onExportability(matrix)).toList());
        }

        @Override
        protected Object getTransferData(Matrix data, TssTransferHandler handler) throws IOException {
            return handler.exportMatrix(data);
        }
    }

    private static class TableAdapter extends CustomAdapter<Table<?>> {

        TableAdapter(Table<?> table, FluentIterable<? extends TssTransferHandler> allHandlers) {
            super(table, allHandlers.filter(onExportability(table)).toList());
        }

        @Override
        protected Object getTransferData(Table<?> data, TssTransferHandler handler) throws IOException {
            return handler.exportTable(data);
        }
    }
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="Functions and Predicates">
    private static final Function<TssTransferHandler, DataFlavor> TO_DATA_FLAVOR = new Function<TssTransferHandler, DataFlavor>() {
        @Override
        public DataFlavor apply(TssTransferHandler input) {
            return input != null ? input.getDataFlavor() : null;
        }
    };

    private static Predicate<TssTransferHandler> onExportability(final TsCollection col) {
        return new Predicate<TssTransferHandler>() {
            @Override
            public boolean apply(TssTransferHandler input) {
                return input != null ? input.canExportTsCollection(col) : null;
            }
        };
    }

    private static Predicate<TssTransferHandler> onExportability(final Matrix matrix) {
        return new Predicate<TssTransferHandler>() {
            @Override
            public boolean apply(TssTransferHandler input) {
                return input != null ? input.canExportMatrix(matrix) : null;
            }
        };
    }

    private static Predicate<TssTransferHandler> onExportability(final Table<?> table) {
        return new Predicate<TssTransferHandler>() {
            @Override
            public boolean apply(TssTransferHandler input) {
                return input != null ? input.canExportTable(table) : null;
            }
        };
    }

    private static Predicate<TssTransferHandler> onDataFlavor(DataFlavor dataFlavor) {
        return Predicates.compose(Predicates.equalTo(dataFlavor), TO_DATA_FLAVOR);
    }

    private static Predicate<TssTransferHandler> onDataFlavors(DataFlavor[] dataFlavors) {
        final List<DataFlavor> list = Arrays.asList(dataFlavors);
        return new Predicate<TssTransferHandler>() {
            @Override
            public boolean apply(TssTransferHandler input) {
                return list.contains(input != null ? input.getDataFlavor() : null);
            }
        };
    }
    //</editor-fold>
}
