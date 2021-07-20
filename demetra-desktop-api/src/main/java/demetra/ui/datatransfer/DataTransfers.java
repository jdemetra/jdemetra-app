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
package demetra.ui.datatransfer;

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
import java.util.logging.Level;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import org.checkerframework.checker.nullness.qual.NonNull;
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

    public static <T> Optional<T> tryParse(Transferable t, Parser<T> parser) {
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

    public static <T> Optional<Transferable> tryFormat(T value, Formatter<T> formatter) {
        String text = formatter.formatAsString(value);
        return text != null
                ? Optional.<Transferable>of(new StringSelection(text))
                : Optional.<Transferable>empty();
    }

    public static boolean isMultiFlavor(@NonNull DataFlavor[] dataFlavors) {
        return dataFlavors.length == 1 && dataFlavors[0] == ExTransferable.multiFlavor;
    }

    @NonNull
    public static Stream<Transferable> getMultiTransferables(@NonNull Transferable t) {
        return getMultiTransferObject(t)
                .map(DataTransfers::asTransferableStream)
                .orElse(Stream.of(t));

    }

    @NonNull
    public static Stream<DataFlavor> getMultiDataFlavors(@NonNull Transferable t) {
        return getMultiTransferables(t).flatMap(o -> Stream.of(o.getTransferDataFlavors()));
    }

    @NonNull
    public static Optional<MultiTransferObject> getMultiTransferObject(@NonNull Transferable t) {
        if (isMultiFlavor(t.getTransferDataFlavors())) {
            try {
                return Optional.of((MultiTransferObject) t.getTransferData(ExTransferable.multiFlavor));
            } catch (UnsupportedFlavorException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return Optional.empty();
    }

    @NonNull
    public static Stream<Transferable> asTransferableStream(@NonNull MultiTransferObject multi) {
        return IntStream.range(0, multi.getCount()).mapToObj(multi::getTransferableAt);
    }

    @NonNull
    public static Transferable systemClipboardAsTransferable() {
        return new ClipboardAsTransferable(Toolkit.getDefaultToolkit().getSystemClipboard());
    }

    /**
     * Provides a way to avoid use of method
     * {@link Clipboard#getContents(java.lang.Object)} that might throw
     * OutOfMemoryError.
     */
    @lombok.extern.java.Log
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
                log.log(Level.WARNING, "While getting data flavors from clipboard", ex);
                return new DataFlavor[0];
            }
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            try {
                return clipboard.isDataFlavorAvailable(flavor);
            } catch (IllegalStateException ex) {
                log.log(Level.WARNING, "While checking data flavor from clipboard", ex);
                return false;
            }
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            try {
                return clipboard.getData(flavor);
            } catch (IllegalStateException | OutOfMemoryError ex) {
                log.log(Level.WARNING, "While getting data from clipboard", ex);
                return new IOException(ex);
            }
        }
    }
}
