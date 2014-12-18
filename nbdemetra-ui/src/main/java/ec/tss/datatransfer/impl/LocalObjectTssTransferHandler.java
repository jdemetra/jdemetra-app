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
import java.io.IOException;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = TssTransferHandler.class, position = 0)
public class LocalObjectTssTransferHandler extends TssTransferHandler {

    private static final DataFlavor LOCAL_TSCOL = DataTransfers.newLocalObjectDataFlavor(TsCollection.class);

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
        return LOCAL_TSCOL;
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
}
