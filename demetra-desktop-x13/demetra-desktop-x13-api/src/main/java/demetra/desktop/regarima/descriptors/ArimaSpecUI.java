/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.descriptors;

import demetra.data.Parameter;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.arima.SarimaSpec;
import demetra.regarima.AutoModelSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Kristof Bayens
 */
public class ArimaSpecUI extends BaseRegArimaSpecUI {

    public ArimaSpecUI(RegArimaSpecRoot spec) {
        super(spec);
    }

    private AutoModelSpec ami() {
        return core().getAutoModel();
    }

    private SarimaSpec arima() {
        return core().getArima();
    }

    @Override
    public String toString() {
        if (core().isUsingAutoModel()) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            SarimaSpec m = arima();
            builder.append('(').append(m.getP()).append(", ").append(m.getD()).append(", ").append(m.getQ()).append(")(")
                    .append(m.getBp()).append(", ").append(m.getBd()).append(", ").append(m.getBq()).append(')');
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
        desc = cancelDesc();
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
        desc = furlimitDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = mixedDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = balancedDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = armaDesc();
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
        desc = ljungboxDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = urlimitDesc();
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

    public int getP() {
        return arima().getP();
    }

    public void setP(int value) {
        update((arima().toBuilder().p(value)).build());
    }

    public int getD() {
        return arima().getD();
    }

    public void setD(int value) {
        update((arima().toBuilder().d(value)).build());
    }

    public int getQ() {
        return arima().getQ();
    }

    public void setQ(int value) {
        update((arima().toBuilder().q(value)).build());
    }

    public int getBp() {
         return arima().getBp();
    }

    public void setBp(int value) {
        update((arima().toBuilder().bp(value)).build());
    }

    public int getBd() {
        return arima().getBd();
    }

    public void setBd(int value) {
        update((arima().toBuilder().bd(value)).build());
    }

    public int getBq() {
        return arima().getBq();
    }

    public void setBq(int value) {
        update((arima().toBuilder().bq(value)).build());
    }

    public Parameter[] getPhi() {
        return arima().getPhi();
    }

    public void setPhi(Parameter[] value) {
        update((arima().toBuilder().phi(value)).build());
    }

    public Parameter[] getTheta() {
        return arima().getTheta();
    }

    public void setTheta(Parameter[] value) {
        update((arima().toBuilder().theta(value)).build());
    }

    public Parameter[] getBphi() {
        return arima().getBphi();
    }

    public void setBphi(Parameter[] value) {
        update((arima().toBuilder().bphi(value)).build());
    }

    public Parameter[] getBtheta() {
        return arima().getBtheta();
    }

    public void setBtheta(Parameter[] value) {
        update((arima().toBuilder().btheta(value)).build());
    }

    @Messages({
        "arimaSpecUI.pDesc.name=P",
        "arimaSpecUI.pDesc.desc=[p] Regular auto-regresssive order"
    })
    private EnhancedPropertyDescriptor pDesc() {
        if (core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("P", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, P_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_pDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_pDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.dDesc.name=D",
        "arimaSpecUI.dDesc.desc=[d] Regular differencing order",
    })
    private EnhancedPropertyDescriptor dDesc() {
        if (core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("D", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, D_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_dDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_dDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.qDesc.name=Q",
        "arimaSpecUI.qDesc.desc=[q] Regular moving average order"
    })
    private EnhancedPropertyDescriptor qDesc() {
        if (core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Q", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, Q_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_qDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_qDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.bpDesc.name=BP",
        "arimaSpecUI.bpDesc.desc=[bp] Seasonal auto-regressive order"
    })
    private EnhancedPropertyDescriptor bpDesc() {
        if (core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Bp", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BP_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_bpDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_bpDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.bdDesc.name=BD",
        "arimaSpecUI.bdDesc.desc=[bd] Seasonal differencing order"
    })
    private EnhancedPropertyDescriptor bdDesc() {
        if (core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Bd", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_bdDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_bdDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.bqDesc.name=BQ",
        "arimaSpecUI.bqDesc.desc=[bq] Seasonal moving average order"
    })
    private EnhancedPropertyDescriptor bqDesc() {
        if (core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Bq", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BQ_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_bqDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_bqDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.phiDesc.name=phi",
        "arimaSpecUI.phiDesc.desc=[phi, jpr] Coefficients of the regular auto-regressive polynomial (true signs)"
    })
    private EnhancedPropertyDescriptor phiDesc() {
        if (core().isUsingAutoModel() || arima().getP() == 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Phi", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PHI_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_phiDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_phiDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.thetaDesc.name=theta",
        "arimaSpecUI.thetaDesc.desc=[th, jqr] Coefficients of the regular moving average polynomial (true signs)"
    })
    private EnhancedPropertyDescriptor thetaDesc() {
        if (core().isUsingAutoModel() || arima().getQ() == 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Theta", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, THETA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_thetaDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_thetaDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.bphiDesc.name=bphi",
        "arimaSpecUI.bphiDesc.desc=[bphi, jqr] Coefficients of the seasonal auto-regressive polynomial (true signs)"
    })
    private EnhancedPropertyDescriptor bphiDesc() {
        if (core().isUsingAutoModel() || arima().getBp() == 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Bphi", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BPHI_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_bphiDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_bphiDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.bthetaDesc.name=btheta",
        "arimaSpecUI.bthetaDesc.desc=[bth, jqs] Coefficients of the seasonal moving average polynomial (true signs)"
    })
    private EnhancedPropertyDescriptor bthetaDesc() {
        if (core().isUsingAutoModel() || arima().getBq() == 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Btheta", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BTHETA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_bthetaDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_bthetaDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

//    @Messages({
//        "arimaSpecUI.meanDesc.name=Mean",
//        "arimaSpecUI.meanDesc.desc=[imean] Mean correction"
//    })
//    private EnhancedPropertyDescriptor meanDesc() {
//        if (core().isUsingAutoModel()) {
//            return null;
//        }
//        try {
//            PropertyDescriptor desc = new PropertyDescriptor("Mean", this.getClass());
//            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MEAN_ID);
//            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
//            desc.setDisplayName(Bundle.arimaSpecUI_meanDesc_name());
//            desc.setShortDescription(Bundle.arimaSpecUI_meanDesc_desc());
//            edesc.setReadOnly(isRo());
//            return edesc;
//        }
//        catch (IntrospectionException ex) {
//            return null;
//        }
//    }
//
    public boolean isEnabled() {
        return ami().isEnabled();
    }

    public void setEnabled(boolean value) {
        update(ami().toBuilder().enabled(value).build());
    }

    public boolean isAcceptDefault() {
        return ami().isAcceptDefault();
    }

    public void setAcceptDefault(boolean value) {
        update(ami().toBuilder().acceptDefault(value).build());
    }

    public boolean isBalanced() {
        return ami().isBalanced();
    }

    public void setBalanced(boolean value) {
        update(ami().toBuilder().balanced(value).build());
    }

    public boolean isMixed() {
        return ami().isMixed();
    }

    public void setMixed(boolean value) {
        update(ami().toBuilder().mixed(value).build());
    }

    public boolean isCheckMu() {
        return ami().isCheckMu();
    }

    public void setCheckMu(boolean value) {
        update(ami().toBuilder().checkMu(value).build());
    }

    public boolean isHannanRissannen() {
        return ami().isHannanRissannen();
    }

    public void setHannanRissannen(boolean value) {
        update(ami().toBuilder().hannanRissannen(value).build());
    }

    public double getUbFinal() {
        return ami().getUbfinal();
    }

    public void setUbFinal(double value) {
        update(ami().toBuilder().ubfinal(value).build());
    }

    public double getUb1() {
        return ami().getUb1();
    }

    public void setUb1(double value) {
        update(ami().toBuilder().ub1(value).build());
    }

    public double getUb2() {
        return ami().getUb2();
    }

    public void setUb2(double value) {
         update(ami().toBuilder().ub2(value).build());
   }

    public double getCancel() {
        return ami().getCancel();
    }

    public void setCancel(double value) {
        update(ami().toBuilder().cancel(value).build());
    }

    public double getLjungBoxLimit() {
        return ami().getLjungBoxLimit();
    }

    public void setLjungBoxLimit(double value) {
        update(ami().toBuilder().ljungBoxLimit(value).build());
    }

    public double getArmaSignificance() {
        return ami().getArmaSignificance();
    }

    public void setArmaSignificance(double value) {
        update(ami().toBuilder().armaSignificance(value).build());
    }

    public double getPredCV() {
        return ami().getPredcv();
    }

    public void setPredCV(double value) {
        update(ami().toBuilder().predcv(value).build());
    }

    public double getPercentRSE() {
        return ami().getPercentRSE();
    }

    public void setPercentRSE(double value) {
        update(ami().toBuilder().percentRSE(value).build());
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
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.accdefDesc.name=Accept Default",
        "arimaSpecUI.accdefDesc.desc=[acceptdefault] Controls whether the default model is acceptable."
    })
    private EnhancedPropertyDescriptor accdefDesc() {
        if (!core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("AcceptDefault", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ACCDEF_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_accdefDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_accdefDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.balancedDesc.name=Balanced",
        "arimaSpecUI.balancedDesc.desc=[balanced] Controls whether the automatic model procedure will have a preference for balanced models."
    })
    private EnhancedPropertyDescriptor balancedDesc() {
        if (!core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Balanced", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BALANCED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_balancedDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_balancedDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.mixedDesc.name=Mixed",
        "arimaSpecUI.mixedDesc.desc=[mixed] Controls whether ARIMA models with nonseasonal AR and MA terms or seasonal AR and MA terms will be considered in the automatic model identification procedure."
    })
    private EnhancedPropertyDescriptor mixedDesc() {
        if (!core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Mixed", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MIXED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_mixedDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_mixedDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.checkmuDesc.name=CheckMu",
        "arimaSpecUI.checkmuDesc.desc=[checkmu] Controls whether the automatic model selection procedure will check for the significance of a constant term."
    })
    private EnhancedPropertyDescriptor checkmuDesc() {
        if (!core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("CheckMu", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CHECKMU_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_checkmuDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_checkmuDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.hrinitialDesc.name=HR initial",
        "arimaSpecUI.hrinitialDesc.desc=[hrinitial] Controls whether Hannan-Rissanen estimation is done before exact maximum likelihood estimation to provide initial values."
    })
    private EnhancedPropertyDescriptor hrinitialDesc() {
        if (!core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("HannanRissannen", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, HRINITIAL_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_hrinitialDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_hrinitialDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.urlimitDesc.name=Unit root limit",
        "arimaSpecUI.urlimitDesc.desc=[urfinal] Unit root limit for final model. Should be > 1."
    })
    private EnhancedPropertyDescriptor urlimitDesc() {
        if (!core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("UbFinal", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, URLIMIT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_urlimitDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_urlimitDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.iurlimitDesc.name=Initial UR (Diff.)",
        "arimaSpecUI.iurlimitDesc.desc=Initial unit root limit in the automatic differencing procedure."
    })
    private EnhancedPropertyDescriptor iurlimitDesc() {
        if (!core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Ub1", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, IURLIMIT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_iurlimitDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_iurlimitDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.furlimitDesc.name=Final UR (Diff.)",
        "arimaSpecUI.furlimitDesc.desc=Final unit root limit in the automatic differencing procedure."
    })
    private EnhancedPropertyDescriptor furlimitDesc() {
        if (!core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Ub2", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FURLIMIT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_furlimitDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_furlimitDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.cancelDesc.name=Cancelation limit",
        "arimaSpecUI.cancelDesc.desc=Cancelation limit for AR and MA roots."
    })
    private EnhancedPropertyDescriptor cancelDesc() {
        if (!core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Cancel", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CANCELLIMIT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_cancelDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_cancelDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.ljungboxDesc.name=LjungBox limit",
        "arimaSpecUI.ljungboxDesc.desc=[ljungboxlimit] Ljung-Box Q statistic limit for the acceptance of a model."
    })
    private EnhancedPropertyDescriptor ljungboxDesc() {
        if (!core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LjungBoxLimit", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LJUNGBOXLIMIT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_ljungboxDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_ljungboxDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.armaDesc.name=ArmaLimit",
        "arimaSpecUI.armaDesc.desc=[armalimit] Threshold value for t-statistics of ARMA coefficients used for final test of model parsimony."
    })
    private EnhancedPropertyDescriptor armaDesc() {
        if (!core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Pcr", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ARMALIMIT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_armaDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_armaDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.reducecvDesc.name=Reduce CV",
        "arimaSpecUI.reducecvDesc.desc=[reducecv] The percentage by which the outlier critical value will be reduced when an identified model is found to have a Ljung-Box Q statistic with an unacceptable confidence coefficient."
    })
    private EnhancedPropertyDescriptor reducecvDesc() {
        if (!core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("PredCV", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REDUCECV_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_reducecvDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_reducecvDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "arimaSpecUI.reduceseDesc.name=Reduce SE",
        "arimaSpecUI.reduceseDesc.desc=Percent reduction of SE."
    })
    private EnhancedPropertyDescriptor reduceseDesc() {
        if (!core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("PrecentRSE", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REDUCESE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.arimaSpecUI_reduceseDesc_name());
            desc.setShortDescription(Bundle.arimaSpecUI_reduceseDesc_desc());
            edesc.setReadOnly(isRo());
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
}
