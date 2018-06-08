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
package internal.ui;

import com.google.common.collect.Sets;
import demetra.timeseries.TsData;
import demetra.tsprovider.Ts;
import demetra.tsprovider.TsCollection;
import demetra.tsprovider.TsInformationType;
import demetra.ui.TsManager;
import demetra.ui.beans.ListenableBean;
import ec.tss.datatransfer.impl.LocalObjectDataTransfer;
import ec.tss.tsproviders.utils.FunctionWithIO;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.design.VisibleForTesting;
import ec.tstoolkit.maths.matrices.Matrix;
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
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import demetra.ui.DataTransfer;
import ec.nbdemetra.ui.ns.INamedService;
import ec.tss.datatransfer.DataTransfers;
import demetra.ui.DataTransferSpi;

/**
 * @author Philippe Charles
 */
@ServiceProvider(service = DataTransfer.class)
public final class DefaultDataTransfer extends ListenableBean implements DataTransfer {

    private final ClipboardValidator clipboardValidator;
    private final Lookup lookup;
    private final Logger logger;
    private boolean validClipboard;

    public DefaultDataTransfer() {
        this(Lookup.getDefault(), LoggerFactory.getLogger(DefaultDataTransfer.class), false);
        clipboardValidator.register(Toolkit.getDefaultToolkit().getSystemClipboard());
    }

    @VisibleForTesting
    DefaultDataTransfer(Lookup lookup, Logger logger, boolean validClipboard) {
        this.clipboardValidator = new ClipboardValidator();
        this.lookup = lookup;
        this.logger = logger;
        this.validClipboard = validClipboard;
    }

    @Override
    public boolean isValidClipboard() {
        return validClipboard;
    }

    private void setValidClipboard(boolean validClipboard) {
        boolean old = this.validClipboard;
        this.validClipboard = validClipboard;
        firePropertyChange(VALID_CLIPBOARD_PROPERTY, old, this.validClipboard);
    }

    private Stream<? extends DataTransferSpi> lookupAll() {
        return lookup.lookupAll(DataTransferSpi.class).stream();
    }

    @Override
    public List<? extends INamedService> getProviders() {
        return lookupAll().collect(Collectors.toList());
    }

    @Override
    public boolean canImport(DataFlavor... dataFlavors) {
        // multiFlavor means "maybe", not "yes"
        return DataTransfers.isMultiFlavor(dataFlavors) || lookupAll().anyMatch(onDataFlavors(dataFlavors));
    }

    @Override
    public boolean canImport(Transferable transferable) {
        Set<DataFlavor> dataFlavors = DataTransfers.getMultiDataFlavors(transferable).collect(Collectors.toSet());
        return lookupAll().anyMatch(onDataFlavors(dataFlavors));
    }

    @Override
    public Transferable fromTsData(TsData data) {
        requireNonNull(data);
        return fromTs(Ts.builder().data(data).build());
    }

    @Override
    public Transferable fromTs(Ts ts) {
        requireNonNull(ts);
        return fromTsCollection(TsCollection.of(ts));
    }

    @Override
    public Transferable fromTsCollection(TsCollection col) {
        requireNonNull(col);
        return asTransferable(col, lookupAll(), TsCollectionHelper.INSTANCE);
    }

    @Override
    public Transferable fromMatrix(Matrix matrix) {
        requireNonNull(matrix);
        return asTransferable(matrix, lookupAll(), MatrixHelper.INSTANCE);
    }

    @Override
    public Transferable fromTable(Table<?> table) {
        requireNonNull(table);
        return asTransferable(table, lookupAll(), TableHelper.INSTANCE);
    }

    @Override
    public TsData toTsData(Transferable transferable) {
        Ts ts = toTs(transferable);
        return ts != null
                ? TsManager.getDefault().load(ts, TsInformationType.Data).getData()
                : null;
    }

    @Override
    public Ts toTs(Transferable transferable) {
        TsCollection col = toTsCollection(transferable);
        return col != null && !col.getData().isEmpty() ? col.getData().get(0) : null;
    }

    @Override
    public TsCollection toTsCollection(Transferable transferable) {
        requireNonNull(transferable);
        return lookupAll()
                .filter(onDataFlavors(transferable.getTransferDataFlavors()))
                .map(o -> toTsCollection(o, transferable, logger))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Stream<TsCollection> toTsCollectionStream(Transferable transferable) {
        return DataTransfers.getMultiTransferables(transferable)
                .map(this::toTsCollection)
                .filter(Objects::nonNull);
    }

    @Override
    public Matrix toMatrix(Transferable transferable) {
        requireNonNull(transferable);
        return lookupAll()
                .filter(onDataFlavors(transferable.getTransferDataFlavors()))
                .map(o -> toMatrix(o, transferable, logger))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Table<?> toTable(Transferable transferable) {
        requireNonNull(transferable);
        return lookupAll()
                .filter(onDataFlavors(transferable.getTransferDataFlavors()))
                .map(o -> toTable(o, transferable, logger))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean isTssTransferable(Transferable transferable) {
        return transferable.isDataFlavorSupported(LocalObjectDataTransfer.DATA_FLAVOR);
    }

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

    private static void logUnexpected(Logger logger, DataTransferSpi o, RuntimeException unexpected, String context) {
        logger.info("Unexpected exception while " + context + " using '" + getIdOrClassName(o) + "'", unexpected);
    }

    private static void logExpected(Logger logger, DataTransferSpi o, Exception expected, String context) {
        logger.debug("While " + context + " using '" + getIdOrClassName(o) + "'", expected);
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

    private static TsCollection toTsCollection(DataTransferSpi o, Transferable t, Logger logger) {
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

    private static Matrix toMatrix(DataTransferSpi o, Transferable t, Logger logger) {
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

    private static Table<?> toTable(DataTransferSpi o, Transferable t, Logger logger) {
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
                .collect(Collectors.groupingBy(DefaultDataTransfer::getDataFlavorOrNull));
    }

    private static <T> FunctionWithIO<DataTransferSpi, Object> getTransferDataLoader(T data, TypeHelper<T> helper) {
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
        return onDataFlavors(Sets.newHashSet(dataFlavors));
    }

    private static Predicate<DataTransferSpi> onDataFlavors(Set<DataFlavor> dataFlavors) {
        return o -> dataFlavors.contains((DataFlavor) (o != null ? getDataFlavorOrNull(o) : null));
    }
}
