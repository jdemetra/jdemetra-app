/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.modelling;

import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.SurfacePlotterUI;
import demetra.desktop.ui.processing.SurfacePlotterUI.Functions;
import demetra.processing.ProcSpecification;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import java.util.function.Function;
import jdplus.math.functions.IFunction;
import jdplus.regsarima.regular.RegSarimaModel;

/**
 *
 * @author PALATEJ
 * @param <D>
 */
public abstract class LikelihoodFactory<D extends TsDocument<?, RegSarimaModel>>
        extends ProcDocumentItemFactory<D, Functions> {

    private static final Function<TsDocument<? extends ProcSpecification, RegSarimaModel>, Functions> LLEXTRACTOR = source -> {
        RegSarimaModel model = source.getResult();

        if (model == null) {
            return null;
        } else {
            IFunction fn = model.likelihoodFunction();
            return Functions.create(fn, fn.evaluate(model.getEstimation().getParameters().getValues()));
        }
    };

    protected LikelihoodFactory(Class<D> documentType, Id id) {
        super(documentType, id, LLEXTRACTOR, new SurfacePlotterUI());
    }
}
