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

import data.Data;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tstoolkit.Parameter;
import ec.tstoolkit.ParameterType;
import ec.tstoolkit.modelling.arima.tramo.ArimaSpec;
import ec.tstoolkit.modelling.arima.tramo.TradingDaysSpec;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jean Palate
 */
public class IDecoderTest {

    private final TramoSeatsSpecification sp0;
    private final TramoSeatsSpecification sp1;
 
    public IDecoderTest() {
        sp0 = TramoSeatsSpecification.RSAfull.clone();
        sp0.getTramoSpecification().setUsingAutoModel(false);
        ArimaSpec arima = sp0.getTramoSpecification().getArima();
        arima.setP(3);
        arima.setMean(true);
        sp1 = TramoSeatsSpecification.RSAfull.clone();
        sp1.getTramoSpecification().setUsingAutoModel(false);
        arima = sp1.getTramoSpecification().getArima();
        arima.setPhi(new Parameter[]{new Parameter(-.1, ParameterType.Fixed)});
        arima.setMean(true);
        sp1.getTramoSpecification().getRegression().getCalendar().getTradingDays().setAutomaticMethod(TradingDaysSpec.AutoMethod.FTest);
    }

    @Test
    public void testSp0() {
        LegacyEncoder encoder = new LegacyEncoder(null);
        LegacyDecoder decoder = new LegacyDecoder(null);
        String str = encoder.encode(sp0);
        System.out.println(str);
        TramoSeatsSpecification nsp0 = decoder.decodeSpec(reader(str));
        str = encoder.encode(sp1);
        System.out.println(str);
        //assertTrue(sp0.equals(nsp0));
        //assertTrue(sp1.equals(nsp1));
    }

    private BufferedReader reader(String str) {
        StringReader reader = new StringReader(str);
        return new BufferedReader(reader);
    }

    @Test
    public void testTramoSeatsRSA() {
        LegacyEncoder encoder = new LegacyEncoder(null);
        LegacyDecoder decoder = new LegacyDecoder(null);
        String str = encoder.encode(TramoSeatsSpecification.RSA0);
        TramoSeatsSpecification n0 = decoder.decodeSpec(reader(str));
        assertTrue(n0.equals(TramoSeatsSpecification.RSA0));
        str = encoder.encode(TramoSeatsSpecification.RSA1);
        TramoSeatsSpecification n1 = decoder.decodeSpec(reader(str));
        assertTrue(n1.equals(TramoSeatsSpecification.RSA1));
        str = encoder.encode(TramoSeatsSpecification.RSA2);
        TramoSeatsSpecification n2 = decoder.decodeSpec(reader(str));
//        assertTrue(n2.equals(TramoSeatsSpecification.RSA2));
        str = encoder.encode(TramoSeatsSpecification.RSA3);
        TramoSeatsSpecification n3 = decoder.decodeSpec(reader(str));
//        assertTrue(n3.equals(TramoSeatsSpecification.RSA3));
        str = encoder.encode(TramoSeatsSpecification.RSA4);
        TramoSeatsSpecification n4 = decoder.decodeSpec(reader(str));
//        assertTrue(n4.equals(TramoSeatsSpecification.RSA4));
        str = encoder.encode(TramoSeatsSpecification.RSA5);
        TramoSeatsSpecification n5 = decoder.decodeSpec(reader(str));
//        assertTrue(n5.equals(TramoSeatsSpecification.RSA5));
    }

    @Test
    public void testLegacyDocument() {
        LegacyEncoder encoder = new LegacyEncoder(null);
        LegacyDecoder decoder = new LegacyDecoder(null);
        String str = encoder.encode("Production", Data.P, TramoSeatsSpecification.RSA4);
        IDecoder.Document doc = decoder.decodeDocument(reader(str));
        System.out.println(doc.name);
        System.out.println(doc.series);
        System.out.println(doc.spec);
    }
}
