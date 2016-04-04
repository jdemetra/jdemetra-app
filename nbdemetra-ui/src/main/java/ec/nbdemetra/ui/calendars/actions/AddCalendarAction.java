/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars.actions;

import ec.nbdemetra.ui.calendars.CalendarDocumentManager;
import ec.nbdemetra.ui.calendars.ChainedGregorianCalendarPanel;
import ec.nbdemetra.ui.calendars.CompositeGregorianCalendarPanel;
import ec.nbdemetra.ui.calendars.NationalCalendarPanel;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.calendars.ChainedGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.CompositeGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.GregorianCalendarManager;
import ec.tstoolkit.timeseries.calendars.IGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.NationalCalendarProvider;
import ec.tstoolkit.timeseries.calendars.SpecialDayEvent;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(category = "Tools", id = "ec.nbdemetra.ui.calendars.actions.AddCalendarAction")
@ActionRegistration(displayName = "#CTL_AddCalendarAction", lazy = false)
@ActionReferences({
    @ActionReference(path = CalendarDocumentManager.PATH, position = 1000)
})
@Messages("CTL_AddCalendarAction=Add Calendar")
public final class AddCalendarAction extends AbstractAction implements Presenter.Popup, Presenter.Menu {

    @Override
    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Messages({
        "addCalendarAction.national=National",
        "addCalendarAction.chained=Chained",
        "addCalendarAction.composite=Composite"
    })
    @Override
    public JMenuItem getMenuPresenter() {
        JMenu result = new JMenu(Bundle.CTL_AddCalendarAction());
        result.add(new AbstractAction(Bundle.addCalendarAction_national()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNationalCalendar(ProcessingContext.getActiveContext().getGregorianCalendars());
            }
        });
        result.add(new AbstractAction(Bundle.addCalendarAction_chained()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                addChainedCalendar(ProcessingContext.getActiveContext().getGregorianCalendars());
            }
        });
        result.add(new AbstractAction(Bundle.addCalendarAction_composite()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCompositeCalendar(ProcessingContext.getActiveContext().getGregorianCalendars());
            }
        });
        return result;
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return getMenuPresenter();
    }

    static IGregorianCalendarProvider getProvider(ItemWsNode activatedNode) {
        WorkspaceItem<IGregorianCalendarProvider> tmp = activatedNode.getItem(IGregorianCalendarProvider.class);
        return tmp != null ? tmp.getElement() : null;
    }

    static void add(GregorianCalendarManager manager, String name, IGregorianCalendarProvider p) {
        manager.set(name, p);
        WorkspaceFactory.getInstance().getActiveWorkspace().add(CalendarDocumentManager.systemItem(name, p));
    }

    @Messages({
        "addNationalCalendar.dialog.title=Add National Calendar"
    })
    static void addNationalCalendar(GregorianCalendarManager manager) {
        NationalCalendarPanel panel = new NationalCalendarPanel();

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.addNationalCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            boolean mean=panel.isMeanCorrection();
            boolean julian=panel.isJulianCalendar();
            Collection<SpecialDayEvent> events = panel.getSpecialDayEvents();
            NationalCalendarProvider np = new NationalCalendarProvider(events, mean, julian);
            add(manager, panel.getCalendarName(), np);
        }
    }

    @Messages({
        "addChainedCalendar.dialog.title=Add Chained Gregorian Calendar"
    })
    static void addChainedCalendar(GregorianCalendarManager manager) {
        ChainedGregorianCalendarPanel panel = new ChainedGregorianCalendarPanel();

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.addChainedCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            String first = panel.getFirstCalendar();
            String second = panel.getSecondCalendar();
            Day dayBreak = panel.getDayBreak();
            add(manager, panel.getCalendarName(), new ChainedGregorianCalendarProvider(first, dayBreak, second));
        }
    }

    @Messages({
        "addCompositeCalendar.dialog.title=Add Composite Gregorian Calendar"
    })
    static void addCompositeCalendar(GregorianCalendarManager manager) {
        CompositeGregorianCalendarPanel panel = new CompositeGregorianCalendarPanel("");

        DialogDescriptor dd = panel.createDialogDescriptor(Bundle.addCompositeCalendar_dialog_title());
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            CompositeGregorianCalendarProvider newObj = new CompositeGregorianCalendarProvider();
            newObj.add(panel.getWeightedItems());
            add(manager, panel.getCalendarName(), newObj);
        }
    }
}
