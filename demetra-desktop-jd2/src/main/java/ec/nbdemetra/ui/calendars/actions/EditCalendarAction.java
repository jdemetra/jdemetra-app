/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars.actions;

import com.google.common.collect.ImmutableList;
import ec.nbdemetra.ui.calendars.CalendarDocumentManager;
import ec.nbdemetra.ui.calendars.ChainedGregorianCalendarPanel;
import ec.nbdemetra.ui.calendars.CompositeGregorianCalendarPanel;
import ec.nbdemetra.ui.calendars.NationalCalendarPanel;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.calendars.ChainedGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.CompositeGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.GregorianCalendarManager;
import ec.tstoolkit.timeseries.calendars.IGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.NationalCalendarProvider;
import ec.tstoolkit.timeseries.calendars.SpecialDayEvent;
import java.util.Collection;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools", id = "ec.nbdemetra.ui.calendars.actions.EditCalendarAction")
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
        GregorianCalendarManager manager = ProcessingContext.getActiveContext().getGregorianCalendars();
        IGregorianCalendarProvider o = AddCalendarAction.getProvider(activatedNode);
        if (o instanceof NationalCalendarProvider) {
            editNationalCalendar(manager, (NationalCalendarProvider) o, activatedNode);
        } else if (o instanceof ChainedGregorianCalendarProvider) {
            editChainedCalendar(manager, (ChainedGregorianCalendarProvider) o, activatedNode);
        } else if (o instanceof CompositeGregorianCalendarProvider) {
            editCompositeCalendar(manager, (CompositeGregorianCalendarProvider) o, activatedNode);
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
        return Bundle.CTL_EditCalendarAction();
    }

    static void replace(GregorianCalendarManager manager, String oldName, String newName, IGregorianCalendarProvider newObj, ItemWsNode node) {
        manager.remove(oldName);
        manager.set(newName, newObj);
        node.getWorkspace().remove(node.getItem());
        node.getWorkspace().add(CalendarDocumentManager.systemItem(newName, newObj));
    }

    @Messages({
        "editNationalCalendar.dialog.title=Edit National Calendar"
    })
    static void editNationalCalendar(GregorianCalendarManager manager, NationalCalendarProvider p, ItemWsNode node) {
        String oldName = manager.get(p);


        NationalCalendarPanel panel = new NationalCalendarPanel();
        panel.setCalendarName(oldName);
        panel.setMeanCorrection(p.isLongTermMeanCorrection());
        panel.setJulianEaster(p.isJulianEaster());
        panel.setSpecialDayEvents(ImmutableList.copyOf(p.events()));

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.editNationalCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            String name = panel.getCalendarName();
            boolean mean=panel.isMeanCorrection();
            boolean julian=panel.isJulianCalendar();
            Collection<SpecialDayEvent> events = panel.getSpecialDayEvents();
            NationalCalendarProvider np = new NationalCalendarProvider(events, mean, julian);
            replace(manager, oldName, name, np, node);
        }
    }

    @Messages({
        "editChainedCalendar.dialog.title=Edit Chained Gregorian Calendar"
    })
    static void editChainedCalendar(GregorianCalendarManager manager, ChainedGregorianCalendarProvider p, ItemWsNode node) {
        String oldName = manager.get(p);

        ChainedGregorianCalendarPanel panel = new ChainedGregorianCalendarPanel();
        panel.setCalendarName(oldName);
        panel.setFirstCalendar(p.first);
        panel.setSecondCalendar(p.second);
        panel.setDayBreak(p.breakDay);

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.editChainedCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            String name = panel.getCalendarName();
            String first = panel.getFirstCalendar();
            String second = panel.getSecondCalendar();
            Day dayBreak = panel.getDayBreak();
            replace(manager, oldName, name, new ChainedGregorianCalendarProvider(first, dayBreak, second), node);
        }
    }

    @Messages({
        "editCompositeCalendar.dialog.title=Edit Composite Gregorian Calendar"
    })
    static void editCompositeCalendar(GregorianCalendarManager manager, CompositeGregorianCalendarProvider p, ItemWsNode node) {
        String oldName = manager.get(p);

        CompositeGregorianCalendarPanel panel = new CompositeGregorianCalendarPanel(oldName);
        panel.setWeightedItems(ImmutableList.copyOf(p.items()));

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.editCompositeCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            String name = panel.getCalendarName();
            CompositeGregorianCalendarProvider newObj = new CompositeGregorianCalendarProvider();
            newObj.add(panel.getWeightedItems());
            replace(manager, oldName, name, newObj, node);
        }
    }
}
