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
        desc = wkDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Seats";
    }

    public double getRMod() {
        return core.getTrendBoundary();
    }

    public void setRMod(double value) {
        core.setTrendBoundary(value);
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
    private static final int RMOD_ID = 0, EPSPHI_ID = 1, NOADMISS_ID = 2, XL_ID = 3, WK_ID = 4;

    private EnhancedPropertyDescriptor rmodDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("RMod", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, RMOD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(RMOD_NAME);
            desc.setShortDescription(RMOD_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor wkDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("method", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, WK_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(METHOD_NAME);
            desc.setShortDescription(METHOD_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor epsphiDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("EpsPhi", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, EPSPHI_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(EPSPHI_NAME);
            desc.setShortDescription(EPSPHI_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor noadmissDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ApproximationMode", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NOADMISS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(NOADMISS_NAME);
            desc.setShortDescription(NOADMISS_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor xlDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Xl", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, XL_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(XL_NAME);
            desc.setShortDescription(XL_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    private static final String RMOD_NAME = "Trend boundary",
            EPSPHI_NAME = "Seasonal tolerance",
            NOADMISS_NAME = "Approximation mode",
            METHOD_NAME = "Method",
            XL_NAME = "MA unit root boundary";
    private static final String RMOD_DESC = "[rmod] Boundary from which an AR root is integrated in the trend component.",
            EPSPHI_DESC = "[epsphi] Tolerance(measured in degrees) to allocate ar roots into the seasonal component.",
            NOADMISS_DESC = "[noadmiss] When model does not accept an admissible decomposition, force to use an approximation. The approximation may be a noisy model or defined as in previous Seats code",
            METHOD_DESC = "Estimation method of the component",
            XL_DESC = "[xl] When the modulus of an estimated root falls in the range(xl,1), it is set to 1 if it is in AR; if root is in MA, it is set equal to xl.";
}
