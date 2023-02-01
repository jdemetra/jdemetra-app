/*
 * Copyright 2023 National Bank of Belgium
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
package demetra.desktop.stl.ui;


import demetra.data.WeightFunction;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.stl.LoessSpec;
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
public class LoessSpecUI implements IPropertyDescriptors {

    @Override
    public String toString() {
        return "";
    }

    private LoessSpec loess;
    private final Consumer<LoessSpec> callback;
    private final boolean ro;

    public LoessSpecUI(LoessSpec loess, boolean ro, Consumer<LoessSpec> callback) {
        this.loess = loess;
        this.ro = ro;
        this.callback = callback;
    }

    public LoessSpec getCore() {
        return loess;
    }

    public int getDegree() {
        return loess.getDegree();
    }

    public void setDegree(int degree) {
        if (degree < 0 || degree > 1) {
            throw new IllegalArgumentException("Unsupported option. Should be 0 or 1");
        }
        loess=loess.toBuilder()
                .degree(degree)
                .build();
        callback.accept(loess);
    }

    public int getJump() {
        return loess.getJump();
    }

    public void setJump(int jump) {
        if (jump < 0) {
            throw new IllegalArgumentException("Should be gt 0");
        }
        loess=loess.toBuilder()
                .jump(jump)
                .build();
        callback.accept(loess);
    }

    public int getWindow() {
        return loess.getWindow();
    }

    public void setWindow(int window) {
        if (window <= 0 || window % 2 == 0) {
            throw new IllegalArgumentException("Should be odd");
        }
        loess=loess.toBuilder()
                .window(window)
                .build();
        callback.accept(loess);
    }

    public WeightFunction getWeights() {
        return loess.getLoessFunction();
    }

    public void setWeights(WeightFunction fn) {
        loess=loess.toBuilder()
                .loessFunction(fn)
                .build();
        callback.accept(loess);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = degreeDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = windowDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = jumpDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = weightDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @NbBundle.Messages({
        "loessSpecUI.degreeDesc.name=degree",
        "loessSpecUI.degreeDesc.desc=Degree of the local polynomial."
    })
    private EnhancedPropertyDescriptor degreeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Degree", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DEG_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.loessSpecUI_degreeDesc_name());
            desc.setShortDescription(Bundle.loessSpecUI_degreeDesc_desc());
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "loessSpecUI.windowDesc.name=window",
        "loessSpecUI.windowDesc.desc=Window of the local polynomial."
    })
    private EnhancedPropertyDescriptor windowDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Window", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, WIN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.loessSpecUI_windowDesc_name());
            desc.setShortDescription(Bundle.loessSpecUI_windowDesc_desc());
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "loessSpecUI.jumpDesc.name=jump",
        "loessSpecUI.jumpDesc.desc=Jump between two successive estimations."
    })
    private EnhancedPropertyDescriptor jumpDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Jump", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, JUMP_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.loessSpecUI_jumpDesc_name());
            desc.setShortDescription(Bundle.loessSpecUI_jumpDesc_desc());
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "loessSpecUI.weightDesc.name=weights",
        "loessSpecUI.weightDesc.desc=Weights of the local polynomial."
    })
    private EnhancedPropertyDescriptor weightDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Weights", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, WEIGHT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.loessSpecUI_weightDesc_name());
            desc.setShortDescription(Bundle.loessSpecUI_weightDesc_desc());
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private static final int DEG_ID = 1, WIN_ID = 2, JUMP_ID = 5, WEIGHT_ID = 6;

    @Override
    @NbBundle.Messages("loessSpecUI.getDisplayName=Loess")
    public String getDisplayName() {
        return Bundle.loessSpecUI_getDisplayName();
    }

}
