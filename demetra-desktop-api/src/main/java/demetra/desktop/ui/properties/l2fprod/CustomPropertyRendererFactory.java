package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Demortier Jeremy
 */
public enum CustomPropertyRendererFactory {

    INSTANCE;
    private final PropertyRendererRegistry registry_;

    CustomPropertyRendererFactory() {
        registry_ = new PropertyRendererRegistry();
        registry_.registerRenderer(Parameter[].class, new ArrayRenderer());
        registry_.registerRenderer(Ramp[].class, new ArrayRenderer());
        registry_.registerRenderer(TsVariableDescriptor[].class, new ArrayRenderer());
        registry_.registerRenderer(InterventionVariable[].class, new ArrayRenderer());
        registry_.registerRenderer(Sequence[].class, new ArrayRenderer());
        registry_.registerRenderer(OutlierDefinition[].class, new ArrayRenderer());
        registry_.registerRenderer(SeasonalFilterOption[].class, new ArrayRenderer());
        registry_.registerRenderer(SigmavecOption[].class, new ArrayRenderer());
        registry_.registerRenderer(Coefficients.class, new ArrayRenderer());
    }

    public PropertyRendererRegistry getRegistry() {
        return registry_;
    }
}

class ArrayRenderer extends DefaultTableCellRenderer {

    public ArrayRenderer() {
        super();
    }

    @Override
    protected void setValue(Object value) {
        //super.setValue(value);
        setText("");
    }

}
