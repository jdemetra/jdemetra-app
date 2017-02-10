/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tss.datatransfer;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import ec.tss.tsproviders.utils.FunctionWithIO;
import ec.tss.tsproviders.utils.IFormatter;
import ec.tss.tsproviders.utils.IParser;
import ec.util.various.swing.OnAnyThread;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.MultiTransferObject;

/**
 *
 * @author Philippe Charles
 */
public final class DataTransfers {

    private DataTransfers() {
        // static class
    }

    public static DataFlavor newLocalObjectDataFlavor(Class<?> clazz) {
        try {
            return new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + clazz.getName());
        } catch (ClassNotFoundException ex) {
            throw Throwables.propagate(ex);
        }
    }

    public static Optional<File> getSingleFile(Transferable t) {
        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                if (files.size() == 1) {
                    return Optional.of(files.get(0));
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                throw Throwables.propagate(ex);
            }
        }
        return Optional.absent();
    }

    public static <T> Optional<T> tryParse(Transferable t, IParser<T> parser) {
        if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String text = (String) t.getTransferData(DataFlavor.stringFlavor);
                return text != null ? Optional.fromNullable(parser.parse(text)) : Optional.absent();
            } catch (UnsupportedFlavorException ex) {
                throw Throwables.propagate(ex);
            } catch (IOException ex) {
                return Optional.absent();
            }
        }
        return Optional.absent();
    }

    public static <T> Optional<Transferable> tryFormat(T value, IFormatter<T> formatter) {
        String text = formatter.formatAsString(value);
        return text != null
                ? Optional.<Transferable>of(new StringSelection(text))
                : Optional.<Transferable>absent();
    }

    public static boolean isMultiFlavor(@Nonnull DataFlavor[] dataFlavors) {
        return dataFlavors.length == 1 && dataFlavors[0] == ExTransferable.multiFlavor;
    }

    @Nonnull
    public static java.util.Optional<MultiTransferObject> getMultiTransferObject(@Nonnull Transferable t) {
        if (isMultiFlavor(t.getTransferDataFlavors())) {
            try {
                return java.util.Optional.of((MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor));
            } catch (UnsupportedFlavorException | IOException ex) {
                throw Throwables.propagate(ex);
            }
        }
        return java.util.Optional.empty();
    }

    @Nonnull
    public static Stream<Transferable> asTransferableStream(@Nonnull MultiTransferObject multi) {
        return IntStream.range(0, multi.getCount()).mapToObj(multi::getTransferableAt);
    }

    @Nonnull
    public static Transferable systemClipboardAsTransferable() {
        return new ClipboardAsTransferable(Toolkit.getDefaultToolkit().getSystemClipboard());
    }

    /**
     * Provides a way to avoid use of method
     * {@link Clipboard#getContents(java.lang.Object)} that might throw
     * OutOfMemoryError.
     */
    @lombok.extern.slf4j.Slf4j
    private static final class ClipboardAsTransferable implements Transferable {

        private final Clipboard clipboard;

        private ClipboardAsTransferable(Clipboard clipboard) {
            this.clipboard = clipboard;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            try {
                return clipboard.getAvailableDataFlavors();
            } catch (IllegalStateException ex) {
                log.warn("While getting data flavors from clipboard", ex);
                return new DataFlavor[0];
            }
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            try {
                return clipboard.isDataFlavorAvailable(flavor);
            } catch (IllegalStateException ex) {
                log.warn("While checking data flavor from clipboard", ex);
                return false;
            }
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            try {
                return clipboard.getData(flavor);
            } catch (IllegalStateException | OutOfMemoryError ex) {
                log.warn("While getting data from clipboard", ex);
                return new IOException(ex);
            }
        }
    }

    static final class CustomAdapter<HANDLER> implements Transferable {

        private final Map<DataFlavor, List<HANDLER>> roHandlersByFlavor;
        private final FunctionWithIO<HANDLER, Object> transferDataLoader;
        private final ConcurrentMap<DataFlavor, Object> cache;

        CustomAdapter(Map<DataFlavor, List<HANDLER>> roHandlersByFlavor, FunctionWithIO<HANDLER, Object> transferDataLoader) {
            this.roHandlersByFlavor = Objects.requireNonNull(roHandlersByFlavor);
            this.transferDataLoader = Objects.requireNonNull(transferDataLoader);
            this.cache = new ConcurrentHashMap<>();
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return roHandlersByFlavor.keySet().toArray(new DataFlavor[roHandlersByFlavor.size()]);
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return roHandlersByFlavor.containsKey(flavor);
        }

        @OnAnyThread
        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            // drag&drop on EDT but copy-paste (clipboard) on another thread !
            try {
                return cache.computeIfAbsent(flavor, this::loadTransferDataUnchecked);
            } catch (UncheckedIOException ex) {
                throw ex.getCause();
            } catch (UncheckedUnsupportedFlavorException ex) {
                throw ex.getCause();
            }
        }

        @OnAnyThread
        private Object loadTransferDataUnchecked(DataFlavor flavor) {
            try {
                return loadTransferData(flavor);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            } catch (UnsupportedFlavorException ex) {
                throw new UncheckedUnsupportedFlavorException(ex);
            }
        }

        @OnAnyThread
        private Object loadTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            List<HANDLER> list = roHandlersByFlavor.get(flavor);
            if (list == null || list.isEmpty()) {
                throw new UnsupportedFlavorException(flavor);
            }
            Iterator<HANDLER> handlers = list.iterator();
            try {
                return transferDataLoader.apply(handlers.next());
            } catch (IOException ex) {
                return loadNext(handlers, ex);
            }
        }

        @OnAnyThread
        private Object loadNext(Iterator<HANDLER> handlers, IOException root) throws IOException {
            if (!handlers.hasNext()) {
                throw root;
            }
            try {
                return transferDataLoader.apply(handlers.next());
            } catch (IOException ex) {
                root.addSuppressed(ex);
                return loadNext(handlers, root);
            }
        }
    }

    private static final class UncheckedUnsupportedFlavorException extends RuntimeException {

        private UncheckedUnsupportedFlavorException(UnsupportedFlavorException cause) {
            super(cause);
        }

        @Override
        public UnsupportedFlavorException getCause() {
            return (UnsupportedFlavorException) super.getCause();
        }
    }
}
