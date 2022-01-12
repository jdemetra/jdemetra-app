/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.processing.ui.sa;

import demetra.data.DoubleSeq;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.modelling.ComponentInformation;
import demetra.sa.ComponentType;
import demetra.sa.DecompositionMode;
import demetra.sa.SeriesDecomposition;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import java.util.Map;
import java.util.function.Function;
import jdplus.arima.IArimaModel;

/**
 *
 * @author PALATEJ
 * @param <D>
 */
public abstract class SIFactory<D extends TsDocument<?, ?>>
        extends ProcDocumentItemFactory<D, TsData[]> {

    protected SIFactory(Class<D> documentType, Id id, Function<D, SeriesDecomposition> extractor) {
        super(documentType, id, extractor.andThen((SeriesDecomposition source) -> {
            if (source == null)
                return null;
            TsData seas = source.getSeries(ComponentType.Seasonal, ComponentInformation.Value);
            TsData i = source.getSeries(ComponentType.Irregular, ComponentInformation.Value);
            if (seas == null && i == null) {
                return null;
            }
            TsData si;
            DecompositionMode mode = source.getMode();
            if (mode.isMultiplicative()) {
                si = TsData.multiply(seas, i);
                if (seas == null) {
                    seas = TsData.of(i.getStart(), DoubleSeq.onMapping(i.length(), k -> 1));
                }
            } else {
                si = TsData.add(seas, i);
                if (seas == null) {
                    seas = TsData.of(i.getStart(), DoubleSeq.onMapping(i.length(), k -> 0));
                }
            }
            return new TsData[]{seas, si};
        }),
                 new SiRatioUI());
    }
}
