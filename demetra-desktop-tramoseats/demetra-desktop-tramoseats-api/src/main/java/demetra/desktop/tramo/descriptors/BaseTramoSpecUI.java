/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.tramo.descriptors;

import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.arima.SarimaSpec;
import demetra.modelling.TransformationType;
import demetra.tramo.AutoModelSpec;
import demetra.tramo.CalendarSpec;
import demetra.tramo.EasterSpec;
import demetra.tramo.EstimateSpec;
import demetra.tramo.MeanSpec;
import demetra.tramo.OutlierSpec;
import demetra.tramo.RegressionSpec;
import demetra.tramo.TradingDaysSpec;
import demetra.tramo.TramoSpec;
import demetra.tramo.TransformSpec;

/**
 *
 * @author PALATEJ
 */
abstract class BaseTramoSpecUI implements IPropertyDescriptors{
    
    final TramoSpecRoot root;
        
    BaseTramoSpecUI(TramoSpecRoot root){
        this.root =root;
    }
    
    TramoSpec core(){return root.getCore();}
    
    boolean isRo(){return root.ro;}
    
    boolean isTransformationDefined(){
        return root.getCore().getTransform().getFunction() != TransformationType.Auto;
    }
    
    void update(MeanSpec spec) {
        update(root.core.getRegression()
                .toBuilder()
                .mean(spec)
                .build());
    }

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

    void update(CalendarSpec spec) {
        update(root.core.getRegression()
                .toBuilder()
                .calendar(spec)
                .build());
    }

    void update(EasterSpec spec) {
        update(root.core.getRegression()
                .getCalendar()
                .toBuilder()
                .easter(spec)
                .build());
    }

    void update(TradingDaysSpec spec) {
        update(root.core.getRegression()
                .getCalendar()
                .toBuilder()
                .tradingDays(spec)
                .build());
    }
}
