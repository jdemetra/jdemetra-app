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

import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.stl.MStlSpec;

/**
 *
 * @author PALATEJ
 */
abstract class BaseMStlPlusSpecUI implements IPropertyDescriptors{
    
    final MStlPlusSpecRoot root;
        
    BaseMStlPlusSpecUI(MStlPlusSpecRoot root){
        this.root =root;
    }
    
    MStlSpec core(){return root.getCore();}
    
    boolean isRo(){return root.ro;}
    
//    void update(EstimateSpec spec) {
//        root.core = root.core.toBuilder().estimate(spec).build();
//    }
//
//    void update(ExtendedAirlineSpec spec) {
//        root.core = root.core.toBuilder().stochastic(spec).build();
//    }
//
//    void update(OutlierSpec spec) {
//        root.core = root.core.toBuilder().outlier(spec).build();
//    }
//
//    void update(RegressionSpec spec) {
//        root.core = root.core.toBuilder().regression(spec).build();
//    }
//
//    void update(TransformSpec spec) {
//        root.core = root.core.toBuilder().transform(spec).build();
//    }
//
//    void update(EasterSpec spec) {
//        update(root.core.getRegression()
//                .toBuilder()
//                .easter(spec)
//                .build());
//    }
//
//    void update(HolidaysSpec spec) {
//        update(root.core.getRegression()
//                .toBuilder()
//                .calendar(spec)
//                .build());
//    }
}
