/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.calendar.actions;

import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.workspace.CalendarDocumentManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItem.Status;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.timeseries.calendars.Calendar;
import demetra.timeseries.calendars.CalendarDefinition;
import demetra.timeseries.calendars.CalendarManager;
import demetra.timeseries.calendars.ChainedCalendar;
import demetra.timeseries.calendars.CompositeCalendar;
import demetra.timeseries.regression.ModellingContext;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools", id = "demetra.desktop.ui.calendars.actions.RemoveCalendarAction")
@ActionRegistration(displayName = "#CTL_RemoveCalendarAction", lazy = false)
@ActionReferences({
    @ActionReference(path = CalendarDocumentManager.ITEMPATH, position = 1422, separatorBefore = 1400)
})
@Messages("CTL_RemoveCalendarAction=Remove")
public final class RemoveCalendarAction extends SingleNodeAction<ItemWsNode> {

    public RemoveCalendarAction() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode activatedNode) {
        CalendarDefinition o = AddCalendarAction.getProvider(activatedNode);
        if (o instanceof Calendar) {
            removeNationalCalendar((Calendar) o, activatedNode);
        } else if (o instanceof ChainedCalendar) {
            removeChainedCalendar((ChainedCalendar) o, activatedNode);
        } else if (o instanceof CompositeCalendar) {
            removeCompositeCalendar((CompositeCalendar) o, activatedNode);
        }
    }

    @Override
    protected boolean enable(ItemWsNode activatedNode) {
        WorkspaceItem<CalendarDefinition> tmp = activatedNode.getItem(CalendarDefinition.class);
        
        if (tmp.getStatus() == Status.System)
            return false;
        CalendarDefinition o = tmp.getElement();
        return o instanceof Calendar 
                || o instanceof ChainedCalendar
                || o instanceof CompositeCalendar;
    }

    @Override
    public String getName() {
        return Bundle.CTL_RemoveCalendarAction();
    }

    @Messages({
        "removeNationalCalendar.dialog.title=Remove National Calendar",
        "removeNationalCalendar.dialog.message=Are you sure?"
    })
    static void removeNationalCalendar(Calendar p, ItemWsNode node) {
        DialogDescriptor.Confirmation dd = new DialogDescriptor.Confirmation(
                Bundle.removeNationalCalendar_dialog_message(),
                Bundle.removeNationalCalendar_dialog_title(),
                NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.YES_OPTION) {
            CalendarManager manager = ModellingContext.getActiveContext().getCalendars();
            manager.remove(p);
            node.getWorkspace().remove(node.getItem());
        }
    }

    @Messages({
        "removeChainedCalendar.dialog.title=Remove Chained Calendar",
        "removeChainedCalendar.dialog.message=Are you sure?"
    })
    static void removeChainedCalendar(ChainedCalendar p, ItemWsNode node) {
        DialogDescriptor.Confirmation dd = new DialogDescriptor.Confirmation(
                Bundle.removeChainedCalendar_dialog_message(),
                Bundle.removeChainedCalendar_dialog_title(),
                NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.YES_OPTION) {
            CalendarManager manager = ModellingContext.getActiveContext().getCalendars();
            manager.remove(p);
            node.getWorkspace().remove(node.getItem());
        }
    }

    @Messages({
        "removeCompositeCalendar.dialog.title=Remove Composite Calendar",
        "removeCompositeCalendar.dialog.message=Are you sure?"
    })
    static void removeCompositeCalendar(CompositeCalendar p, ItemWsNode node) {
        DialogDescriptor.Confirmation dd = new DialogDescriptor.Confirmation(
                Bundle.removeCompositeCalendar_dialog_message(),
                Bundle.removeCompositeCalendar_dialog_title(),
                NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.YES_OPTION) {
            CalendarManager manager = ModellingContext.getActiveContext().getCalendars();
            manager.remove(p);
            node.getWorkspace().remove(node.getItem());
        }
    }
}
