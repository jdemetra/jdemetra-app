/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.descriptors;

import ec.tstoolkit.Parameter;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.arima.tramo.ArimaSpec;
import ec.tstoolkit.modelling.arima.tramo.AutoModelSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Kristof Bayens
 */
public class ArimaSpecUI extends BaseTramoSpecUI {

    ArimaSpecUI(TramoSpecification spec, boolean ro) {
        super(spec, ro);
    }

    private AutoModelSpec ami() {
        return core.getAutoModel();
    }

    private ArimaSpec arima() {
        return core.getArima();
    }

    @Override
    public String toString() {
        if (core.isUsingAutoModel()) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append('(').append(core.getArima().getP()).append(", ").append(core.getArima().getD()).append(", ").append(core.getArima().getQ()).append(")(").append(core.getArima().getBP()).append(", ").append(core.getArima().getBD()).append(", ").append(core.getArima().getBQ()).append(')');
            return builder.toString();
        }
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = enabledDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = accdefDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = cancelDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = ub1Desc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = ub2Desc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = tsigDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = pcDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = pcrDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = amiDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = meanDesc();
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
    @Messages("arimaSpecUI.getDisplayName=Arima")
    public String getDisplayName() {
        return Bundle.arimaSpecUI_getDisplayName();
    }

    public boolean isEnabled() {
        AutoModelSpec spec = ami();
        return spec.isEnabled();
    }

    public void setEnabled(boolean value) {
        ami().setEnabled(value);
    }

    public boolean isAcceptDefault() {
        return ami().isAcceptDefault();
    }

    public void setAcceptDefault(boolean value) {
        ami().setAcceptDefault(value);
    }

    public boolean isAmiCompare() {
        return ami().isAmiCompare();
    }

    public void setAmiCompare(boolean value) {
        ami().setAmiCompare(value);
    }

    public double getUb1() {
        return ami().getUb1();
    }

    public void setUb1(double value) {
        ami().setUb1(value);
    }

    public double getUb2() {
        return ami().getUb2();
    }

    public void setUb2(double value) {
        ami().setUb2(value);
    }

    public double getCancel() {
        return ami().getCancel();
    }

    public void setCancel(double value) {
        ami().setCancel(value);
    }

    public double getPcr() {
        return ami().getPcr();
    }

    public void setPcr(double value) {
        ami().setPcr(value);
    }

    public double getTsig() {
        return ami().getTsig();
    }

    public void setTsig(double value) {
        ami().setTsig(value);
    }

    public double getPc() {
        return ami().getPc();
    }

    public void setPc(double value) {
        ami().setPc(value);
    }

    @Messages({
        "arimaSpecUI.enabledDesc.name=Automatic",
        "arimaSpecUI.enabledDesc.desc=Enables automatic modelling."
    })
    private EnhancedPropertyDescriptor enabledDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Enabled", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ENABLED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_enabledDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_enabledDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.accdefDesc.name=Accept Default",
        "arimaSpecUI.accdefDesc.desc=[fal] Controls whether the default model is acceptable."
    })
    private EnhancedPropertyDescriptor accdefDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("AcceptDefault", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ACCDEF_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_accdefDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_accdefDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.amiDesc.name=Compare to default",
        "arimaSpecUI.amiDesc.desc=[amicompare] Controls whether the final model is compared to the default model."
    })
    private EnhancedPropertyDescriptor amiDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("AmiCompare", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AMI_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_amiDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_amiDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.ub1Desc.name=Initial UR (Diff.)",
        "arimaSpecUI.ub1Desc.desc=[ub1] Unit root limit in the first step of the automatic differencing procedure [(2 0)(1 0) ARMA models]."
    })
    private EnhancedPropertyDescriptor ub1Desc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Ub1", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, UB1_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_ub1Desc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_ub1Desc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.ub2Desc.name=Final UR (Diff.)",
        "arimaSpecUI.ub2Desc.desc=[ub2] Unit root limit in the first step of the automatic differencing procedure [(1 1)(1 1) ARMA models]."
    })
    private EnhancedPropertyDescriptor ub2Desc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Ub2", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, UB2_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_ub2Desc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_ub2Desc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.cancelDesc.name=Cancelation limit",
        "arimaSpecUI.cancelDesc.desc=[cancel] Cancelation limit for AR and MA roots."
    })
    private EnhancedPropertyDescriptor cancelDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Cancel", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CANCEL_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_cancelDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_cancelDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.pcDesc.name=Reduce CV",
        "arimaSpecUI.pcDesc.desc=[pc] The percentage by which the outlier critical value will be reduced when an identified model is found to have a Ljung-Box Q statistic with an unacceptable confidence coefficient."
    })
    private EnhancedPropertyDescriptor pcDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("pc", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PC_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_pcDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_pcDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.tsigDesc.name=ArmaLimit",
        "arimaSpecUI.tsigDesc.desc=[tsig] Threshold value for t-statistics of ARMA coefficients and mean correction used for test of model parsimony."
    })
    private EnhancedPropertyDescriptor tsigDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("tsig", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TSIG_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_tsigDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_tsigDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.pcrDesc.name=LjungBox limit",
        "arimaSpecUI.pcrDesc.desc=[pcr] Ljung-Box Q statistic limit for the acceptance of a model."
    })
    private EnhancedPropertyDescriptor pcrDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("pcr", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PCR_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_pcrDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_pcrDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
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

    @Messages({
        "arimaSpecUI.pDesc.desc=[p] Regular auto-regresssive order"
    })
    private EnhancedPropertyDescriptor pDesc() {
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("P", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, P_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.arimaSpecUI_pDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.dDesc.desc=[d] Regular differencing order"
    })
    private EnhancedPropertyDescriptor dDesc() {
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("D", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, D_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.arimaSpecUI_dDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.qDesc.desc=[q] Regular moving average order"
    })
    private EnhancedPropertyDescriptor qDesc() {
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Q", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, Q_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.arimaSpecUI_qDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.bpDesc.desc=[bp] Seasonal auto-regressive order"
    })
    private EnhancedPropertyDescriptor bpDesc() {
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BP", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BP_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.arimaSpecUI_bpDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.bdDesc.desc=[bd] Seasonal differencing order"
    })
    private EnhancedPropertyDescriptor bdDesc() {
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BD", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.arimaSpecUI_bdDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.bqDesc.desc=[bq] Seasonal moving average order"
    })
    private EnhancedPropertyDescriptor bqDesc() {
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BQ", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BQ_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.arimaSpecUI_bqDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.phiDesc.name=phi",
        "arimaSpecUI.phiDesc.desc=[phi, jpr] Coefficients of the regular auto-regressive polynomial (true signs)"
    })
    private EnhancedPropertyDescriptor phiDesc() {
        if (core.isUsingAutoModel() || arima().getP() == 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Phi", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PHI_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_phiDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_phiDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.thetaDesc.name=theta",
        "arimaSpecUI.thetaDesc.desc=[th, jqr] Coefficients of the regular moving average polynomial (true signs)"
    })
    private EnhancedPropertyDescriptor thetaDesc() {
        if (core.isUsingAutoModel() || arima().getQ() == 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Theta", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, THETA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_thetaDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_thetaDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.bphiDesc.name=bphi",
        "arimaSpecUI.bphiDesc.desc=[bphi, jqr] Coefficients of the seasonal auto-regressive polynomial (true signs)"
    })
    private EnhancedPropertyDescriptor bphiDesc() {
        if (core.isUsingAutoModel() || arima().getBP() == 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BPhi", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BPHI_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_bphiDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_bphiDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.bthetaDesc.name=btheta",
        "arimaSpecUI.bthetaDesc.desc=[bth, jqs] Coefficients of the seasonal moving average polynomial (true signs)"
    })
    private EnhancedPropertyDescriptor bthetaDesc() {
        if (core.isUsingAutoModel() || arima().getBQ() == 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BTheta", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BTHETA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_bthetaDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_bthetaDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.meanDesc.desc=[imean] Mean correction"
    })
    private EnhancedPropertyDescriptor meanDesc() {
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Mean", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MEAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.arimaSpecUI_meanDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    private static final int ENABLED_ID = 0, ACCDEF_ID = 1, TD_ID = 2, AMI_ID = 3, UB1_ID = 4, UB2_ID = 5,
            CANCEL_ID = 6, TSIG_ID = 7, PCR_ID = 8, PC_ID = 9;
    private static final int P_ID = 10, D_ID = 11, Q_ID = 12, BP_ID = 13, BD_ID = 14, BQ_ID = 15,
            PHI_ID = 16, THETA_ID = 17, BPHI_ID = 18, BTHETA_ID = 19, MEAN_ID = 20;
}
