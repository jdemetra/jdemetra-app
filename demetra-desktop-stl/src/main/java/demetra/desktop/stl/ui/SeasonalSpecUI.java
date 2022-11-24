/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.stl.ui;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.stl.SeasonalSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public class SeasonalSpecUI implements IObjectDescriptor<SeasonalSpec> {

    @Override
    public String toString() {
        return "seasonal-" + seasSpec.getPeriod();
    }

    private SeasonalSpec seasSpec;
    private final Consumer<SeasonalSpec> callback;
    private final boolean ro;

    @Override
    public SeasonalSpec getCore() {
        return seasSpec;
    }
    
    public SeasonalSpecUI(){
        seasSpec=new SeasonalSpec();
        ro=false;
        callback=null;
    }
    
    public SeasonalSpecUI(SeasonalSpec loess, boolean ro, Consumer<SeasonalSpec> callback) {
        this.seasSpec = loess;
        this.ro = ro;
        this.callback = callback;
    }

    public SeasonalSpecUI duplicate(){
        return new SeasonalSpecUI(seasSpec, ro, callback);
    }

    public PeriodUI getPeriod() {
        return new PeriodUI(seasSpec.getPeriod(), ro, period
                -> {
            seasSpec=seasSpec.toBuilder()
                    .period(period)
                    .build();
            if (callback != null) {
                callback.accept(seasSpec);
            }
        });
    }

    public LoessSpecUI getSeasonalSpec() {
        return new LoessSpecUI(seasSpec.getSeasonalSpec(), ro, sspec -> {
            seasSpec = seasSpec.toBuilder().seasonalSpec(sspec).build();
            if (callback != null) {
                callback.accept(seasSpec);
            }
        });
    }

    public LoessSpecUI getLowPassSpec() {
        return new LoessSpecUI(seasSpec.getLowPassSpec(), ro, lpspec -> {
            seasSpec = seasSpec.toBuilder().lowPassSpec(lpspec).build();
            if (callback != null) {
                callback.accept(seasSpec);
            }
        });
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = periodDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = seasonalDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = lowpassDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @NbBundle.Messages({
        "SeasonalSpecUI.periodDesc.name=PERIODICITY",
        "SeasonalSpecUI.periodDesc.desc=Periodicity of the filter."
    })
    private EnhancedPropertyDescriptor periodDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Period", this.getClass(), "getPeriod", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PERIOD_ID);
            desc.setDisplayName(Bundle.SeasonalSpecUI_periodDesc_name());
            desc.setShortDescription(Bundle.SeasonalSpecUI_periodDesc_desc());
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "SeasonalSpecUI.seasonalDesc.name=SEASONAL",
        "SeasonalSpecUI.seasonalDesc.desc=Specification of the seasonal filter."
    })
    private EnhancedPropertyDescriptor seasonalDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Seasonal", this.getClass(), "getSeasonalSpec", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, S_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.SeasonalSpecUI_seasonalDesc_name());
            desc.setShortDescription(Bundle.SeasonalSpecUI_seasonalDesc_desc());
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "SeasonalSpecUI.lowpassDesc.name=LOW-PASS",
        "SeasonalSpecUI.lowpassDesc.desc=Specification of the low-pass filter."
    })
    private EnhancedPropertyDescriptor lowpassDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LowPass", this.getClass(), "getLowPassSpec", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LP_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.SeasonalSpecUI_lowpassDesc_name());
            desc.setShortDescription(Bundle.SeasonalSpecUI_lowpassDesc_desc());
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private static final int PERIOD_ID = 1, S_ID = 2, LP_ID = 3;

    @Override
    @NbBundle.Messages("SeasonalSpecUI.getDisplayName=seasonal spec")
    public String getDisplayName() {
        return Bundle.SeasonalSpecUI_getDisplayName();
    }

}
