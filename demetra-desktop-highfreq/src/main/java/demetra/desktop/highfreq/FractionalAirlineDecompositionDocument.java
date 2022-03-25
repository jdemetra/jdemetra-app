/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq;

import demetra.highfreq.ExtendedAirlineModellingSpec;
import demetra.highfreq.ExtendedAirlineSpec;
import demetra.processing.DefaultProcessingLog;
import demetra.processing.ProcessingLog;
import demetra.timeseries.AbstractTsDocument;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDomain;
import demetra.timeseries.TsUnit;
import demetra.timeseries.calendars.Calendar;
import demetra.timeseries.calendars.CalendarDefinition;
import demetra.timeseries.calendars.Holiday;
import demetra.timeseries.calendars.HolidaysOption;
import demetra.timeseries.regression.ModellingContext;
import java.time.temporal.ChronoUnit;
import jdplus.highfreq.ExtendedAirlineKernel;
import jdplus.highfreq.ExtendedAirlineEstimation;
import jdplus.highfreq.ExtendedRegAirlineModel;
import jdplus.math.matrices.FastMatrix;
import jdplus.timeseries.calendars.HolidaysUtility;

/**
 *
 * @author PALATEJ
 */
public class FractionalAirlineDecompositionDocument extends AbstractTsDocument<ExtendedAirlineModellingSpec, ExtendedRegAirlineModel> {

    private final ModellingContext context;

    public FractionalAirlineDecompositionDocument() {
        super(ExtendedAirlineModellingSpec.DEFAULT);
        context = ModellingContext.getActiveContext();
    }

    public FractionalAirlineDecompositionDocument(ModellingContext context) {
        super(ExtendedAirlineModellingSpec.DEFAULT);
        this.context = context;
    }

    @Override
    protected ExtendedRegAirlineModel internalProcess(ExtendedAirlineModellingSpec spec, TsData data) {
        // modify the spec and prepare data according to the time series
        ExtendedAirlineSpec sspec = spec.getStochastic();
        TsDomain domain = data.getDomain();
        ExtendedAirlineModellingSpec nspec = spec;
        int freq = domain.getTsUnit().getAnnualFrequency();
        if (freq > 0) {
            sspec = sspec.toBuilder()
                    .periodicities(new double[]{freq})
                    .adjustToInt(true)
                    .build();
            nspec = spec.toBuilder().stochastic(sspec).build();
        } else if (domain.getTsUnit().equals(TsUnit.WEEK)) {
            sspec = sspec.toBuilder()
                    .periodicities(new double[]{365.25 / 7})
                    .build();
            nspec = spec.toBuilder().stochastic(sspec).build();
        } else if (!domain.getTsUnit().getChronoUnit().equals(ChronoUnit.DAYS)) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        return new ExtendedAirlineKernel(nspec, context).process(data, new DefaultProcessingLog());
    }

}
