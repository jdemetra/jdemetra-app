/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.descriptors;

import demetra.timeseries.TimeSelector;
import demetra.timeseries.TsDomain;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.openide.util.NbBundle;

/**
 *
 * @author Jean Palate
 */
public class DateSelectorUI implements IPropertyDescriptors {    

    public enum Type {

        All,
        From,
        To,
        Between,
        Last,
        First,
        Excluding;

        public static Type of(TimeSelector.SelectionType type) {
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

        public static TimeSelector.SelectionType to(Type type) {
            switch (type) {
                case All:
                    return TimeSelector.SelectionType.All;
                case From:
                    return TimeSelector.SelectionType.From;
                case To:
                    return TimeSelector.SelectionType.To;
                case Between:
                    return TimeSelector.SelectionType.Between;
                case Last:
                    return TimeSelector.SelectionType.Last;
                case First:
                    return TimeSelector.SelectionType.First;
                case Excluding:
                    return TimeSelector.SelectionType.Excluding;
                default:
                    return TimeSelector.SelectionType.None;
            }
        }
    }

    @Override
    public String toString() {
        return core.toDateString();
    }
    private TimeSelector core;
    private final TsDomain domain;
    private final boolean ro_;
    private final Consumer<TimeSelector> callback;

    public DateSelectorUI(TimeSelector sel, boolean ro, Consumer<TimeSelector> callback) {
        core = sel;
        ro_ = ro;
        domain = null;
        this.callback=callback;
    }

    public DateSelectorUI(TimeSelector sel, TsDomain domain, boolean ro, Consumer<TimeSelector> callback) {
        core = sel;
        ro_ = ro;
        this.domain = domain;
        this.callback=callback;
    }

    public TimeSelector getCore() {
        return core;
    }

    public Type getType() {
        return Type.of(core.getType());
    }

    public void setType(Type value) {
        core=core
                .toBuilder()
                .type(Type.to(value))
                .build();
        callback.accept(core);
    }

    public LocalDate getStart() {
        if (core.getD0().equals(LocalDateTime.MIN)) {
            if (domain != null)
               return domain.getStartPeriod().start().toLocalDate();
            else
                return LocalDate.now();
        }
        return core.getD0().toLocalDate();
    }

    public void setStart(LocalDate day) {
        core=core.toBuilder()
                .d0(day.atStartOfDay())
                .build();
        callback.accept(core);
    }

    public LocalDate getEnd() {
        if (core.getD1().equals(LocalDateTime.MAX)){
            if (domain != null){
                return domain.getLastPeriod().end().toLocalDate();
            }else{ 
                return LocalDate.now();
            }
        }
        return core.getD1().toLocalDate();
    }

    public void setEnd(LocalDate day) {
        core=core.toBuilder()
                .d1(day.atStartOfDay())
                .build();
        callback.accept(core);
    }

    public int getFirst() {
        return core.getN0();
    }

    public void setFirst(int n) {
        core=core.toBuilder().n0(n).build();
        callback.accept(core);
    }

    public int getLast() {
        return core.getN1();
    }

    public void setLast(int n) {
        core=core.toBuilder().n1(n).build();
        callback.accept(core);
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

    @NbBundle.Messages({
        "tsPeriodSelectorUI.typeDesc.name=Type",
        "tsPeriodSelectorUI.typeDesc.desc=Specify the way the time span is defined"
    })
    private EnhancedPropertyDescriptor typeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("type", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.tsPeriodSelectorUI_typeDesc_name());
            desc.setShortDescription(Bundle.tsPeriodSelectorUI_typeDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "tsPeriodSelectorUI.startDesc.name=Start",
        "tsPeriodSelectorUI.startDesc.desc=Start of the time span. Only complete periods will be taken into account"
    })
    private EnhancedPropertyDescriptor startDesc() {
        TimeSelector.SelectionType type = core.getType();
        if (type != TimeSelector.SelectionType.Between && type != TimeSelector.SelectionType.From) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("start", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.tsPeriodSelectorUI_startDesc_name());
            desc.setShortDescription(Bundle.tsPeriodSelectorUI_startDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "tsPeriodSelectorUI.endDesc.name=End",
        "tsPeriodSelectorUI.endDesc.desc=End of the time span. Only complete periods will be taken into account"
    })
    private EnhancedPropertyDescriptor endDesc() {
        TimeSelector.SelectionType type = core.getType();
        if (type != TimeSelector.SelectionType.Between && type != TimeSelector.SelectionType.To) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("end", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.tsPeriodSelectorUI_endDesc_name());
            desc.setShortDescription(Bundle.tsPeriodSelectorUI_endDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "tsPeriodSelectorUI.firstDesc.name=First",
        "tsPeriodSelectorUI.firstDesc.desc=Number of periods considered at the beginning of the series"
    })
    private EnhancedPropertyDescriptor firstDesc() {
        TimeSelector.SelectionType type = core.getType();
        if (type != TimeSelector.SelectionType.Excluding && type != TimeSelector.SelectionType.First) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("first", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.tsPeriodSelectorUI_firstDesc_name());
            desc.setShortDescription(Bundle.tsPeriodSelectorUI_firstDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "tsPeriodSelectorUI.lastDesc.name=Last",
        "tsPeriodSelectorUI.lastDesc.desc=Number of periods considered at the end of the series"
    })
    private EnhancedPropertyDescriptor lastDesc() {
        TimeSelector.SelectionType type = core.getType();
        if (type != TimeSelector.SelectionType.Excluding && type != TimeSelector.SelectionType.Last) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("last", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            desc.setDisplayName(Bundle.tsPeriodSelectorUI_lastDesc_name());
            desc.setShortDescription(Bundle.tsPeriodSelectorUI_lastDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
