/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tss.datatransfer.impl;

import ec.tss.TsCollection;
import ec.tss.datatransfer.DataTransfers;
import ec.tss.datatransfer.TssTransferHandler;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.maths.matrices.Matrix;
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
@ServiceProvider(service = TssTransferHandler.class, position = 0)
public class LocalObjectTssTransferHandler extends TssTransferHandler {

    public static final DataFlavor DATA_FLAVOR = DataTransfers.newLocalObjectDataFlavor(LocalObjectTssTransferHandler.class);

    public LocalObjectTssTransferHandler() {
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

    @Override
    public boolean canExportMatrix(Matrix matrix) {
        return true;
    }

    @Override
    public Object exportMatrix(Matrix matrix) throws IOException {
        return matrix;
    }

    @Override
    public boolean canImportMatrix(Object obj) {
        return obj instanceof Matrix;
    }

    @Override
    public Matrix importMatrix(Object obj) throws IOException, ClassCastException {
        return (Matrix) obj;
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
