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
package internal.ui.components;

import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsInformationType;
import demetra.timeseries.TsMoniker;
import demetra.ui.TsManager;
import demetra.ui.components.parts.HasTsCollection;
import java.awt.datatransfer.Transferable;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import org.openide.util.Lookup;
import demetra.ui.datatransfer.DataTransfer;
import demetra.ui.datatransfer.DataTransfers;
import demetra.ui.datatransfer.LocalObjectDataTransfer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author Philippe Charles
 */
public final class HasTsCollectionTransferHandler extends TransferHandler {

    private final HasTsCollection delegate;
    private final DataTransfer tssSupport;

    public HasTsCollectionTransferHandler(HasTsCollection delegate, DataTransfer tssSupport) {
        this.delegate = delegate;
        this.tssSupport = tssSupport;
    }

    @Override
    public int getSourceActions(JComponent c) {
        //            TsDragRenderer r = selection.length < 10 ? TsDragRenderer.asChart() : TsDragRenderer.asCount();
        //            Image image = r.getTsDragRendererImage(Arrays.asList(selection));
        //            setDragImage(image);
        return COPY;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        TsCollection data = delegate.getTsSelectionStream().collect(TsCollection.toTsCollection());
        return tssSupport.fromTsCollection(data);
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (canImport(delegate, tssSupport, support::getTransferable)) {
            if (support.isDrop()) {
                support.setDropAction(COPY);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        return importData(delegate, tssSupport, support::getTransferable);
    }

    public static boolean canImport(@NonNull HasTsCollection view, @NonNull DataTransfer tssSupport, @NonNull Supplier<Transferable> toData) {
        if (!view.getTsUpdateMode().isReadOnly()) {
            Transferable t = toData.get();
            return tssSupport.canImport(t) && TransferChange.of(t, view.getTsCollection()).mayChangeContent();
        }
        return false;
    }

    public static boolean importData(@NonNull HasTsCollection view, @NonNull DataTransfer tssSupport, @NonNull Supplier<Transferable> toData) {
        if (!view.getTsUpdateMode().isReadOnly()) {
            return tssSupport.toTsCollectionStream(toData.get())
                    .peek(col -> importData(view, col))
                    .count() > 0;
        }
        return false;
    }

    private static void importData(HasTsCollection view, TsCollection data) {
        if (view.isFreezeOnImport()) {
            TsCollection latest = TsManager.getDefault().getNextTsManager().getTsCollection(data.getMoniker(), TsInformationType.All);
            view.setTsCollection(update(view.getTsUpdateMode(), view.getTsCollection(), freezedCopyOf(latest)));
        } else {
            if (TransferChange.isNotYetLoaded(data)) {
                // TODO: put load in a separate thread
                TsManager.getDefault().getNextTsManager().loadTsCollection(data, TsInformationType.Definition);
            }
            if (!data.isEmpty()) {
                TsManager.getDefault().loadAsync(data, TsInformationType.All);
                view.setTsCollection(update(view.getTsUpdateMode(), view.getTsCollection(), data));
            }
        }
    }

    private static TsCollection update(HasTsCollection.TsUpdateMode mode, TsCollection first, TsCollection second) {
        switch (mode) {
            case None:
                return first;
            case Single:
                return TsCollection.of(second.get(0));
            case Replace:
                return second;
            case Append:
                Set<TsMoniker> firstMonikers = first.stream().map(Ts::getMoniker).collect(Collectors.toSet());
                Predicate<TsMoniker> filter = moniker -> !moniker.isProvided() || !firstMonikers.contains(moniker);
                return Stream.concat(first.stream(), second.stream().filter(internal.ui.Collections2.compose(filter, Ts::getMoniker))).collect(TsCollection.toTsCollection());
        }
        return first;
    }

    private static TsCollection freezedCopyOf(TsCollection input) {
        return input.stream().map(Ts::freeze).collect(TsCollection.toTsCollection());
    }

    private enum TransferChange {
        YES, NO, MAYBE;

        public boolean mayChangeContent() {
            return this != NO;
        }

        public static TransferChange of(Transferable source, TsCollection target) {
            LocalObjectDataTransfer handler = Lookup.getDefault().lookup(LocalObjectDataTransfer.class);
            return handler != null ? of(handler, source, target) : MAYBE;
        }

        private static TransferChange of(LocalObjectDataTransfer handler, Transferable source, TsCollection target) {
            return DataTransfers.getMultiTransferables(source)
                    .map(handler::peekTsCollection)
                    .map(col -> of(col, target))
                    .filter(TransferChange::mayChangeContent)
                    .findFirst()
                    .orElse(NO);
        }

        private static TransferChange of(@Nullable TsCollection source, @NonNull TsCollection target) {
            if (source == null) {
                return MAYBE;
            }
            if (isNotYetLoaded(source)) {
                return MAYBE;
            }
            if (!source.stream().allMatch(target.getItems()::contains)) {
                return YES;
            }
            return NO;
        }

        public static boolean isNotYetLoaded(TsCollection o) {
            return o.getMoniker().isProvided() && o.isEmpty();
        }
    }
}
