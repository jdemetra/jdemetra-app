/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.processing.ui.modelling;

import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.TsViewToolkit;
import demetra.timeseries.Ts;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import java.util.Collections;

/**
 *
 * @author PALATEJ
 * @param <D>
 */
public abstract class InputFactory<D extends TsDocument<?, ?>> extends ProcDocumentItemFactory<D, Ts> {

    protected InputFactory(Class<D> documentType, Id id) {
        super(documentType, id,
                source -> source.getInput(),
                s -> TsViewToolkit.getGrid(s == null ? null : Collections.singleton(s)));
    }
}
