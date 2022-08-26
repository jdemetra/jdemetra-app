/*
 * Copyright 2022 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.stl.extractors;

import demetra.information.InformationExtractor;
import demetra.information.InformationMapping;
import demetra.sa.SaDictionaries;
import demetra.timeseries.TsData;
import demetra.timeseries.TsUnit;
import demetra.toolkit.dictionaries.RegressionDictionaries;
import java.time.temporal.ChronoUnit;
import jdplus.stl.StlPlusResults;
import nbbrd.service.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(InformationExtractor.class)
public class StlPlusExtractor extends InformationMapping<StlPlusResults> {
    
    private static int freq(StlPlusResults source){
        TsUnit unit = source.getSeries().getTsUnit();
        int a=unit.getAnnualFrequency();
        if (a > 0)
            return a;
        if (unit.getChronoUnit() == ChronoUnit.DAYS){
            if (unit.getAmount() == 1)
                return 365;
            else if (unit.getAmount() == 7)
                return 52;
        }
        return 0;
    }

    public static final String Y = "y", T = "t", S = "s", I = "i", SA = "sa", SY = "sy", SW = "sw";

    public StlPlusExtractor() {
        set(RegressionDictionaries.Y, TsData.class, source
                -> source.getSeries());
        set(SaDictionaries.T, TsData.class, source
                -> source.getTrend());
        set(SaDictionaries.SA, TsData.class, source
                -> source.getSa());
        set(SaDictionaries.S, TsData.class, source
                -> source.seasonal());
        set(SaDictionaries.I, TsData.class, source
                -> source.getIrregular());
        set(SY, TsData.class, source
                -> {
            int freq = freq(source);
            if (freq<=0)
                return null;
            return source.getSeasonals().get(freq);
        });
        set(SW, TsData.class, source
                -> {
            int freq = freq(source);
            if (freq != 365)
                return null;
            return source.getSeasonals().get(7);
        });
     }

    @Override
public Class

<StlPlusResults> getSourceClass() {
        return StlPlusResults.class  
;
}

}
