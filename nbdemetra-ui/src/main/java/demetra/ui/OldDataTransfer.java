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
package demetra.ui;

import demetra.ui.beans.PropertyChangeSource;
import ec.nbdemetra.core.GlobalService;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.util.various.swing.OnEDT;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openide.util.Lookup;

/**
 * A support class that deals with the clipboard. It allows the user to get/set
 * time series, collections, matrixes and tables from/to any transferable. The
 * actual conversion is done by TssTransferHandler.
 * <p>
 * Note that this class can be extended to modify its behavior.
 *
 * @author Philippe Charles
 */
@GlobalService
public interface OldDataTransfer extends PropertyChangeSource {

    /**
     * A convenient method to get the current single instance of DataTransfer.
     * You could use the default lookup to get the same result.
     *
     * @return a non-null DataTransfer
     */
    @Nonnull
    static OldDataTransfer getDefault() {
        return Lookup.getDefault().lookup(OldDataTransfer.class);
    }

    String VALID_CLIPBOARD_PROPERTY = "validClipboard";

    @OnEDT
    boolean canImport(@Nonnull DataFlavor... dataFlavors);

    @OnEDT
    boolean canImport(@Nonnull Transferable transferable);

    /**
     * Creates a Transferable from a Matrix.
     *
     * @param matrix a non-null {@link Matrix}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @Nonnull
    Transferable fromMatrix(@Nonnull Matrix matrix);

    /**
     * Creates a Transferable from a Table.
     *
     * @param table a non-null {@link Table}
     * @return a never-null {@link Transferable}
     */
    @OnEDT
    @Nonnull
    Transferable fromTable(@Nonnull Table<?> table);

    /**
     * Checks if the clipboard currently contains data that can be imported.
     *
     * @return true if the data in the clipboard is importable; false otherwise
     */
    @OnEDT
    boolean isValidClipboard();

    /**
     * Retrieves a list of all available TssTransferHandler.
     *
     * @return a non-null stream of INamedService
     */
    @Nonnull
    List<? extends NamedService> getProviders();

    /**
     * Retrieves a Matrix from a transferable.
     *
     * @param transferable a non-null object
     * @return a {@link Matrix} if possible, <code>null</code> otherwise
     */
    @OnEDT
    @Nullable
    Matrix toMatrix(@Nonnull Transferable transferable);

    /**
     * Retrieves a Table from a transferable.
     *
     * @param transferable a non-null object
     * @return a {@link Table} if possible, <code>null</code> otherwise
     */
    @OnEDT
    @Nullable
    Table<?> toTable(@Nonnull Transferable transferable);
}
