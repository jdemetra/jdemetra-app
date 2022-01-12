/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace;

import demetra.desktop.workspace.ui.CalendarTopComponent;
import demetra.desktop.DemetraIcons;
import demetra.timeseries.calendars.Calendar;
import demetra.timeseries.calendars.CalendarDefinition;
import demetra.timeseries.calendars.CalendarManager;
import demetra.util.Id;
import demetra.util.LinearId;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = WorkspaceItemManager.class, position = 9900)
public class CalendarDocumentManager extends AbstractWorkspaceItemManager<CalendarDefinition> {

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
    protected CalendarDefinition createNewObject() {
        return Calendar.DEFAULT;
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
                WorkspaceItem<CalendarDefinition> doc = (WorkspaceItem<CalendarDefinition>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    openDocument(doc);
                }
            }
        };
    }

    public void openDocument(WorkspaceItem<CalendarDefinition> doc) {
        if (doc.isOpen()) {
            doc.getView().requestActive();
        } else {
            CalendarTopComponent view = new CalendarTopComponent(doc);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public List<WorkspaceItem<CalendarDefinition>> getDefaultItems() {
        List<WorkspaceItem<CalendarDefinition>> result = new ArrayList<>();
        String o = CalendarManager.DEF;
        result.add(systemItem(o, CalendarManager.getDefault(o)));
        return result;
    }

    @Override
    public Class<CalendarDefinition> getItemClass() {
        return CalendarDefinition.class;
    }

    @Override
    public Icon getManagerIcon() {
        return DemetraIcons.CALENDAR_16;
    }

    @Override
    public Icon getItemIcon(WorkspaceItem<CalendarDefinition> doc) {
        return super.getItemIcon(doc);
    }

    public static WorkspaceItem<CalendarDefinition> systemItem(String name, CalendarDefinition p) {
        return WorkspaceItem.system(ID, name, p);
    }
}
