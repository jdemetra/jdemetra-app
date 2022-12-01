/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.calendar.actions;

import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.ui.calendar.ChainedGregorianCalendarPanel;
import demetra.desktop.ui.calendar.CompositeGregorianCalendarPanel;
import demetra.desktop.ui.calendar.NationalCalendarPanel;
import demetra.desktop.workspace.CalendarDocumentManager;
import demetra.desktop.workspace.Workspace;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.timeseries.calendars.*;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ActionID(category = "Tools", id = "demetra.desktop.ui.calendars.actions.EditCalendarAction")
@ActionRegistration(displayName = "#CTL_EditCalendarAction", lazy = false)
@ActionReferences({
    @ActionReference(path = CalendarDocumentManager.ITEMPATH, position = 1420, separatorBefore = 1400)
})
@Messages("CTL_EditCalendarAction=Edit")
public final class EditCalendarAction extends SingleNodeAction<ItemWsNode> {

    public EditCalendarAction() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode activatedNode) {
        CalendarManager manager = ModellingContext.getActiveContext().getCalendars();
        CalendarDefinition o = AddCalendarAction.getProvider(activatedNode);
        if (o instanceof Calendar) {
            editNationalCalendar(manager, (Calendar) o, activatedNode);
        } else if (o instanceof ChainedCalendar) {
            editChainedCalendar(manager, (ChainedCalendar) o, activatedNode);
        } else if (o instanceof CompositeCalendar) {
            editCompositeCalendar(manager, (CompositeCalendar) o, activatedNode);
        }
    }

    @Override
    protected boolean enable(ItemWsNode activatedNode) {
        CalendarDefinition o = AddCalendarAction.getProvider(activatedNode);
        return (o instanceof Calendar)
                || o instanceof ChainedCalendar
                || o instanceof CompositeCalendar;
    }

    @Override
    public String getName() {
        return Bundle.CTL_EditCalendarAction();
    }

    static void replace(CalendarManager manager, String oldName, String newName, CalendarDefinition newObj, ItemWsNode node) {
        manager.remove(oldName);
        manager.set(newName, newObj);
        WorkspaceItem<CalendarDefinition> item = (WorkspaceItem<CalendarDefinition>) node.getItem();
        item.setElement(newObj);
        Workspace workspace = node.getWorkspace();
        WorkspaceFactory.Event ev = new WorkspaceFactory.Event(workspace, item.getId(), WorkspaceFactory.Event.ITEMCHANGED, null);
        WorkspaceFactory.getInstance().notifyEvent(ev);
        if (oldName != newName) {
            item.setDisplayName(newName);
            WorkspaceFactory.Event nev = new WorkspaceFactory.Event(workspace, item.getId(), WorkspaceFactory.Event.ITEMRENAMED, null);
            WorkspaceFactory.getInstance().notifyEvent(nev);
        }
    }

    @Messages({
        "editNationalCalendar.dialog.title=Edit National Calendar"
    })
    static void editNationalCalendar(CalendarManager manager, Calendar p, ItemWsNode node) {
        String oldName = manager.get(p);

        NationalCalendarPanel panel = new NationalCalendarPanel();
        panel.setCalendarName(oldName);
        panel.setHolidays(Arrays.asList(p.getHolidays()));

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.editNationalCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            String name = panel.getCalendarName();
            Collection<Holiday> events = panel.getHolidays();
            Calendar np = new Calendar(events.toArray(new Holiday[events.size()]));
            replace(manager, oldName, name, np, node);
        }
    }

    @Messages({
        "editChainedCalendar.dialog.title=Edit Chained Gregorian Calendar"
    })
    static void editChainedCalendar(CalendarManager manager, ChainedCalendar p, ItemWsNode node) {
        String oldName = manager.get(p);

        ChainedGregorianCalendarPanel panel = new ChainedGregorianCalendarPanel();
        panel.setCalendarName(oldName);
        panel.setFirstCalendar(p.getFirst());
        panel.setSecondCalendar(p.getSecond());
        panel.setDayBreak(p.getBreakDate());

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.editChainedCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            String name = panel.getCalendarName();
            String first = panel.getFirstCalendar();
            String second = panel.getSecondCalendar();
            LocalDate dayBreak = panel.getDayBreak();
            replace(manager, oldName, name, new ChainedCalendar(first, second, dayBreak), node);
        }
    }

    @Messages({
        "editCompositeCalendar.dialog.title=Edit Composite Gregorian Calendar"
    })
    static void editCompositeCalendar(CalendarManager manager, CompositeCalendar p, ItemWsNode node) {
        String oldName = manager.get(p);

        CompositeGregorianCalendarPanel panel = new CompositeGregorianCalendarPanel(oldName);
        panel.setWeightedItems(Arrays.asList(p.getCalendars()));

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.editCompositeCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            String name = panel.getCalendarName();
            List<WeightedItem<String>> weightedItems = panel.getWeightedItems();
            CompositeCalendar newObj = new CompositeCalendar(weightedItems.toArray(new WeightedItem[weightedItems.size()]));
            replace(manager, oldName, name, newObj, node);
        }
    }
}
