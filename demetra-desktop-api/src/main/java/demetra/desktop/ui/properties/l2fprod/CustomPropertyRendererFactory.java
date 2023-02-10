package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import demetra.data.Parameter;

/**
 *
 * @author Demortier Jeremy
 */
public enum CustomPropertyRendererFactory {

    INSTANCE;
    private final PropertyRendererRegistry registry_;

    CustomPropertyRendererFactory() {
        registry_ = new PropertyRendererRegistry();
        registry_.registerRenderer(Parameter.class, new ParameterRenderer());
        registry_.registerRenderer(Parameter[].class, new ArrayRenderer());
        registry_.registerRenderer(RampDescriptor[].class, new ArrayRenderer());
        registry_.registerRenderer(TsVariableDescriptor[].class, new ArrayRenderer());
        registry_.registerRenderer(InterventionVariableDescriptor[].class, new ArrayRenderer());
        registry_.registerRenderer(Sequence[].class, new ArrayRenderer());
        registry_.registerRenderer(OutlierDescriptor[].class, new ArrayRenderer());
        registry_.registerRenderer(HighFreqOutlierDescriptor[].class, new ArrayRenderer());
        registry_.registerRenderer(Coefficients.class, new ArrayRenderer());
    }

    public PropertyRendererRegistry getRegistry() {
        return registry_;
    }
}
