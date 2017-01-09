package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import ec.satoolkit.x11.SeasonalFilterOption;
import ec.satoolkit.x11.SigmavecOption;
import ec.tstoolkit.Parameter;
import ec.tstoolkit.modelling.TsVariableDescriptor;
import ec.tstoolkit.timeseries.regression.InterventionVariable;
import ec.tstoolkit.timeseries.regression.OutlierDefinition;
import ec.tstoolkit.timeseries.regression.Ramp;
import ec.tstoolkit.timeseries.regression.Sequence;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Demortier Jeremy
 */
public enum CustomPropertyRendererFactory {

    INSTANCE;
    private PropertyRendererRegistry registry_;

    private CustomPropertyRendererFactory() {
        registry_ = new PropertyRendererRegistry();
        registry_.registerRenderer(Parameter[].class, new ArrayRenderer());
        registry_.registerRenderer(Ramp[].class, new ArrayRenderer());
        registry_.registerRenderer(TsVariableDescriptor[].class, new ArrayRenderer());
        registry_.registerRenderer(InterventionVariable[].class, new ArrayRenderer());
        registry_.registerRenderer(Sequence[].class, new ArrayRenderer());
        registry_.registerRenderer(OutlierDefinition[].class, new ArrayRenderer());
        registry_.registerRenderer(SeasonalFilterOption[].class, new ArrayRenderer());
        registry_.registerRenderer(SigmavecOption[].class, new ArrayRenderer());
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
