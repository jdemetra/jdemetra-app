package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import ec.satoolkit.DecompositionMode;
import ec.satoolkit.benchmarking.SaBenchmarkingSpec.Target;
import ec.satoolkit.x11.CalendarSigma;
import ec.satoolkit.x11.SeasonalFilterOption;
import ec.satoolkit.x11.SigmavecOption;
import ec.tss.sa.output.CsvLayout;
import ec.tstoolkit.Parameter;
import ec.tstoolkit.modelling.ComponentType;
import ec.tstoolkit.modelling.DefaultTransformationType;
import ec.tstoolkit.modelling.RegressionTestSpec;
import ec.tstoolkit.modelling.TradingDaysSpecType;
import ec.tstoolkit.modelling.TsVariableDescriptor;
import ec.tstoolkit.modelling.TsVariableDescriptor.UserComponentType;
import ec.tstoolkit.modelling.arima.Method;
import ec.tstoolkit.modelling.arima.tramo.EasterSpec;
import ec.tstoolkit.modelling.arima.tramo.TramoChoice;
import ec.tstoolkit.structural.ComponentUse;
import ec.tstoolkit.structural.SeasonalModel;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.PeriodSelectorType;
import ec.tstoolkit.timeseries.calendars.LengthOfPeriodType;
import ec.tstoolkit.timeseries.calendars.TradingDaysType;
import ec.tstoolkit.timeseries.regression.InterventionVariable;
import ec.tstoolkit.timeseries.regression.OutlierDefinition;
import ec.tstoolkit.timeseries.regression.OutlierType;
import ec.tstoolkit.timeseries.regression.Ramp;
import ec.tstoolkit.timeseries.regression.Sequence;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.utilities.Directory;
import ec.ui.interfaces.ITsCollectionView.TsUpdateMode;
import ec.ui.interfaces.ITsControl.TooltipType;
import ec.ui.interfaces.ITsGrid;
import ec.ui.interfaces.ITsList;
import java.beans.PropertyEditor;

/**
 *
 * @author Demortier Jeremy
 */
public enum CustomPropertyEditorRegistry {

    INSTANCE;
    private final PropertyEditorRegistry m_registry;

    private CustomPropertyEditorRegistry() {
        m_registry = PropertyEditorRegistry.Instance;

        registerEnumEditor(ITsGrid.Chronology.class);
        registerEnumEditor(ITsGrid.Orientation.class);
        registerEnumEditor(ITsGrid.Mode.class);
        registerEnumEditor(ITsList.InfoType.class);
        registerEnumEditor(TooltipType.class);
        registerEnumEditor(TsUpdateMode.class);
        registerEnumEditor(ComponentType.class);
        registerEnumEditor(UserComponentType.class);
        registerEnumEditor(PeriodSelectorType.class);
        registerEnumEditor(DefaultTransformationType.class);
        registerEnumEditor(TradingDaysType.class);
        registerEnumEditor(TramoChoice.class);
        registerEnumEditor(TradingDaysSpecType.class);
        registerEnumEditor(LengthOfPeriodType.class);
        registerEnumEditor(RegressionTestSpec.class);
        registerEnumEditor(SeasonalFilterOption.class);
        registerEnumEditor(ComponentUse.class);
        registerEnumEditor(SeasonalModel.class);
        registerEnumEditor(Method.class);
        registerEnumEditor(DecompositionMode.class);
        registerEnumEditor(CalendarSigma.class);
        registerEnumEditor(CsvLayout.class);
        registerEnumEditor(Target.class);
        registerEnumEditor(EasterSpec.Type.class);
        registerEnumEditor(TsFrequency.class);
        //registerEnumEditor(SpreadsheetLayout.class);
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
        register(OutlierType.class, new OutlierTypeSelector());
        register(String[].class, new StringCollectionEditor());
        register(Holidays.class, new HolidaysSelector());
        register(UserVariable.class, new UserVariableSelector());
        register(UserVariables.class, new UserVariablesEditor());
    }

    public PropertyEditorRegistry getRegistry() {
        return m_registry;
    }

    public void registerEnumEditor(Class<? extends Enum<?>> type) {
        if (m_registry.getEditor(type) == null) {
            Enum<?>[] enumConstants = type.getEnumConstants();
            ComboBoxPropertyEditor.Value[] values = new ComboBoxPropertyEditor.Value[enumConstants.length];
            for (int i = 0; i < values.length; i++) {
                values[i] = new ComboBoxPropertyEditor.Value(enumConstants[i], enumConstants[i].name());
            }

            ComboBoxPropertyEditor editor = new ComboBoxPropertyEditor();
            editor.setAvailableValues(values);
            m_registry.registerEditor(type, editor);
        }
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
