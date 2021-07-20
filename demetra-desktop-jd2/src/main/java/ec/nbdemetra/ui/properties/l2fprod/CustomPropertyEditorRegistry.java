package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import ec.satoolkit.x11.SeasonalFilterOption;
import ec.satoolkit.x11.SigmavecOption;
import ec.tstoolkit.Parameter;
import ec.tstoolkit.modelling.TsVariableDescriptor;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.regression.InterventionVariable;
import ec.tstoolkit.timeseries.regression.OutlierDefinition;
import ec.tstoolkit.timeseries.regression.OutlierType;
import ec.tstoolkit.timeseries.regression.Ramp;
import ec.tstoolkit.timeseries.regression.Sequence;
import ec.tstoolkit.utilities.Directory;
import java.beans.PropertyEditor;

/**
 *
 * @author Demortier Jeremy
 */
public enum CustomPropertyEditorRegistry {

    INSTANCE;
    private final PropertyEditorRegistry m_registry;

    private CustomPropertyEditorRegistry() {
        m_registry = PropertyEditorRegistry.INSTANCE;

        m_registry.registerEditor(Enum.class, EnumPropertyEditor.class);
        m_registry.registerEditor(double.class, DoublePropertyEditor.class);
        m_registry.registerEditor(Double.class, DoublePropertyEditor.class);

        //registerCompositeEditor(SeasonalFilterOption[].class);
        register(Directory.class, new DirectoryEditor());
        register(Day.class, new JDayPropertyEditor());
        register(Parameter[].class, new ParametersPropertyEditor());
        register(SeasonalFilterOption[].class, new SeasonalFilterPropertyEditor());
        register(SigmavecOption[].class, new SigmavecPropertyEditor());
        register(Ramp[].class, new RampsEditor());
        register(TsVariableDescriptor[].class, new TsVariableDescriptorsEditor());
        register(InterventionVariable[].class, new InterventionVariablesEditor());
        register(Sequence[].class, new SequencesEditor());
        register(OutlierDefinition[].class, new OutlierDefinitionsEditor());
        register(String[].class, new StringCollectionEditor());
        register(Holidays.class, new HolidaysSelector());
        register(UserVariable.class, new UserVariableSelector());
        register(UserVariables.class, new UserVariablesEditor());
        register(Coefficients.class, new FixedCoefficientsEditor());

        registerEnumEditor(OutlierType.class, new OutlierTypeSelector());
    }

    public PropertyEditorRegistry getRegistry() {
        return m_registry;
    }

    /**
     * Used to register a custom enum editor. Then, the default
     * EnumPropertyEditor is not used for the given property type.
     *
     * @param type Type of the property
     * @param editor New editor for the property
     */
    public void registerEnumEditor(Class<? extends Enum<?>> type, PropertyEditor editor) {
        m_registry.registerEditor(type, editor);
    }

    public void registerCompositeEditor(Class type) {
        if (m_registry.getEditor(type) == null && type.isArray() && type.getComponentType().isEnum()) {
            MultiEnumPropertyEditor editor = new MultiEnumPropertyEditor(type.getComponentType());
            m_registry.registerEditor(type, editor);
        }
    }

    public void register(Class c, PropertyEditor editor) {
        if (null == m_registry.getEditor(c)) {
            m_registry.registerEditor(c, editor);
        }
    }

    public void unregister(Class c) {
        if (null != m_registry.getEditor(c)) {
            m_registry.unregisterEditor(c);
        }
    }
}
