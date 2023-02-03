/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq;

import demetra.highfreq.DecompositionSpec;
import demetra.highfreq.ExtendedAirlineDecompositionSpec;
import demetra.highfreq.ExtendedAirlineModellingSpec;
import demetra.highfreq.ExtendedAirlineSpec;
import demetra.processing.DefaultProcessingLog;
import demetra.timeseries.AbstractTsDocument;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDomain;
import demetra.timeseries.TsUnit;
import demetra.timeseries.regression.ModellingContext;
import java.time.temporal.ChronoUnit;
import jdplus.highfreq.extendedairline.decomposiiton.ExtendedAirlineDecompositionKernel;
import jdplus.highfreq.extendedairline.ExtendedAirlineResults;

/**
 *
 * @author PALATEJ
 */
public class FractionalAirlineDecompositionDocument extends AbstractTsDocument<ExtendedAirlineDecompositionSpec, ExtendedAirlineResults> {

    private final ModellingContext context;

    public FractionalAirlineDecompositionDocument() {
        super(ExtendedAirlineDecompositionSpec.DEFAULT);
        context = ModellingContext.getActiveContext();
    }

    public FractionalAirlineDecompositionDocument(ModellingContext context) {
        super(ExtendedAirlineDecompositionSpec.DEFAULT);
        this.context = context;
    }

    @Override
    protected ExtendedAirlineResults internalProcess(ExtendedAirlineDecompositionSpec spec, TsData data) {
        // modify the spec and prepare data according to the time series
        ExtendedAirlineModellingSpec pspec = spec.getPreprocessing();
        DecompositionSpec dspec = spec.getDecomposition();
        ExtendedAirlineSpec sspec = pspec.getStochastic();
        TsDomain domain = data.getDomain();
        ExtendedAirlineDecompositionSpec nspec=spec;
        int freq = domain.getTsUnit().getAnnualFrequency();
        if (freq > 0) {
            sspec = sspec.toBuilder()
                    .periodicities(new double[]{freq})
                    .adjustToInt(true)
                    .build();
            pspec = pspec.toBuilder().stochastic(sspec).build();
            dspec=dspec.toBuilder()
                    .periodicities(new double[]{freq})
                    .adjustToInt(true)
                    .build();
            nspec=ExtendedAirlineDecompositionSpec.builder()
                    .preprocessing(pspec)
                    .decomposition(dspec)
                    .build();
        } else if (domain.getTsUnit().equals(TsUnit.WEEK)) {
            sspec = sspec.toBuilder()
                    .periodicities(new double[]{365.25 / 7})
                    .build();
            pspec = pspec.toBuilder().stochastic(sspec).build();
            dspec=dspec.toBuilder()
                    .periodicities(new double[]{365.25 / 7})
                    .build();
            nspec=ExtendedAirlineDecompositionSpec.builder()
                    .preprocessing(pspec)
                    .decomposition(dspec)
                    .build();
        } else if (!domain.getTsUnit().getChronoUnit().equals(ChronoUnit.DAYS)) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        return new ExtendedAirlineDecompositionKernel(nspec, context).process(data, new DefaultProcessingLog());
    }

}
