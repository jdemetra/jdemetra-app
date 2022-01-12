/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.tramoseats.descriptors;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.seats.DecompositionSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public class DecompositionSpecUI extends BaseTramoSeatsSpecUI {
    DecompositionSpecUI(TramoSeatsSpecRoot root) {
        super(root);
     }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = nfcastsDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = nbcastsDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = noadmissDesc();
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
    @NbBundle.Messages("decompositionSpecUI.getDisplayName=Seats")
    public String getDisplayName() {
        return Bundle.decompositionSpecUI_getDisplayName();
    }

    public int getForecastsLength() {
        return seats().getForecastCount();
    }

    public void setForecastsLength(int value) {
        update(seats().toBuilder().forecastCount(value).build());
    }

    public int getBackcastsLength() {
        return seats().getBackcastCount();
    }

    public void setBackcastsLength(int value) {
        update(seats().toBuilder().backcastCount(value).build());
    }

    public double getRMod() {
        return seats().getTrendBoundary();
    }

    public void setRMod(double value) {
        update(seats().toBuilder().trendBoundary(value).build());
    }

    public double getSMod() {
        return seats().getSeasBoundary();
    }

    public void setSMod(double value) {
        update(seats().toBuilder().seasBoundary(value).build());
    }

    public double getSMod1() {
        return seats().getSeasBoundaryAtPi();
    }

    public void setSMod1(double value) {
        update(seats().toBuilder().seasBoundaryAtPi(value).build());
    }

    public double getEpsPhi() {
        return seats().getSeasTolerance();
    }

    public void setEpsPhi(double value) {
        update(seats().toBuilder().seasTolerance(value).build());
    }

    public DecompositionSpec.ModelApproximationMode getApproximationMode() {
        return seats().getApproximationMode();
    }

    public void setApproximationMode(DecompositionSpec.ModelApproximationMode value) {
        update(seats().toBuilder().approximationMode(value).build());
    }

    public double getXl() {
        return seats().getXlBoundary();
    }

    public void setXl(double value) {
        update(seats().toBuilder().xlBoundary(value).build());
    }

    public DecompositionSpec.ComponentsEstimationMethod getMethod() {
        return seats().getMethod();
    }

    public void setMethod(DecompositionSpec.ComponentsEstimationMethod value) {
        update(seats().toBuilder().method(value).build());
    }
    private static final int RMOD_ID = 0, EPSPHI_ID = 1, SMOD_ID = 2, SMOD1_ID = 3, NOADMISS_ID = 4, XL_ID = 5, WK_ID = 6, NFCASTS_ID = 10, NBCASTS_ID = 11;

    @NbBundle.Messages({
        "decompositionSpecUI.rmodDesc.name=Trend boundary",
        "decompositionSpecUI.rmodDesc.desc=[rmod] Boundary from which a positive AR root is integrated in the trend component."
    })
    private EnhancedPropertyDescriptor rmodDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("RMod", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, RMOD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.decompositionSpecUI_rmodDesc_name());
            desc.setShortDescription(Bundle.decompositionSpecUI_rmodDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "decompositionSpecUI.smodDesc.name=Seasonal boundary",
        "decompositionSpecUI.smodDesc.desc=[smod] Boundary from which a negative AR root is integrated in the seasonal component."
    })
    private EnhancedPropertyDescriptor smodDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SMod", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SMOD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.decompositionSpecUI_smodDesc_name());
            desc.setShortDescription(Bundle.decompositionSpecUI_smodDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "decompositionSpecUI.nbcastsDesc.name=Prediction length",
        "decompositionSpecUI.nbcastsDesc.desc=[npred] Number of forecasts used in the decomposition. Negative values correspond to numbers of years"
    })
    private EnhancedPropertyDescriptor nbcastsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BackcastsLength", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NBCASTS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.decompositionSpecUI_nbcastsDesc_name());
            desc.setShortDescription(Bundle.decompositionSpecUI_nbcastsDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "decompositionSpecUI.nfcastsDesc.name=Forecasts length",
        "decompositionSpecUI.nfcastsDesc.desc=[npred] Number of forecasts used in the decomposition. Negative values correspond to numbers of years"
    })
    private EnhancedPropertyDescriptor nfcastsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ForecastsLength", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NFCASTS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.decompositionSpecUI_nfcastsDesc_name());
            desc.setShortDescription(Bundle.decompositionSpecUI_nfcastsDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "decompositionSpecUI.smod1Desc.name=Seas. boundary (unique)",
        "decompositionSpecUI.smod1Desc.desc=[smod] Boundary from which a negative AR root is integrated in the seasonal component when the root is the unique seasonal root."
    })
    private EnhancedPropertyDescriptor smod1Desc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SMod1", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SMOD1_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.decompositionSpecUI_smod1Desc_name());
            desc.setShortDescription(Bundle.decompositionSpecUI_smod1Desc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "decompositionSpecUI.wkDesc.name=Method",
        "decompositionSpecUI.wkDesc.desc=Estimation method of the component"
    })
    private EnhancedPropertyDescriptor wkDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("method", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, WK_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.decompositionSpecUI_wkDesc_name());
            desc.setShortDescription(Bundle.decompositionSpecUI_wkDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "decompositionSpecUI.epsphiDesc.name=Seasonal tolerance",
        "decompositionSpecUI.epsphiDesc.desc=[epsphi] Tolerance(measured in degrees) to allocate complex ar roots into the seasonal component."
    })
    private EnhancedPropertyDescriptor epsphiDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("EpsPhi", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, EPSPHI_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.decompositionSpecUI_epsphiDesc_name());
            desc.setShortDescription(Bundle.decompositionSpecUI_epsphiDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "decompositionSpecUI.noadmissDesc.name=Approximation mode",
        "decompositionSpecUI.noadmissDesc.desc=[noadmiss] When model does not accept an admissible decomposition, force to use an approximation. The approximation may be a noisy model or defined as in previous Seats code"
    })
    private EnhancedPropertyDescriptor noadmissDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ApproximationMode", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NOADMISS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.decompositionSpecUI_noadmissDesc_name());
            desc.setShortDescription(Bundle.decompositionSpecUI_noadmissDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "decompositionSpecUI.xlDesc.name=MA unit root boundary",
        "decompositionSpecUI.xlDesc.desc=[xl] When the modulus of an estimated root falls in the range(xl,1), it is set to 1 if it is in AR; if root is in MA, it is set equal to xl."
    })
    private EnhancedPropertyDescriptor xlDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Xl", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, XL_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.decompositionSpecUI_xlDesc_name());
            desc.setShortDescription(Bundle.decompositionSpecUI_xlDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
