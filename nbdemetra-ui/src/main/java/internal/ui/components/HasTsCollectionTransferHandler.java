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

import demetra.ui.TsManager;
import demetra.ui.components.HasTsCollection;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsInformationType;
import ec.tss.datatransfer.DataTransfers;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tss.datatransfer.impl.LocalObjectTssTransferHandler;
import java.awt.datatransfer.Transferable;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import org.openide.util.Lookup;

/**
 *
 * @author Philippe Charles
 */
public final class HasTsCollectionTransferHandler extends TransferHandler {

    private final HasTsCollection delegate;
    private final TssTransferSupport tssSupport;

    public HasTsCollectionTransferHandler(HasTsCollection delegate, TssTransferSupport tssSupport) {
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
        TsCollection col = delegate.getTsSelectionStream().collect(TsManager.getDefault().getTsCollector());
        return tssSupport.fromTsCollection(col);
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

    public static boolean canImport(@Nonnull HasTsCollection view, @Nonnull TssTransferSupport tssSupport, @Nonnull Supplier<Transferable> toData) {
        if (!view.getTsUpdateMode().isReadOnly()) {
            Transferable t = toData.get();
            return tssSupport.canImport(t) && TransferChange.of(t, view.getTsCollection()).mayChangeContent();
        }
        return false;
    }

    public static boolean importData(@Nonnull HasTsCollection view, @Nonnull TssTransferSupport tssSupport, @Nonnull Supplier<Transferable> toData) {
        if (!view.getTsUpdateMode().isReadOnly()) {
            return tssSupport.toTsCollectionStream(toData.get())
                    .peek(o -> importData(view, o))
                    .count() > 0;
        }
        return false;
    }

    private static void importData(HasTsCollection view, TsCollection data) {
        if (view.isFreezeOnImport()) {
            data.load(TsInformationType.All);
            update(view.getTsUpdateMode(), view.getTsCollection(), freezedCopyOf(data));
        } else {
            if (TransferChange.isNotYetLoaded(data)) {
                // TODO: put load in a separate thread
                data.load(TsInformationType.Definition);
            }
            if (!data.isEmpty()) {
                data.query(TsInformationType.All);
                update(view.getTsUpdateMode(), view.getTsCollection(), data);
            }
        }
    }

    private static void update(HasTsCollection.TsUpdateMode mode, TsCollection main, TsCollection col) {
        switch (mode) {
            case None:
                break;
            case Single:
                main.replace(col.get(0));
                break;
            case Replace:
                main.replace(col);
                break;
            case Append:
                main.append(col);
                break;
        }
    }

    private static TsCollection freezedCopyOf(TsCollection input) {
        return input.stream().map(Ts::freeze).collect(TsManager.getDefault().getTsCollector());
    }

    private enum TransferChange {
        YES, NO, MAYBE;

        public boolean mayChangeContent() {
            return this != NO;
        }

        public static TransferChange of(Transferable source, TsCollection target) {
            LocalObjectTssTransferHandler handler = Lookup.getDefault().lookup(LocalObjectTssTransferHandler.class);
            return handler != null ? of(handler, source, target) : MAYBE;
        }

        private static TransferChange of(LocalObjectTssTransferHandler handler, Transferable source, TsCollection target) {
            return DataTransfers.getMultiTransferables(source)
                    .map(handler::peekTsCollection)
                    .map(o -> of(o, target))
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
