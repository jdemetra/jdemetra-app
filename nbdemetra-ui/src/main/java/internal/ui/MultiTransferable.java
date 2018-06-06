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

import ec.tss.tsproviders.utils.FunctionWithIO;
import ec.util.various.swing.OnAnyThread;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
final class MultiTransferable<HANDLER> implements Transferable {

    @lombok.NonNull
    private final Map<DataFlavor, List<HANDLER>> roHandlersByFlavor;

    @lombok.NonNull
    private final FunctionWithIO<HANDLER, Object> transferDataLoader;

    private final ConcurrentMap<DataFlavor, Object> cache = new ConcurrentHashMap<>();

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
