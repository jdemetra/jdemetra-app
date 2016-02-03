/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.descriptors;

import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.satoolkit.seats.SeatsSpecification;
import ec.satoolkit.seats.SeatsSpecification.EstimationMethod;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Kristof Bayens
 */
public class SeatsSpecUI extends BaseSeatsSpecUI {
    
    static{
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(SeatsSpecification.ApproximationMode.class);
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(SeatsSpecification.EstimationMethod.class);
    }

    public SeatsSpecUI(SeatsSpecification spec, boolean ro) {
        super(spec, ro);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = noadmissDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = xlDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = rmodDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = epsphiDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = smodDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = smod1Desc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = wkDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    @Messages("seatsSpecUI.getDisplayName=Seats")
    public String getDisplayName() {
        return Bundle.seatsSpecUI_getDisplayName();
    }

    public double getRMod() {
        return core.getTrendBoundary();
    }

    public void setRMod(double value) {
        core.setTrendBoundary(value);
    }

    public double getSMod() {
        return core.getSeasBoundary();
    }

    public void setSMod(double value) {
        core.setSeasBoundary(value);
    }
    
    public double getSMod1() {
        return core.getSeasBoundary1();
    }

    public void setSMod1(double value) {
        core.setSeasBoundary1(value);
    }
    
    public double getEpsPhi() {
        return core.getSeasTolerance();
    }

    public void setEpsPhi(double value) {
        core.setSeasTolerance(value);
    }

    public SeatsSpecification.ApproximationMode getApproximationMode() {
        return core.getApproximationMode();
    }

    public void setApproximationMode(SeatsSpecification.ApproximationMode value) {
        core.setApproximationMode(value);
    }

    public double getXl() {
        return core.getXlBoundary();
    }

    public void setXl(double value) {
        core.setXlBoundary(value);
    }

    public EstimationMethod getMethod() {
        return core.getMethod();
    }

    public void setMethod(EstimationMethod value) {
        core.setMethod(value);
    }
    private static final int RMOD_ID = 0, EPSPHI_ID = 1, SMOD_ID=2, SMOD1_ID=3, NOADMISS_ID = 4, XL_ID = 5, WK_ID = 6;

    @Messages({
        "seatsSpecUI.rmodDesc.name=Trend boundary",
        "seatsSpecUI.rmodDesc.desc=[rmod] Boundary from which a positive AR root is integrated in the trend component."
    })
    private EnhancedPropertyDescriptor rmodDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("RMod", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, RMOD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.seatsSpecUI_rmodDesc_name());
            desc.setShortDescription(Bundle.seatsSpecUI_rmodDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    @Messages({
        "seatsSpecUI.smodDesc.name=Seasonal boundary",
        "seatsSpecUI.smodDesc.desc=[smod] Boundary from which a negative AR root is integrated in the seasonal component."
    })
    private EnhancedPropertyDescriptor smodDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SMod", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SMOD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.seatsSpecUI_smodDesc_name());
            desc.setShortDescription(Bundle.seatsSpecUI_smodDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "seatsSpecUI.smod1Desc.name=Seas. boundary (unique)",
        "seatsSpecUI.smod1Desc.desc=[smod] Boundary from which a negative AR root is integrated in the seasonal component when the root is the unique seasonal root."
    })
    private EnhancedPropertyDescriptor smod1Desc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SMod1", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SMOD1_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.seatsSpecUI_smod1Desc_name());
            desc.setShortDescription(Bundle.seatsSpecUI_smod1Desc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "seatsSpecUI.wkDesc.name=Method",
        "seatsSpecUI.wkDesc.desc=Estimation method of the component"
    })
    private EnhancedPropertyDescriptor wkDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("method", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, WK_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.seatsSpecUI_wkDesc_name());
            desc.setShortDescription(Bundle.seatsSpecUI_wkDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "seatsSpecUI.epsphiDesc.name=Seasonal tolerance",
        "seatsSpecUI.epsphiDesc.desc=[epsphi] Tolerance(measured in degrees) to allocate complex ar roots into the seasonal component."
    })
    private EnhancedPropertyDescriptor epsphiDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("EpsPhi", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, EPSPHI_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.seatsSpecUI_epsphiDesc_name());
            desc.setShortDescription(Bundle.seatsSpecUI_epsphiDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "seatsSpecUI.noadmissDesc.name=Approximation mode",
        "seatsSpecUI.noadmissDesc.desc=[noadmiss] When model does not accept an admissible decomposition, force to use an approximation. The approximation may be a noisy model or defined as in previous Seats code"
    })
    private EnhancedPropertyDescriptor noadmissDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ApproximationMode", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NOADMISS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.seatsSpecUI_noadmissDesc_name());
            desc.setShortDescription(Bundle.seatsSpecUI_noadmissDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "seatsSpecUI.xlDesc.name=MA unit root boundary",
        "seatsSpecUI.xlDesc.desc=[xl] When the modulus of an estimated root falls in the range(xl,1), it is set to 1 if it is in AR; if root is in MA, it is set equal to xl."
    })
    private EnhancedPropertyDescriptor xlDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Xl", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, XL_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.seatsSpecUI_xlDesc_name());
            desc.setShortDescription(Bundle.seatsSpecUI_xlDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
