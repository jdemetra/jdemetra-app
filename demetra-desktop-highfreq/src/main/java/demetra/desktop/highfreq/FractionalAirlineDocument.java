/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq;

import demetra.highfreq.FractionalAirlineSpec;
import demetra.timeseries.AbstractTsDocument;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDomain;
import demetra.timeseries.TsUnit;
import demetra.timeseries.regression.ModellingContext;
import java.time.temporal.ChronoUnit;
import jdplus.fractionalairline.FractionalAirlineKernel;
import jdplus.highfreq.FractionalAirlineEstimation;

/**
 *
 * @author PALATEJ
 */
public class FractionalAirlineDocument extends AbstractTsDocument<FractionalAirlineSpec, FractionalAirlineEstimation> {

    private final ModellingContext context;

    public FractionalAirlineDocument() {
        super(FractionalAirlineSpec.DEFAULT_Y);
        context = ModellingContext.getActiveContext();
    }

    public FractionalAirlineDocument(ModellingContext context) {
        super(FractionalAirlineSpec.DEFAULT_Y);
        this.context = context;
    }

    @Override
    protected FractionalAirlineEstimation internalProcess(FractionalAirlineSpec spec, TsData data) {
        // modify the spec and prepare data according to the time series
        TsDomain domain = data.getDomain();
        int freq = domain.getTsUnit().getAnnualFrequency();
        if (freq > 0) {
            spec = spec.toBuilder()
                    .periodicities(new double[]{freq})
                    .adjustToInt(true)
                    .build();
        } else if (domain.getTsUnit().equals(TsUnit.WEEK)) {
            spec = spec.toBuilder()
                    .periodicities(new double[]{365.25 / 7})
                    .build();
        } else if (!domain.getTsUnit().getChronoUnit().equals(ChronoUnit.DAYS)) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        return FractionalAirlineKernel.process(data.getValues(), spec);
    }
}
