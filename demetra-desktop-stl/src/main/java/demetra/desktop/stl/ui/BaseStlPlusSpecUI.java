/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.stl.ui;

import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.stl.MStlSpec;
import demetra.stl.StlSpec;

/**
 *
 * @author PALATEJ
 */
abstract class BaseStlPlusSpecUI implements IPropertyDescriptors{
    
    final StlPlusSpecRoot root;
        
    BaseStlPlusSpecUI(StlPlusSpecRoot root){
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
