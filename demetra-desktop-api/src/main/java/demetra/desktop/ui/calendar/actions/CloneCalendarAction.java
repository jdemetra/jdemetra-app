/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.calendar.actions;

import com.google.common.collect.ImmutableList;
import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.ui.calendar.CalendarDocumentManager;
import demetra.desktop.ui.calendar.ChainedGregorianCalendarPanel;
import demetra.desktop.ui.calendar.CompositeGregorianCalendarPanel;
import demetra.desktop.ui.calendar.NationalCalendarPanel;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.timeseries.calendars.Calendar;
import demetra.timeseries.calendars.CalendarDefinition;
import demetra.timeseries.calendars.CalendarManager;
import demetra.timeseries.calendars.ChainedCalendar;
import demetra.timeseries.calendars.CompositeCalendar;
import demetra.timeseries.calendars.Holiday;
import demetra.timeseries.regression.ModellingContext;
import demetra.util.WeightedItem;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools", id = "demetra.desktop.ui.calendars.actions.CloneCalendarAction")
@ActionRegistration(displayName = "#CTL_CloneCalendarAction", lazy = false)
@ActionReferences({
    @ActionReference(path = CalendarDocumentManager.ITEMPATH, position = 1421, separatorBefore = 1400)
})
@Messages("CTL_CloneCalendarAction=Clone")
public final class CloneCalendarAction extends SingleNodeAction<ItemWsNode> {

    public CloneCalendarAction() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode activatedNode) {
        CalendarManager manager = ModellingContext.getActiveContext().getCalendars();
        CalendarDefinition o = AddCalendarAction.getProvider(activatedNode);
        if (o instanceof Calendar) {
            cloneNationalCalendar(manager, (Calendar) o, activatedNode);
        } else if (o instanceof ChainedCalendar) {
            cloneChainedCalendar(manager, (ChainedCalendar) o, activatedNode);
        } else if (o instanceof CompositeCalendar) {
            cloneCompositeCalendar(manager, (CompositeCalendar) o, activatedNode);
        }
    }

    @Override
    protected boolean enable(ItemWsNode activatedNode) {
        CalendarDefinition o = AddCalendarAction.getProvider(activatedNode);
        return o instanceof Calendar
                || o instanceof ChainedCalendar
                || o instanceof CompositeCalendar;
    }

    @Override
    public String getName() {
        return Bundle.CTL_CloneCalendarAction();
    }

    @Messages({
        "cloneNationalCalendar.dialog.title=Clone National Calendar"
    })
    static void cloneNationalCalendar(CalendarManager manager, Calendar p, ItemWsNode node) {
        NationalCalendarPanel panel = new NationalCalendarPanel();
        panel.setHolidays(ImmutableList.copyOf(p.getHolidays()));

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.cloneNationalCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            ImmutableList<Holiday> holidays = panel.getHolidays();
            Calendar newObject = new Calendar(holidays.toArray(new Holiday[holidays.size()]));
            AddCalendarAction.add(manager, panel.getCalendarName(), newObject);
        }
    }

    @Messages({
        "cloneChainedCalendar.dialog.title=Clone Chained Calendar"
    })
    static void cloneChainedCalendar(CalendarManager manager, ChainedCalendar p, ItemWsNode node) {
        ChainedGregorianCalendarPanel panel = new ChainedGregorianCalendarPanel();
        panel.setFirstCalendar(p.getFirst());
        panel.setDayBreak(p.getBreakDate());
        panel.setSecondCalendar(p.getSecond());

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.cloneChainedCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            ChainedCalendar newObject = new ChainedCalendar(panel.getFirstCalendar(), panel.getSecondCalendar(), panel.getDayBreak());
            AddCalendarAction.add(manager, panel.getCalendarName(), newObject);
        }
    }

    @Messages({
        "cloneCompositeCalendar.dialog.title=Clone Composite Calendar"
    })
    static void cloneCompositeCalendar(CalendarManager manager, CompositeCalendar p, ItemWsNode node) {
        CompositeGregorianCalendarPanel panel = new CompositeGregorianCalendarPanel("");
        panel.setWeightedItems(ImmutableList.copyOf(p.getCalendars()));

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.cloneCompositeCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            String name = panel.getCalendarName();
            ImmutableList<WeightedItem<String>> weightedItems = panel.getWeightedItems();
            CompositeCalendar newObj = new CompositeCalendar(weightedItems.toArray(new WeightedItem[weightedItems.size()]));
            AddCalendarAction.add(manager, name, newObj);
        }
    }
}
