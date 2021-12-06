/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.modelling;

import static demetra.desktop.modelling.PreprocessingViews.MODEL_ARIMA;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.stats.ArimaUI;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import java.util.Collections;
import java.util.Map;
import jdplus.arima.IArimaModel;
import jdplus.regsarima.regular.RegSarimaModel;

/**
 *
 * @author PALATEJ
 * @param <D>
 */
public abstract class ModelArimaFactory<D extends TsDocument<?, RegSarimaModel>>
        extends ProcDocumentItemFactory<D, Map<String, IArimaModel>> {

    protected ModelArimaFactory(Class<D> documentType, Id id) {
        super(documentType, MODEL_ARIMA, source -> {
            RegSarimaModel pm = source.getResult();
            if (pm == null) {
                return null;
            }
            IArimaModel model = pm.arima();
            return Collections.singletonMap("Arima model", model);
        }, new ArimaUI());
    }
}
