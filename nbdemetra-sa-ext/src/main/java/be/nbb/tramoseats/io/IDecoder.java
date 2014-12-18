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

package be.nbb.tramoseats.io;

import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import java.io.BufferedReader;

/**
 *
 * @author Jean Palate
 */
public interface IDecoder {
    
    public static class Document{
        public String name;
        public TsData series;
        public TramoSeatsSpecification spec;
    }
    
    TramoSeatsSpecification decodeSpec(BufferedReader reader);
    Document decodeDocument(BufferedReader reader);
}
