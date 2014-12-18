/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced.descriptors.mixedfrequencies;

import ec.tstoolkit.Parameter;
import ec.tstoolkit.arima.special.mixedfrequencies.MixedFrequenciesSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.arima.DefaultArimaSpec;
import ec.tstoolkit.modelling.arima.tramo.ArimaSpec;
import ec.tstoolkit.modelling.arima.tramo.AutoModelSpec;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kristof Bayens
 */
public class ArimaSpecUI extends BaseSpecUI {

    ArimaSpecUI(MixedFrequenciesSpecification spec) {
        super(spec);
    }

    private DefaultArimaSpec arima() {
        return core.getArima();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(').append(core.getArima().getP()).append(", ").append(core.getArima().getD()).append(", ").append(core.getArima().getQ()).append(")(").append(core.getArima().getBP()).append(", ").append(core.getArima().getBD()).append(", ").append(core.getArima().getBQ()).append(')');
        return builder.toString();
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = meanDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = pDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = phiDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = dDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = qDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = thetaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = bpDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = bphiDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = bdDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = bqDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = bthetaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Arima";
    }

    public int getP() {
        return arima().getP();
    }

    public void setP(int value) {
        arima().setP(value);
    }

    public int getD() {
        return arima().getD();
    }

    public void setD(int value) {
        arima().setD(value);
    }

    public int getQ() {
        return arima().getQ();
    }

    public void setQ(int value) {
        arima().setQ(value);
    }

    public int getBP() {
        return arima().getBP();
    }

    public void setBP(int value) {
        arima().setBP(value);
    }

    public int getBD() {
        return arima().getBD();
    }

    public void setBD(int value) {
        arima().setBD(value);
    }

    public int getBQ() {
        return arima().getBQ();
    }

    public void setBQ(int value) {
        arima().setBQ(value);
    }

    public Parameter[] getPhi() {
        return arima().getPhi();
    }

    public void setPhi(Parameter[] value) {
        arima().setPhi(value);
    }

    public Parameter[] getTheta() {
        return arima().getTheta();
    }

    public void setTheta(Parameter[] value) {
        arima().setTheta(value);
    }

    public Parameter[] getBPhi() {
        return arima().getBPhi();
    }

    public void setBPhi(Parameter[] value) {
        arima().setBPhi(value);
    }

    public Parameter[] getBTheta() {
        return arima().getBTheta();
    }

    public void setBTheta(Parameter[] value) {
        arima().setBTheta(value);
    }

    public boolean isMean() {
        return arima().isMean();
    }

    public void setMean(boolean value) {
        arima().setMean(value);
    }

    private EnhancedPropertyDescriptor pDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("P", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, P_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(P_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor dDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("D", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, D_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(D_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor qDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Q", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, Q_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Q_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor bpDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BP", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BP_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(BP_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor bdDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BD", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(BD_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor bqDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BQ", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BQ_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(BQ_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor phiDesc() {
        if (core.getArima().getP() == 0)
            return null;
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Phi", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PHI_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName("phi");
            desc.setShortDescription(PHI_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor thetaDesc() {
        if (core.getArima().getQ() == 0)
            return null;
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Theta", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, THETA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName("theta");
            desc.setShortDescription(THETA_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor bphiDesc() {
        if (core.getArima().getBP() == 0)
            return null;
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BPhi", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BPHI_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName("bphi");
            desc.setShortDescription(BPHI_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor bthetaDesc() {
         if (core.getArima().getBQ() == 0)
            return null;
       try {
            PropertyDescriptor desc = new PropertyDescriptor("BTheta", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BTHETA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName("btheta");
            desc.setShortDescription(BTHETA_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor meanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Mean", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MEAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(MEAN_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    private static final String ENABLED_NAME = "Automatic",
            ACCDEF_NAME = "Accept Default",
            TD_NAME = "Trading days pretest",
            AMI_NAME = "Compare to default",
            UB1_NAME = "Initial unit root limit",
            UB2_NAME = "Final unit root limit",
            CANCEL_NAME = "Cancelation limit",
            PC_NAME = "LjungBox limit",
            TSIG_NAME = "ArmaLimit",
            PCR_NAME = "Reduce CV";
    private static final String ENABLED_DESC = "Enables automatic modelling.",
            ACCDEF_DESC = "[fal] Controls whether the default model is acceptable.",
            TD_DESC = "Controls whether the new initial test on td is executed.",
            AMI_DESC = "[amicompare] Controls whether the final model is compared to the default model.",
            UB1_DESC = "(ub1] Initial unit root limit in the automatic differencing procedure.",
            UB2_DESC = "[ub2] Final unit root limit in the automatic differencing procedure.",
            CANCEL_DESC = "[cancel] Cancelation limit for AR and MA roots.",
            TSIG_DESC = "[tsig] Threshold value for t-statistics of ARMA coefficients used for final test of model parsimony.",
            PC_DESC = "[pc] Ljung-Box Q statistic limit for the acceptance of a model.",
            PCR_DESC = "[pcr] The percentage by which the outlier critical value will be reduced when an identified model is found to have a Ljung-Box Q statistic with an unacceptable confidence coefficient.";
    private static final String P_DESC = "[p] Regular auto-regresssive order",
            D_DESC = "[d] Regular differencing order",
            Q_DESC = "[q] Regular moving average order",
            BP_DESC = "[bp] Seasonal auto-regressive order",
            BD_DESC = "[bd] Seasonal differencing order",
            BQ_DESC = "[bq] Seasonal moving average order",
            PHI_DESC = "[phi, jpr] Coefficients of the regular auto-regressive polynomial (true signs)",
            THETA_DESC = "[th, jqr] Coefficients of the regular moving average polynomial (true signs)",
            BPHI_DESC = "[bphi, jqr] Coefficients of the seasonal auto-regressive polynomial (true signs)",
            BTHETA_DESC = "[bth, jqs] Coefficients of the seasonal moving average polynomial (true signs)",
            MEAN_DESC = "[imean] Mean correction";
    private static final int ENABLED_ID = 0, ACCDEF_ID = 1, TD_ID = 2, AMI_ID = 3, UB1_ID = 4, UB2_ID = 5,
            CANCEL_ID = 6, TSIG_ID = 7, PCR_ID = 8, PC_ID = 9;
    private static final int P_ID = 10, D_ID = 11, Q_ID = 12, BP_ID = 13, BD_ID = 14, BQ_ID = 15,
            PHI_ID = 16, THETA_ID = 17, BPHI_ID = 18, BTHETA_ID = 19, MEAN_ID = 20;
}
