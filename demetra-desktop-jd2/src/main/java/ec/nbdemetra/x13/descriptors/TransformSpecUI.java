/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.modelling.arima.x13.TransformSpec;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.DefaultTransformationType;
import ec.tstoolkit.timeseries.calendars.LengthOfPeriodType;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Kristof Bayens
 */
public class TransformSpecUI extends BaseRegArimaSpecUI {

    @Override
    public String toString() {
        return "";
    }

    private TransformSpec inner() {
        return core.getTransform();
    }

    TransformSpecUI(RegArimaSpecification spec, boolean ro) {
        super(spec, ro);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = fnDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = aicDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = adjustDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Messages("transformSpecUI.getDisplayName=Transformation")
    @Override
    public String getDisplayName() {
        return Bundle.transformSpecUI_getDisplayName();
    }

    public DefaultTransformationType getFunction() {
        return inner().getFunction();
    }

    public void setFunction(DefaultTransformationType value) {
        inner().setFunction(value);
        core.getRegression().getTradingDays().setAutoAdjust(value == DefaultTransformationType.Auto);
        if (value == DefaultTransformationType.None && inner().getAdjust() != LengthOfPeriodType.None){
            inner().setAdjust(LengthOfPeriodType.None);
            core.getRegression().getTradingDays().setLengthOfPeriod(LengthOfPeriodType.LeapYear);
        }
    }

    public double getAic() {
        return inner().getAICDiff();
    }

    public void setAic(double value) {
        inner().setAICDiff(value);
    }

    public LengthOfPeriodType getAdjust() {
        return inner().getAdjust();
    }

    public void setAdjust(LengthOfPeriodType value) {
        inner().setAdjust(value);
        if (value != LengthOfPeriodType.None) {
            core.getRegression().getTradingDays().setLengthOfPeriod(LengthOfPeriodType.None);
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int FN_ID = 1, AIC_ID = 2, ADJUST_ID = 3;

    @Messages({
        "transformSpecUI.fnDesc.name=function",
        "transformSpecUI.fnDesc.desc=[lam] None=no transformation of data; Log=takes logs of data; Auto:the program tests for the log-level specification."
    })
    private EnhancedPropertyDescriptor fnDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("function", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.transformSpecUI_fnDesc_name());
            desc.setShortDescription(Bundle.transformSpecUI_fnDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "transformSpecUI.aicDesc.name=AIC difference",
        "transformSpecUI.aicDesc.desc=[aicdiff] Defines the difference in AICC needed to accept no transformation when the automatic transformation selection option is invoked."
    })
    private EnhancedPropertyDescriptor aicDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("aic", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AIC_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_ || getFunction() != DefaultTransformationType.Auto);
            desc.setShortDescription(Bundle.transformSpecUI_aicDesc_desc());
            desc.setDisplayName(Bundle.transformSpecUI_aicDesc_name());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "transformSpecUI.adjustDesc.name=Adjust",
        "transformSpecUI.adjustDesc.desc=[adjust] Preadjustment of the series for length of period or leap year effects. The series is divided by the specified effect. Not available with the \"auto\" mode"
    })
    private EnhancedPropertyDescriptor adjustDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("adjust", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ADJUST_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_ || getFunction() != DefaultTransformationType.Log);
            desc.setShortDescription(Bundle.transformSpecUI_adjustDesc_desc());
            desc.setDisplayName(Bundle.transformSpecUI_adjustDesc_name());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
