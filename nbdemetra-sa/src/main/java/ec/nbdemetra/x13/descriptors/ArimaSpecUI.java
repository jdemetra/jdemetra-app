/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.tstoolkit.Parameter;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.arima.x13.ArimaSpec;
import ec.tstoolkit.modelling.arima.x13.AutoModelSpec;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kristof Bayens
 */
public class ArimaSpecUI extends BaseRegArimaSpecUI {

    ArimaSpecUI(RegArimaSpecification spec, boolean ro) {
        super(spec, ro);
    }

    private ArimaSpec arima() {
        return core.getArima();
    }

    private AutoModelSpec ami() {
        return core.getAutoModel();
    }

    @Override
    public String toString() {
        if (core == null) {
            return "";
        }
        else {
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
//        desc = checkmuDesc();
//        if (desc != null) {
//            descs.add(desc);
//        }
        desc = ljungboxDesc();
        if (desc != null) {
            descs.add(desc);
        }
//        desc = mixedDesc();
//        if (desc != null) {
//            descs.add(desc);
//        }
        desc = armaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = balancedDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = cancelDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = furlimitDesc();
        if (desc != null) {
            descs.add(desc);
        }
//        desc = hrinitialDesc();
//        if (desc != null) {
//            descs.add(desc);
//        }
        desc = iurlimitDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = reducecvDesc();
        if (desc != null) {
            descs.add(desc);
        }
//        desc = reduceseDesc();
//        if (desc != null) {
//            descs.add(desc);
//        }
        desc = urlimitDesc();
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
        ArimaSpec spec = arima();
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
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("P", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, P_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(P_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor dDesc() {
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("D", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, D_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(D_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor qDesc() {
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Q", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, Q_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Q_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor bpDesc() {
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BP", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BP_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(BP_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor bdDesc() {
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BD", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(BD_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor bqDesc() {
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BQ", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BQ_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(BQ_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor phiDesc() {
        if (core.isUsingAutoModel() || arima().getP() == 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Phi", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PHI_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName("phi");
            desc.setShortDescription(PHI_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor thetaDesc() {
        if (core.isUsingAutoModel() || arima().getQ() == 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Theta", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, THETA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName("theta");
            desc.setShortDescription(THETA_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor bphiDesc() {
        if (core.isUsingAutoModel() || arima().getBP() == 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BPhi", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BPHI_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName("bphi");
            desc.setShortDescription(BPHI_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor bthetaDesc() {
        if (core.isUsingAutoModel() || arima().getBQ() == 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BTheta", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BTHETA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName("btheta");
            desc.setShortDescription(BTHETA_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor meanDesc() {
        if (core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Mean", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MEAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(MEAN_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isEnabled() {
        return ami().isEnabled();
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

    public boolean isBalanced() {
        return ami().isBalanced();
    }

    public void setBalanced(boolean value) {
        ami().setBalanced(value);
    }

    public boolean isMixed() {
        return ami().isMixed();
    }

    public void setMixed(boolean value) {
        ami().setMixed(value);
    }

    public boolean isCheckMu() {
        return ami().isCheckMu();
    }

    public void setCheckMu(boolean value) {
        ami().setCheckMu(value);
    }

    public boolean isHannanRissannen() {
        return ami().isHannanRissannen();
    }

    public void setHannanRissannen(boolean value) {
        ami().setHannanRissanen(value);
    }

    public double getUbFinal() {
        return ami().getUnitRootLimit();
    }

    public void setUbFinal(double value) {
        ami().setUnitRootLimit(value);
    }

    public double getUb1() {
        return ami().getInitialUnitRootLimit();
    }

    public void setUb1(double value) {
        ami().setInitialUnitRootLimit(value);
    }

    public double getUb2() {
        return ami().getFinalUnitRootLimit();
    }

    public void setUb2(double value) {
        ami().setFinalUnitRootLimit(value);
    }

    public double getCancel() {
        return ami().getCancelationLimit();
    }

    public void setCancel(double value) {
        ami().setCancelationLimit(value);
    }

    public double getPcr() {
        return ami().getLjungBoxLimit();
    }

    public void setPcr(double value) {
        ami().setLjungBoxLimit(value);
    }

    public double getTsig() {
        return ami().getArmaSignificance();
    }

    public void setTsig(double value) {
        ami().setArmaSignificance(value);
    }

    public double getPredCV() {
        return ami().getPercentReductionCV();
    }

    public void setPredCV(double value) {
        ami().setPercentReductionCV(value);
    }

    public double getPredSE() {
        return ami().getPercentRSE();
    }

    public void setPredSE(double value) {
        ami().setPercentRSE(value);
    }

    private EnhancedPropertyDescriptor enabledDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Enabled", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ENABLED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(ENABLED_NAME);
            desc.setShortDescription(ENABLED_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor accdefDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("AcceptDefault", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ACCDEF_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(ACCDEF_NAME);
            desc.setShortDescription(ACCDEF_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor balancedDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Balanced", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BALANCED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(BALANCED_NAME);
            desc.setShortDescription(BALANCED_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor mixedDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Mixed", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MIXED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(MIXED_NAME);
            desc.setShortDescription(MIXED_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor checkmuDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("CheckMu", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CHECKMU_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(CHECKMU_NAME);
            desc.setShortDescription(CHECKMU_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor hrinitialDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("HannanRissannen", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, HRINITIAL_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(HRINITIAL_NAME);
            desc.setShortDescription(HRINITIAL_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor urlimitDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("UbFinal", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, URLIMIT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(URLIMIT_NAME);
            desc.setShortDescription(URLIMIT_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor iurlimitDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Ub1", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, IURLIMIT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(IURLIMIT_NAME);
            desc.setShortDescription(IURLIMIT_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor furlimitDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Ub2", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FURLIMIT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(FURLIMIT_NAME);
            desc.setShortDescription(FURLIMIT_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor cancelDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Cancel", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CANCELLIMIT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(CANCELLIMIT_NAME);
            desc.setShortDescription(CANCELLIMIT_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor ljungboxDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Pcr", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LJUNGBOXLIMIT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(LJUNGBOXLIMIT_NAME);
            desc.setShortDescription(LJUNGBOXLIMIT_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor armaDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Tsig", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ARMALIMIT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(ARMALIMIT_NAME);
            desc.setShortDescription(ARMALIMIT_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor reducecvDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("PredCV", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REDUCECV_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(REDUCECV_NAME);
            desc.setShortDescription(REDUCECV_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor reduceseDesc() {
        if (!core.isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("PredSE", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REDUCESE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(REDUCESE_NAME);
            desc.setShortDescription(REDUCESE_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }
    private static final int P_ID = 20, D_ID = 21, Q_ID = 22, BP_ID = 23, BD_ID = 24, BQ_ID = 25,
            PHI_ID = 26, THETA_ID = 27, BPHI_ID = 28, BTHETA_ID = 29, MEAN_ID = 30;
    private static final int ENABLED_ID = 0, ACCDEF_ID = 1, BALANCED_ID = 2, MIXED_ID = 3,
            CHECKMU_ID = 4, HRINITIAL_ID = 5, URLIMIT_ID = 6, IURLIMIT_ID = 7, FURLIMIT_ID = 8,
            CANCELLIMIT_ID = 9, LJUNGBOXLIMIT_ID = 10, ARMALIMIT_ID = 11, REDUCECV_ID = 12,
            REDUCESE_ID = 13;
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
    private static final String ENABLED_NAME = "Automatic",
            ACCDEF_NAME = "Accept Default",
            BALANCED_NAME = "Balanced",
            MIXED_NAME = "Mixed",
            CHECKMU_NAME = "CheckMu",
            HRINITIAL_NAME = "HR initial",
            URLIMIT_NAME = "Unit root limit",
            IURLIMIT_NAME = "Initial unit root limit",
            FURLIMIT_NAME = "Final unit root limit",
            CANCELLIMIT_NAME = "Cancelation limit",
            LJUNGBOXLIMIT_NAME = "LjungBox limit",
            ARMALIMIT_NAME = "ArmaLimit",
            REDUCECV_NAME = "Reduce CV",
            REDUCESE_NAME = "Reduce SE";
    private static final String ENABLED_DESC = "Enables automatic modelling.",
            ACCDEF_DESC = "[acceptdefault] Controls whether the default model is acceptable.",
            BALANCED_DESC = "[balanced] Controls whether the automatic model procedure will have a preference for balanced models.",
            MIXED_DESC = "[mixed] Controls whether ARIMA models with nonseasonal AR and MA terms or seasonal AR and MA terms will be considered in the automatic model identification procedure.",
            CHECKMU_DESC = "[checkmu] Controls whether the automatic model selection procedure will check for the significance of a constant term.",
            HRINITIAL_DESC = "[hrinitial] Controls whether Hannan-Rissanen estimation is done before exact maximum likelihood estimation to provide initial values.",
            URLIMIT_DESC = "[urfinal] Unit root limit for final model. Should be > 1.",
            IURLIMIT_DESC = "Initial unit root limit in the automatic differencing procedure.",
            FURLIMIT_DESC = "Final unit root limit in the automatic differencing procedure.",
            CANCELLIMIT_DESC = "Cancelation limit for AR and MA roots.",
            LJUNGBOXLIMIT_DESC = "[ljungboxlimit] Ljung-Box Q statistic limit for the acceptance of a model.",
            ARMALIMIT_DESC = "[armalimit] Threshold value for t-statistics of ARMA coefficients used for final test of model parsimony.",
            REDUCECV_DESC = "[reducecv] The percentage by which the outlier critical value will be reduced when an identified model is found to have a Ljung-Box Q statistic with an unacceptable confidence coefficient.",
            REDUCESE_DESC = "Percent reduction of SE.";
}
