/*
 * Copyright 2023 National Bank of Belgium
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
package demetra.desktop.mstl.ui;

import demetra.desktop.ui.properties.l2fprod.ArrayRenderer;
import demetra.desktop.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import demetra.desktop.ui.properties.l2fprod.CustomPropertyRendererFactory;
import demetra.stl.SeasonalSpec;
import demetra.stl.MStlSpec;



/**
 *
 * @author Jean Palate
 */
@lombok.Getter
@lombok.AllArgsConstructor
public class MStlPlusSpecRoot  {
    
    static{
        CustomPropertyEditorRegistry.INSTANCE.register(SeasonalSpec[].class, new SeasonalSpecsEditor());
        CustomPropertyRendererFactory.INSTANCE.getRegistry().registerRenderer(SeasonalSpec[].class, new ArrayRenderer());
    }
    
    MStlSpec core;
    boolean ro;
}
