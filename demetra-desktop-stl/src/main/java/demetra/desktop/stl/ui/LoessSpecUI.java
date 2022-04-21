/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.stl.ui;

import demetra.data.WeightFunction;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.stl.LoessSpecification;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
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

    private LoessSpecification loess;
    private final Consumer<LoessSpecification> callback;
    private final boolean ro;

    public LoessSpecUI(LoessSpecification loess, boolean ro, Consumer<LoessSpecification> callback) {
        this.loess = loess;
        this.ro = ro;
        this.callback = callback;
    }

    public LoessSpecification getCore() {
        return loess;
    }

    public int getDegree() {
        return loess.getDegree();
    }

    public void setDegree(int degree) {
        if (degree < 0 || degree > 1) {
            throw new IllegalArgumentException("Unsupported option. Should be 0 or 1");
        }
        loess.toBuilder()
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
        loess.toBuilder()
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
        loess.toBuilder()
                .window(window)
                .build();
        callback.accept(loess);
    }

    public WeightFunction getWeights() {
        return loess.getLoessFunction();
    }

    public void setWeights(WeightFunction fn) {
        loess.toBuilder()
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
        "loessSpecUI.windowDesc.name=degree",
        "loessSpecUI.windowDesc.desc=Degree of the local polynomial."
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
        "loessSpecUI.jumpDesc.name=degree",
        "loessSpecUI.jumpDesc.desc=Degree of the local polynomial."
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
        "loessSpecUI.weightDesc.name=degree",
        "loessSpecUI.weightDesc.desc=Degree of the local polynomial."
    })
    private EnhancedPropertyDescriptor weightDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Degree", this.getClass());
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
    @NbBundle.Messages("decompositionSpecUI.getDisplayName=Outliers")
    public String getDisplayName() {
        return Bundle.decompositionSpecUI_getDisplayName();
    }

}
