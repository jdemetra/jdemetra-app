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
import demetra.timeseries.calendars.CalendarDefinition;
import demetra.timeseries.calendars.CalendarManager;
import demetra.timeseries.regression.ModellingContext;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Edit", id = "demetra.desktop.ui.calendars.actions.RemoveCalendarAction")
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
        WorkspaceItem<CalendarDefinition> tmp = activatedNode.getItem(CalendarDefinition.class);
        if (tmp == null)
            return;
        CalendarDefinition o = tmp.getElement();
        removeCalendar(o, activatedNode);
    }

    @Override
    protected boolean enable(ItemWsNode activatedNode) {
        WorkspaceItem<CalendarDefinition> tmp = activatedNode.getItem(CalendarDefinition.class);
        if (tmp == null)
            return false;
        return tmp.getStatus() != Status.System;
    }

    @Override
    public String getName() {
        return Bundle.CTL_RemoveCalendarAction();
    }

    @Messages({
        "RemoveCalendar.dialog.title=Remove calendar",        
        "RemoveCalendar.dialog.message=Are you sure?"
    })
    static void removeCalendar(CalendarDefinition p, ItemWsNode node) {
        DialogDescriptor.Confirmation dd = new DialogDescriptor.Confirmation(
                Bundle.RemoveCalendar_dialog_message(),
                Bundle.RemoveCalendar_dialog_title(),
                NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.YES_OPTION) {
            CalendarManager manager = ModellingContext.getActiveContext().getCalendars();
            manager.remove(p);
            node.getWorkspace().remove(node.getItem());
        }
    }
}