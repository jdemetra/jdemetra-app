/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tss.datatransfer;

import ec.tss.tsproviders.utils.IFormatter;
import ec.tss.tsproviders.utils.IParser;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
            throw new RuntimeException(ex);
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
                throw new RuntimeException(ex);
            }
        }
        return Optional.empty();
    }

    public static <T> Optional<T> tryParse(Transferable t, IParser<T> parser) {
        if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String text = (String) t.getTransferData(DataFlavor.stringFlavor);
                return text != null ? Optional.ofNullable(parser.parse(text)) : Optional.empty();
            } catch (UnsupportedFlavorException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static <T> Optional<Transferable> tryFormat(T value, IFormatter<T> formatter) {
        String text = formatter.formatAsString(value);
        return text != null
                ? Optional.<Transferable>of(new StringSelection(text))
                : Optional.<Transferable>empty();
    }

    public static boolean isMultiFlavor(@Nonnull DataFlavor[] dataFlavors) {
        return dataFlavors.length == 1 && dataFlavors[0] == ExTransferable.multiFlavor;
    }

    @Nonnull
    public static Stream<Transferable> getMultiTransferables(@Nonnull Transferable t) {
        return getMultiTransferObject(t)
                .map(DataTransfers::asTransferableStream)
                .orElse(Stream.of(t));

    }

    @Nonnull
    public static Stream<DataFlavor> getMultiDataFlavors(@Nonnull Transferable t) {
        return getMultiTransferables(t).flatMap(o -> Stream.of(o.getTransferDataFlavors()));
    }

    @Nonnull
    public static Optional<MultiTransferObject> getMultiTransferObject(@Nonnull Transferable t) {
        if (isMultiFlavor(t.getTransferDataFlavors())) {
            try {
                return Optional.of((MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor));
            } catch (UnsupportedFlavorException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return Optional.empty();
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
}
