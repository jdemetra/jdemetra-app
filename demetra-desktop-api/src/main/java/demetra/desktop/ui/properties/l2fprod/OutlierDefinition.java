/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.ui.properties.l2fprod;

import demetra.information.InformationSet;
import demetra.information.formatters.StringFormatter;
import demetra.timeseries.TsPeriod;
import demetra.timeseries.TsUnit;
import demetra.timeseries.regression.IOutlier;
import java.time.LocalDate;
import nbbrd.design.Development;

/**
 *
 * @author Jean Palate
 */
@Development(status = Development.Status.Release)
@lombok.Value
public class OutlierDefinition {

    public static enum OutlierType {
        AO, LS, TC, SO;
    }

    private LocalDate position;
    private OutlierType type;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(type).append(InformationSet.SEP).append(position.toString());
        return builder.toString();
    }

    public String toString(int freq) {
        if (freq == 0) {
            return toString();
        } else {
            StringBuilder builder = new StringBuilder();
            TsUnit unit = TsUnit.ofAnnualFrequency(freq);
            builder.append(type).append(InformationSet.SEP).append(TsPeriod.of(unit, position).display());
            return builder.toString();
        }
    }

}
