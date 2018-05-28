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
import demetra.ui.components.HasTsCollection;
import ec.tss.TsCollection;
import ec.tss.datatransfer.TssTransferSupport;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.TooManyListenersException;
import org.openide.util.Exceptions;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class HasTsCollectionDropTargetListener implements DropTargetListener {

    @lombok.NonNull
    private final HasTsCollection target;

    @lombok.NonNull
    private final TssTransferSupport transferSupport;

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        if (!target.getTsUpdateMode().isReadOnly()) {
            Transferable t = dtde.getTransferable();
            if (transferSupport.canImport(t)) {
                demetra.tsprovider.TsCollection.Builder dropContent = demetra.tsprovider.TsCollection.builder();
                transferSupport
                        .toTsCollectionStream(t)
                        .flatMap(TsCollection::stream)
                        .map(TsConverter::toTs)
                        .forEach(dropContent::data);
                target.setDropContent(dropContent.build());
            }
        }
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        target.setDropContent(demetra.tsprovider.TsCollection.EMPTY);
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        dragExit(dtde);
    }

    public void register(DropTarget dropTarget) {
        try {
            dropTarget.addDropTargetListener(this);
        } catch (TooManyListenersException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
