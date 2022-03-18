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
import demetra.timeseries.calendars.Calendar;
import demetra.timeseries.calendars.CalendarDefinition;
import demetra.timeseries.calendars.Holiday;
import demetra.timeseries.calendars.HolidaysOption;
import demetra.timeseries.regression.ModellingContext;
import java.time.temporal.ChronoUnit;
import jdplus.fractionalairline.FractionalAirlineKernel;
import jdplus.highfreq.FractionalAirlineEstimation;
import jdplus.math.matrices.FastMatrix;
import jdplus.timeseries.calendars.HolidaysUtility;

/**
 *
 * @author PALATEJ
 */
public class FractionalAirlineDecompositionDocument extends AbstractTsDocument<FractionalAirlineSpec, FractionalAirlineEstimation> {

    private final ModellingContext context;

    public FractionalAirlineDecompositionDocument() {
        super(FractionalAirlineSpec.DEFAULT_Y);
        context = ModellingContext.getActiveContext();
    }

    public FractionalAirlineDecompositionDocument(ModellingContext context) {
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

        if (spec.getCalendar() != null) {
            CalendarDefinition cdef = context.getCalendars().get(spec.getCalendar());
            if (cdef instanceof Calendar) {
                Calendar c = (Calendar) cdef;
                Holiday[] holidays = c.getHolidays();
                if (holidays.length > 0) {
                    FastMatrix H = HolidaysUtility.regressionVariables(c.getHolidays(), domain, spec.getHolidaysOption(), new int[]{6, 7}, spec.isSingle());
                    String[] names = spec.isSingle() ? new String[]{"holidays"} : HolidaysUtility.names(c.getHolidays());
                    spec = spec.toBuilder()
                            .X(H)
                            .Xnames(names)
                            .build();
                }
            }
        }
        set(spec);
        return FractionalAirlineKernel.process(data.getValues(), spec);
    }

}
