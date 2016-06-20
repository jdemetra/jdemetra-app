/*
 * Copyright 2016 National Bank of Belgium
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
package ec.nbdemetra.sa.output;

import ec.nbdemetra.ui.properties.ListSelectionEditor;
import ec.tss.sa.output.BasicConfiguration;
import java.util.Arrays;

/**
 *
 * @author Jean Palate
 */
public class Matrix extends ListSelectionEditor<String> {

    public Matrix() {
        super(Arrays.asList(BasicConfiguration.allDetails));
    }
}
