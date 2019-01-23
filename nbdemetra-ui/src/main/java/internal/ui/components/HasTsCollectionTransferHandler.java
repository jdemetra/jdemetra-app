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

import demetra.bridge.TsConverter;
import demetra.ui.TsManager;
import demetra.ui.components.HasTsCollection;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsInformationType;
import java.awt.datatransfer.Transferable;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import org.openide.util.Lookup;
import demetra.ui.datatransfer.DataTransfer;
import demetra.ui.datatransfer.DataTransfers;
import demetra.ui.datatransfer.LocalObjectDataTransfer;

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
        demetra.tsprovider.TsCollection.Builder col = demetra.tsprovider.TsCollection.builder();
        delegate.getTsSelectionStream().forEach(col::data);
        return tssSupport.fromTsCollection(col.build());
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

    public static boolean canImport(@Nonnull HasTsCollection view, @Nonnull DataTransfer tssSupport, @Nonnull Supplier<Transferable> toData) {
        if (!view.getTsUpdateMode().isReadOnly()) {
            Transferable t = toData.get();
            return tssSupport.canImport(t) && TransferChange.of(t, view.getTsCollection()).mayChangeContent();
        }
        return false;
    }

    public static boolean importData(@Nonnull HasTsCollection view, @Nonnull DataTransfer tssSupport, @Nonnull Supplier<Transferable> toData) {
        if (!view.getTsUpdateMode().isReadOnly()) {
            return tssSupport.toTsCollectionStream(toData.get())
                    .map(TsConverter::fromTsCollection)
                    .peek(o -> importData(view, o))
                    .count() > 0;
        }
        return false;
    }

    private static void importData(HasTsCollection view, TsCollection data) {
        if (view.isFreezeOnImport()) {
            data.load(TsInformationType.All);
            view.setTsCollection(update(view.getTsUpdateMode(), view.getTsCollection(), freezedCopyOf(data)));
        } else {
            if (TransferChange.isNotYetLoaded(data)) {
                // TODO: put load in a separate thread
                data.load(TsInformationType.Definition);
            }
            if (!data.isEmpty()) {
                data.query(TsInformationType.All);
                view.setTsCollection(update(view.getTsUpdateMode(), view.getTsCollection(), data));
            }
        }
    }

    private static demetra.tsprovider.TsCollection update(HasTsCollection.TsUpdateMode mode, demetra.tsprovider.TsCollection main, TsCollection col) {
        switch (mode) {
            case None:
                return main;
            case Single:
                return demetra.tsprovider.TsCollection.of(TsConverter.toTs(col.get(0)));
            case Replace:
                return TsConverter.toTsCollection(col);
            case Append:
                Set<demetra.tsprovider.TsMoniker> monikers = main.getData().stream().map(demetra.tsprovider.Ts::getMoniker).collect(Collectors.toSet());
                demetra.tsprovider.TsCollection.Builder result = main.toBuilder();
                for (Ts o : col) {
                    demetra.tsprovider.TsMoniker id = TsConverter.toTsMoniker(o.getMoniker());
                    if (!id.isProvided() || !monikers.contains(id)) {
                        result.data(TsConverter.toTs(o));
                    }
                }
                return result.build();
        }
        return main;
    }

    private static TsCollection freezedCopyOf(TsCollection input) {
        return input.stream().map(Ts::freeze).collect(TsManager.getDefault().getTsCollector());
    }

    private enum TransferChange {
        YES, NO, MAYBE;

        public boolean mayChangeContent() {
            return this != NO;
        }

        public static TransferChange of(Transferable source, demetra.tsprovider.TsCollection target) {
            LocalObjectDataTransfer handler = Lookup.getDefault().lookup(LocalObjectDataTransfer.class);
            return handler != null ? of(handler, source, target) : MAYBE;
        }

        private static TransferChange of(LocalObjectDataTransfer handler, Transferable source, demetra.tsprovider.TsCollection target) {
            return DataTransfers.getMultiTransferables(source)
                    .map(handler::peekTsCollection)
                    .map(o -> of(TsConverter.fromTsCollection(o), TsConverter.fromTsCollection(target)))
                    .filter(TransferChange::mayChangeContent)
                    .findFirst()
                    .orElse(NO);
        }

        private static TransferChange of(@Nullable TsCollection source, @Nonnull TsCollection target) {
            if (source == null) {
                return MAYBE;
            }
            if (isNotYetLoaded(source)) {
                return MAYBE;
            }
            if (!source.stream().allMatch(target::contains)) {
                return YES;
            }
            return NO;
        }

        public static boolean isNotYetLoaded(TsCollection o) {
            return !o.getMoniker().isAnonymous() && o.isEmpty();
        }
    }
}
