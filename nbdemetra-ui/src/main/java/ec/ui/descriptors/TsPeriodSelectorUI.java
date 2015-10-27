/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.descriptors;

import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.PeriodSelectorType;
import ec.tstoolkit.timeseries.TsPeriodSelector;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class TsPeriodSelectorUI implements IPropertyDescriptors {

    static {
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(Type.class);
    }

    public static enum Type {

        All,
        From,
        To,
        Between,
        Last,
        First,
        Excluding;

        public static Type of(PeriodSelectorType type) {
            switch (type) {
                case All:
                    return All;
                case From:
                    return From;
                case To:
                    return To;
                case Between:
                    return Between;
                case Last:
                    return Last;
                case First:
                    return First;
                case Excluding:
                    return Excluding;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public static PeriodSelectorType to(Type type) {
            switch (type) {
                case All:
                    return PeriodSelectorType.All;
                case From:
                    return PeriodSelectorType.From;
                case To:
                    return PeriodSelectorType.To;
                case Between:
                    return PeriodSelectorType.Between;
                case Last:
                    return PeriodSelectorType.Last;
                case First:
                    return PeriodSelectorType.First;
                case Excluding:
                    return PeriodSelectorType.Excluding;
                default:
                    return PeriodSelectorType.None;
            }
        }
    }

    @Override
    public String toString() {
        return core_.toString();
    }
    private final TsPeriodSelector core_;
    private final TsDomain domain_;
    private final boolean ro_;

    public TsPeriodSelectorUI(TsPeriodSelector sel, boolean ro) {
        core_ = sel;
        ro_ = ro;
        domain_ = null;
    }

    public TsPeriodSelectorUI(TsPeriodSelector sel, TsDomain domain, boolean ro) {
        core_ = sel;
        ro_ = ro;
        domain_ = domain;
    }

    public TsPeriodSelector getCore() {
        return core_;
    }

    public Type getType() {
        return Type.of(core_.getType());
    }

    public void setType(Type value) {
        core_.setType(Type.to(value));
    }

    public Day getStart() {
        if (core_.getD0().equals(TsPeriodSelector.DEF_BEG) && domain_ != null) {
            return domain_.getStart().firstday();
        }
        return core_.getD0();
    }

    public void setStart(Day day) {
        core_.setD0(day);
    }

    public Day getEnd() {
        if (core_.getD1().equals(TsPeriodSelector.DEF_END) && domain_ != null) {
            return domain_.getLast().lastday();
        }
        return core_.getD1();
    }

    public void setEnd(Day day) {
        core_.setD1(day);
    }

    public int getFirst() {
        return core_.getN0();
    }

    public void setFirst(int n) {
        core_.setN0(n);
    }

    public int getLast() {
        return core_.getN1();
    }

    public void setLast(int n) {
        core_.setN1(n);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = typeDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = startDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = endDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = firstDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = lastDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Span";
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int TYPE_ID = 1, D0_ID = 2, D1_ID = 3, N0_ID = 4, N1_ID = 5;

    private EnhancedPropertyDescriptor typeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("type", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor startDesc() {
        PeriodSelectorType type = core_.getType();
        if (type != PeriodSelectorType.Between && type != PeriodSelectorType.From) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("start", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor endDesc() {
        PeriodSelectorType type = core_.getType();
        if (type != PeriodSelectorType.Between && type != PeriodSelectorType.To) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("end", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor firstDesc() {
        PeriodSelectorType type = core_.getType();
        if (type != PeriodSelectorType.Excluding && type != PeriodSelectorType.First) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("first", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor lastDesc() {
        PeriodSelectorType type = core_.getType();
        if (type != PeriodSelectorType.Excluding && type != PeriodSelectorType.Last) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("last", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
