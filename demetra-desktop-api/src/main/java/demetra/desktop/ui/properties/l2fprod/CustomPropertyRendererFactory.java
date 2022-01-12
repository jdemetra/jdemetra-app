package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import demetra.data.Parameter;
import demetra.timeseries.regression.InterventionVariable;
import demetra.timeseries.regression.Ramp;
import demetra.timeseries.regression.TsContextVariable;
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
        registry_.registerRenderer(TsContextVariable[].class, new ArrayRenderer());
        registry_.registerRenderer(InterventionVariable[].class, new ArrayRenderer());
        registry_.registerRenderer(Sequence[].class, new ArrayRenderer());
        registry_.registerRenderer(OutlierDefinition[].class, new ArrayRenderer());
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
