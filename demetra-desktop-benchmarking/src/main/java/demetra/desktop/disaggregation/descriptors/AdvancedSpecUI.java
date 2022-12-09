/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.disaggregation.descriptors;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.tempdisagg.univariate.TemporalDisaggregationSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean
 */
public class AdvancedSpecUI extends BaseTemporalDisaggregationSpecUI {

    public static final String DISPLAYNAME = "Advanced options";
    public static final String EPS_NAME = "Precision", KF_NAME = "Method", ML_NAME = "ML estimation",
            ZERO_NAME = "Zero initialization", TRUNCATED_NAME = "Truncated rho", DREGS_NAME = "Diffuse regression coefficients";
    public static final String EPS_DESC = "Precision", KF_DESC = "Kalman filter used for estimation", ML_DESC = "Use Maximum Likelihood or Minimum SSR",
            ZERO_DESC = "Zero initialization", TRUNCATED_DESC = "Lower bound for the estimated coefficient", DREGS_DESC = "Diffuse regression coefficients";
    public static final int EPS_ID = 0, KF_ID = 10, ML_ID = 15, ZERO_ID = 20, TRUNCATED_ID = 25, DREGS_ID = 30;

    @Override
    public String toString() {
        return "";
    }

    public AdvancedSpecUI(TemporalDisaggregationSpecRoot root) {
        super(root);
    }

    public double getEpsilon() {
        return core().getEstimationPrecision();
    }

    public void setEpsilon(double eps) {
        update(core()
                .toBuilder()
                .estimationPrecision(eps)
                .build());
    }

    public boolean isZeroInitialization() {
        return core().isZeroInitialization();
    }

    public void setZeroInitialization(boolean t) {

        TemporalDisaggregationSpec.Builder builder = core().toBuilder();
        builder.zeroInitialization(t);
        if (!core().getResidualsModel().isStationary() && !t) {
            builder.constant(false);
        }
        update(builder.build());
    }

    public double getTruncatedRho() {
        return core().getTruncatedParameter();
    }

    public void setTruncatedRho(double t) {
        update(core()
                .toBuilder()
                .truncatedParameter(t)
                .build());
    }

    public boolean isDiffuseRegression() {
        return core().isDiffuseRegressors();
    }

    public void setDiffuseRegression(boolean t) {
        update(core()
                .toBuilder()
                .diffuseRegressors(t)
                .build());
    }

    public boolean isML() {
        return core().isMaximumLikelihood();
    }

    public void setML(boolean t) {
        update(core()
                .toBuilder()
                .maximumLikelihood(t)
                .build());
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> props = new ArrayList<>();
        EnhancedPropertyDescriptor desc = epsDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = methodDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = mlDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = zeroDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = truncatedDesc();
        if (desc != null) {
            props.add(desc);
        }
        desc = dregsDesc();
        if (desc != null) {
            props.add(desc);
        }
        return props;
    }

    private EnhancedPropertyDescriptor epsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Epsilon", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, EPS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(EPS_NAME);
            desc.setShortDescription(EPS_DESC);
            edesc.setReadOnly(isRo() || !core().getResidualsModel().hasParameter());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor zeroDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ZeroInitialization", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ZERO_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(ZERO_NAME);
            desc.setShortDescription(ZERO_DESC);
            edesc.setReadOnly(isRo() || core().getResidualsModel() == TemporalDisaggregationSpec.Model.Wn
                    || core().getResidualsModel().getDifferencingOrder() > 1);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor truncatedDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TruncatedRho", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TRUNCATED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(TRUNCATED_NAME);
            desc.setShortDescription(TRUNCATED_DESC);
            edesc.setReadOnly(isRo() || (core().getResidualsModel() != TemporalDisaggregationSpec.Model.Ar1 || core().getParameter().isFixed()));
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor methodDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Method", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, KF_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(KF_NAME);
            desc.setShortDescription(KF_DESC);
            edesc.setReadOnly(isRo() || !core().getResidualsModel().hasParameter() || core().getResidualsModel().isStationary());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor mlDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ML", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ML_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(ML_NAME);
            desc.setShortDescription(ML_DESC);
            edesc.setReadOnly(isRo() || !core().getResidualsModel().hasParameter());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor dregsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("DiffuseRegression", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DREGS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(DREGS_NAME);
            desc.setShortDescription(DREGS_DESC);
            edesc.setReadOnly(isRo() || !core().getResidualsModel().hasParameter());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return DISPLAYNAME;
    }
}