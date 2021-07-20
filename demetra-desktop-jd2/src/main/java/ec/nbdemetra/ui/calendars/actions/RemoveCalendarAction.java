/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars.actions;

import ec.nbdemetra.ui.calendars.CalendarDocumentManager;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.calendars.ChainedGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.CompositeGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.GregorianCalendarManager;
import ec.tstoolkit.timeseries.calendars.IGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.NationalCalendarProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools", id = "ec.nbdemetra.ui.calendars.actions.RemoveCalendarAction")
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
        IGregorianCalendarProvider o = AddCalendarAction.getProvider(activatedNode);
        if (o instanceof NationalCalendarProvider) {
            removeNationalCalendar((NationalCalendarProvider) o, activatedNode);
        } else if (o instanceof ChainedGregorianCalendarProvider) {
            removeChainedCalendar((ChainedGregorianCalendarProvider) o, activatedNode);
        } else if (o instanceof CompositeGregorianCalendarProvider) {
            removeCompositeCalendar((CompositeGregorianCalendarProvider) o, activatedNode);
        }
    }

    @Override
    protected boolean enable(ItemWsNode activatedNode) {
        IGregorianCalendarProvider o = AddCalendarAction.getProvider(activatedNode);
        return (o instanceof NationalCalendarProvider && !((NationalCalendarProvider) o).isLocked())
                || o instanceof ChainedGregorianCalendarProvider
                || o instanceof CompositeGregorianCalendarProvider;
    }

    @Override
    public String getName() {
        return Bundle.CTL_RemoveCalendarAction();
    }

    @Messages({
        "removeNationalCalendar.dialog.title=Remove National Calendar",
        "removeNationalCalendar.dialog.message=Are you sure?"
    })
    static void removeNationalCalendar(NationalCalendarProvider p, ItemWsNode node) {
        DialogDescriptor.Confirmation dd = new DialogDescriptor.Confirmation(
                Bundle.removeNationalCalendar_dialog_message(),
                Bundle.removeNationalCalendar_dialog_title(),
                NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.YES_OPTION) {
            GregorianCalendarManager manager = ProcessingContext.getActiveContext().getGregorianCalendars();
            manager.remove(p);
            node.getWorkspace().remove(node.getItem());
        }
    }

    @Messages({
        "removeChainedCalendar.dialog.title=Remove Chained Calendar",
        "removeChainedCalendar.dialog.message=Are you sure?"
    })
    static void removeChainedCalendar(ChainedGregorianCalendarProvider p, ItemWsNode node) {
        DialogDescriptor.Confirmation dd = new DialogDescriptor.Confirmation(
                Bundle.removeChainedCalendar_dialog_message(),
                Bundle.removeChainedCalendar_dialog_title(),
                NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.YES_OPTION) {
            GregorianCalendarManager manager = ProcessingContext.getActiveContext().getGregorianCalendars();
            manager.remove(p);
            node.getWorkspace().remove(node.getItem());
        }
    }

    @Messages({
        "removeCompositeCalendar.dialog.title=Remove Composite Calendar",
        "removeCompositeCalendar.dialog.message=Are you sure?"
    })
    static void removeCompositeCalendar(CompositeGregorianCalendarProvider p, ItemWsNode node) {
        DialogDescriptor.Confirmation dd = new DialogDescriptor.Confirmation(
                Bundle.removeCompositeCalendar_dialog_message(),
                Bundle.removeCompositeCalendar_dialog_title(),
                NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.YES_OPTION) {
            GregorianCalendarManager manager = ProcessingContext.getActiveContext().getGregorianCalendars();
            manager.remove(p);
            node.getWorkspace().remove(node.getItem());
        }
    }
}
