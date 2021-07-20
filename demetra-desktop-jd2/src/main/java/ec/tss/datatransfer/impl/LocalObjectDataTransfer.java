/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tss.datatransfer.impl;

import ec.tstoolkit.data.Table;
import ec.tstoolkit.maths.matrices.Matrix;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import org.openide.util.lookup.ServiceProvider;
import demetra.ui.datatransfer.DataTransfers;
import demetra.ui.OldDataTransferSpi;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = OldDataTransferSpi.class, position = 0)
public final class LocalObjectDataTransfer implements OldDataTransferSpi {

    public static final DataFlavor DATA_FLAVOR = DataTransfers.newLocalObjectDataFlavor(LocalObjectDataTransfer.class);

    public LocalObjectDataTransfer() {
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
}
