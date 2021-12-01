/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.regarima.descriptors;

import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.modelling.implementations.SarimaSpec;

/**
 *
 * @author PALATEJ
 */
abstract class BaseRegArimaSpecUI implements IPropertyDescriptors{
    
    final RegArimaSpecRoot root;
        
    BaseRegArimaSpecUI(RegArimaSpecRoot root){
        this.root =root;
    }
    
    RegArimaSpec core(){return root.getCore();}
    
    boolean isRo(){return root.ro_;}
    
   void update(EstimateSpec spec) {
        root.core = root.core.toBuilder().estimate(spec).build();
    }

    void update(SarimaSpec spec) {
        root.core = root.core.toBuilder().arima(spec).build();
    }

    void update(AutoModelSpec spec) {
        root.core = root.core.toBuilder().autoModel(spec).build();
    }

    void update(OutlierSpec spec) {
        root.core = root.core.toBuilder().outliers(spec).build();
    }

    void update(RegressionSpec spec) {
        root.core = root.core.toBuilder().regression(spec).build();
    }

    void update(TransformSpec spec) {
        root.core = root.core.toBuilder().transform(spec).build();
    }

    void update(EasterSpec spec) {
        update(root.core.getRegression()
                .toBuilder()
                .easter(spec)
                .build());
    }

    void update(TradingDaysSpec spec) {
        update(root.core.getRegression()
                .toBuilder()
                .tradingDays(spec)
                .build());
    }
}
