/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.benchmarking;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mats Maggi
 */
@ServiceProvider(
        service = IWorkspaceItemManager.class,
        position = 4000)
public class CalendarizationDocumentManager extends AbstractWorkspaceItemManager<CalendarizationDocument> {

    public static final LinearId ID = new LinearId("Benchmarking", "Calendarization");
    public static final String PATH = "calendarization";
    public static final String ITEMPATH = "calendarization.item";
    public static final String CONTEXTPATH = "calendarization.context";

    @Override
    protected String getItemPrefix() {
        return "Calendarization";
    }

    @Override
    protected CalendarizationDocument createNewObject() {
        return new CalendarizationDocument();
    }

    @Override
    public Status getStatus() {
        return Status.Experimental;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.Doc;
    }

    @Override
    public Class<CalendarizationDocument> getItemClass() {
        return CalendarizationDocument.class;
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public String getActionsPath() {
        return PATH;
    }

    @Override
    public Action getPreferredItemAction(final Id child) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorkspaceItem<CalendarizationDocument> doc = (WorkspaceItem<CalendarizationDocument>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    openDocument(doc);
                }
            }
        };
    }
    
    public void openDocument(WorkspaceItem<CalendarizationDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            CalendarizationTopComponent view = new CalendarizationTopComponent(item);
            view.open();
            view.requestActive();
        }
    }
    
    @Override
    public Icon getManagerIcon() {
        return DemetraUiIcon.CALENDAR_16;
    }
}
