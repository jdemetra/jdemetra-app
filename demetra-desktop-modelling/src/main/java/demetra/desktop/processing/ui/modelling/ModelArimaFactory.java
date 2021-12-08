/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.processing.ui.modelling;

import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.stats.ArimaUI;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import jdplus.arima.IArimaModel;
import jdplus.regsarima.regular.RegSarimaModel;

/**
 *
 * @author PALATEJ
 * @param <D>
 */
public abstract class ModelArimaFactory<D extends TsDocument<?, ?>>
        extends ProcDocumentItemFactory<D, Map<String, IArimaModel>> {

    protected ModelArimaFactory(Class<D> documentType, Id id, Function<D, RegSarimaModel> extractor) {
        super(documentType, id, extractor.andThen(source -> {
            if (source == null) {
                return null;
            }
            IArimaModel model = source.arima();
            return Collections.singletonMap("Arima model", model);
        }), new ArimaUI());
    }
}
