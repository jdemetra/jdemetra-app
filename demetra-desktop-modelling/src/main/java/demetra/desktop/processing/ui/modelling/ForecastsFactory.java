/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.processing.ui.modelling;

import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.stats.EstimationUI;
import demetra.timeseries.TimeSelector;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import java.util.function.Function;
import jdplus.regsarima.regular.RegSarimaModel;

/**
 *
 * @author PALATEJ
 * @param <D>
 */
public abstract class ForecastsFactory<D extends TsDocument<?, ?>>
        extends ProcDocumentItemFactory<D, EstimationUI.Information> {

    protected ForecastsFactory(Class<D> documentType, Id id, Function<D, RegSarimaModel> extractor) {
        super(documentType, id, (D source) -> {
            RegSarimaModel model = extractor.apply(source);
            if (model == null) {
                return null;
            }
            TsData orig = model.getDescription().getSeries();
            TimeSelector sel = TimeSelector.last(3 * orig.getAnnualFrequency());
            RegSarimaModel.Forecasts fcasts = model.forecasts(-2);
            TsData f = fcasts.getForecasts(), ef = fcasts.getForecastsStdev();
            if (f == null) {
                return null;
            } else {
                return new EstimationUI.Information(orig.select(sel), f, ef, 1.96);
            }
        },
                 new EstimationUI());
    }
}
