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
package demetra.desktop.disaggregation.descriptors;

import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.tempdisagg.univariate.TemporalDisaggregationSpec;


/**
 *
 * @author Jean
 */
abstract class BaseTemporalDisaggregationSpecUI implements IPropertyDescriptors{

    final TemporalDisaggregationSpecRoot root;
         
    BaseTemporalDisaggregationSpecUI(TemporalDisaggregationSpecRoot root){
        this.root =root;
    }
    
    TemporalDisaggregationSpec core(){return root.getCore();}
    
    boolean isRo(){return root.isRo();}
    
    void update(TemporalDisaggregationSpec nspec){
        root.core=nspec;
    }
   
}
