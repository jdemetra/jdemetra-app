/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.nbdemetra.core;

import ec.tss.tsproviders.utils.ByteArrayConverter;
import java.io.IOException;
import org.xerial.snappy.Snappy;

/**
 *
 * @author Philippe Charles
 */
final class SnappyConverter extends ByteArrayConverter {

    @Override
    public byte[] fromDoubleArray(double[] input) {
        try {
            return Snappy.compress(input);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public double[] toDoubleArray(byte[] input) {
        try {
            return Snappy.uncompressDoubleArray(input);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
