/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.ui.datatransfer;

import demetra.timeseries.TsCollection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DataTransferSpi.class, position = 0)
public final class LocalObjectDataTransfer implements DataTransferSpi {

    public static final DataFlavor DATA_FLAVOR = DataTransfers.newLocalObjectDataFlavor(LocalObjectDataTransfer.class);

    public LocalObjectDataTransfer() {
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public String getName() {
        return "LocalObject";
    }

    @Override
    public String getDisplayName() {
        return "Local Object";
    }

    @Override
    public DataFlavor getDataFlavor() {
        return DATA_FLAVOR;
    }

    @Override
    public boolean canExportTsCollection(TsCollection col) {
        return true;
    }

    @Override
    public Object exportTsCollection(TsCollection col) {
        return col;
    }

    @Override
    public boolean canImportTsCollection(Object obj) {
        return obj instanceof TsCollection;
    }

    @Override
    public TsCollection importTsCollection(Object obj) throws IOException {
        return (TsCollection) obj;
    }

    @Nullable
    public TsCollection peekTsCollection(@NonNull Transferable t) {
        if (t.isDataFlavorSupported(DATA_FLAVOR)) {
            try {
                Object data = t.getTransferData(DATA_FLAVOR);
                if (canImportTsCollection(data)) {
                    return importTsCollection(data);
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }
}
