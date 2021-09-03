/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.datatransfer;

import demetra.math.matrices.MatrixType;
import demetra.timeseries.TsCollection;
import demetra.util.Table;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

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

    @Override
    public boolean canExportMatrix(MatrixType matrix) {
        return true;
    }

    @Override
    public Object exportMatrix(MatrixType matrix) throws IOException {
        return matrix;
    }

    @Override
    public boolean canImportMatrix(Object obj) {
        return obj instanceof MatrixType;
    }

    @Override
    public MatrixType importMatrix(Object obj) throws IOException, ClassCastException {
        return (MatrixType) obj;
    }

    @Override
    public boolean canExportTable(Table<?> table) {
        return true;
    }

    @Override
    public Object exportTable(Table<?> table) throws IOException {
        return table;
    }

    @Override
    public boolean canImportTable(Object obj) {
        return obj instanceof Table;
    }

    @Override
    public Table<?> importTable(Object obj) throws IOException, ClassCastException {
        return (Table<?>) obj;
    }
}
