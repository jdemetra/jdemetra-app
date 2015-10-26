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

@ActionID(category = "Tools", id = "ec.nbdemetra.ui.calendars.actions.CloneCalendarAction")
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
        GregorianCalendarManager manager = ProcessingContext.getActiveContext().getGregorianCalendars();
        IGregorianCalendarProvider o = AddCalendarAction.getProvider(activatedNode);
        if (o instanceof NationalCalendarProvider) {
            cloneNationalCalendar(manager, (NationalCalendarProvider) o, activatedNode);
        } else if (o instanceof ChainedGregorianCalendarProvider) {
            cloneChainedCalendar(manager, (ChainedGregorianCalendarProvider) o, activatedNode);
        } else if (o instanceof CompositeGregorianCalendarProvider) {
            cloneCompositeCalendar(manager, (CompositeGregorianCalendarProvider) o, activatedNode);
        }
    }

    @Override
    protected boolean enable(ItemWsNode activatedNode) {
        IGregorianCalendarProvider o = AddCalendarAction.getProvider(activatedNode);
        return o instanceof NationalCalendarProvider
                || o instanceof ChainedGregorianCalendarProvider
                || o instanceof CompositeGregorianCalendarProvider;
    }

    @Override
    public String getName() {
        return Bundle.CTL_CloneCalendarAction();
    }

    @Messages({
        "cloneNationalCalendar.dialog.title=Clone National Calendar"
    })
    static void cloneNationalCalendar(GregorianCalendarManager manager, NationalCalendarProvider p, ItemWsNode node) {
        NationalCalendarPanel panel = new NationalCalendarPanel();
        panel.setSpecialDayEvents(ImmutableList.copyOf(p.events()));
        panel.setMeanCorrection(p.isLongTermMeanCorrection());
        panel.setJulianEaster(p.isJulianEaster());

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.cloneNationalCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            boolean mean=panel.isMeanCorrection();
            boolean julian=panel.isJulianCalendar();
            NationalCalendarProvider newObject = new NationalCalendarProvider(panel.getSpecialDayEvents(), mean, julian);
            AddCalendarAction.add(manager, panel.getCalendarName(), newObject);
        }
    }

    @Messages({
        "cloneChainedCalendar.dialog.title=Clone Chained Calendar"
    })
    static void cloneChainedCalendar(GregorianCalendarManager manager, ChainedGregorianCalendarProvider p, ItemWsNode node) {
        ChainedGregorianCalendarPanel panel = new ChainedGregorianCalendarPanel();
        panel.setFirstCalendar(p.first);
        panel.setDayBreak(p.breakDay);
        panel.setSecondCalendar(p.second);

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.cloneChainedCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            ChainedGregorianCalendarProvider newObject = new ChainedGregorianCalendarProvider(p.first, p.breakDay, p.second);
            AddCalendarAction.add(manager, panel.getCalendarName(), newObject);
        }
    }

    @Messages({
        "cloneCompositeCalendar.dialog.title=Clone Composite Calendar"
    })
    static void cloneCompositeCalendar(GregorianCalendarManager manager, CompositeGregorianCalendarProvider p, ItemWsNode node) {
        CompositeGregorianCalendarPanel panel = new CompositeGregorianCalendarPanel("");
        panel.setWeightedItems(ImmutableList.copyOf(p.items()));

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.cloneCompositeCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            String name = panel.getCalendarName();
            CompositeGregorianCalendarProvider newObj = new CompositeGregorianCalendarProvider();
            newObj.add(panel.getWeightedItems());
            AddCalendarAction.add(manager, name, newObj);
        }
    }
}
