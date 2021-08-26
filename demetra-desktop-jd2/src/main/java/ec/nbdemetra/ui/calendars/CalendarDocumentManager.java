/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.calendars;

import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ws.AbstractWorkspaceItemManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tstoolkit.timeseries.calendars.GregorianCalendarManager;
import ec.tstoolkit.timeseries.calendars.IGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.NationalCalendarProvider;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IWorkspaceItemManager.class, position = 9900)
public class CalendarDocumentManager extends AbstractWorkspaceItemManager<IGregorianCalendarProvider> {

    public static final LinearId ID = new LinearId("Utilities", "Calendars");
    public static final String PATH = "Calendars";
    public static final String ITEMPATH = "Calendars.item";

    @Override
    protected String getItemPrefix() {
        return "Calendars";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected IGregorianCalendarProvider createNewObject() {
        return new NationalCalendarProvider();
    }

    @Override
    public ItemType getItemType() {
        return ItemType.Doc;
    }

    @Override
    public String getActionsPath() {
        return PATH;
    }

    @Override
    public Status getStatus() {
        return Status.Experimental;
    }

    @Override
    public Action getPreferredItemAction(final Id child) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorkspaceItem<IGregorianCalendarProvider> doc = (WorkspaceItem<IGregorianCalendarProvider>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    openDocument(doc);
                }
            }
        };
    }

    public void openDocument(WorkspaceItem<IGregorianCalendarProvider> doc) {
        if (doc.isOpen()) {
            doc.getView().requestActive();
        } else {
            CalendarTopComponent view = new CalendarTopComponent(doc);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public List<WorkspaceItem<IGregorianCalendarProvider>> getDefaultItems() {
        List<WorkspaceItem<IGregorianCalendarProvider>> result = new ArrayList<>();
        String o = GregorianCalendarManager.DEF;
        result.add(systemItem(o, GregorianCalendarManager.getDefault(o)));
        return result;
    }

    @Override
    public Class<IGregorianCalendarProvider> getItemClass() {
        return IGregorianCalendarProvider.class;
    }

    @Override
    public Icon getManagerIcon() {
        return DemetraUiIcon.CALENDAR_16;
    }

    @Override
    public Icon getItemIcon(WorkspaceItem<IGregorianCalendarProvider> doc) {
        IGregorianCalendarProvider o = doc.getElement();
        if (o instanceof NationalCalendarProvider && ((NationalCalendarProvider) o).isLocked()) {
            return DemetraUiIcon.PUZZLE_16; // TODO: choose another icon
        }
        return super.getItemIcon(doc);
    }

    public static WorkspaceItem<IGregorianCalendarProvider> systemItem(String name, IGregorianCalendarProvider p) {
        return WorkspaceItem.system(ID, name, p);
    }
}
