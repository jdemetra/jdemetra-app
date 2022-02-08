/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package demetra.desktop.ui.calendar.actions;

import com.google.common.collect.ImmutableList;
import demetra.desktop.Config;
import demetra.desktop.interchange.Importable;
import demetra.desktop.interchange.Interchange;
import demetra.desktop.nodes.SingleNodeAction;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;
import demetra.desktop.Converter;
import demetra.desktop.workspace.CalendarDocumentManager;
import demetra.desktop.workspace.Workspace;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.timeseries.calendars.CalendarDefinition;
import demetra.timeseries.regression.ModellingContext;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "Edit", id = "demetra.desktop.ui.calendars.actions.ImportCalendarAction")
@ActionRegistration(displayName = "#CTL_ImportCalendarAction", lazy = false)
@ActionReferences({
    @ActionReference(path = CalendarDocumentManager.PATH, position = 1430)
})
@Messages("CTL_ImportCalendarAction=Import from")
public final class ImportCalendarAction extends SingleNodeAction<ItemWsNode> implements Presenter.Popup {

    private static final Converter<Config, CalendarDefinition> CONVERTER = new CalendarConfig().reverse();
    private static final ImmutableList<Importable> IMPORTABLES = ImmutableList.of(new ImportableCalendar());

    public ImportCalendarAction() {
        super(ItemWsNode.class);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = Interchange.getDefault().newImportMenu(IMPORTABLES);
        result.setText(Bundle.CTL_ImportCalendarAction());
        return result;
    }

    @Override
    protected void performAction(ItemWsNode activatedNode) {
    }

    @Override
    protected boolean enable(ItemWsNode activatedNode) {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }

    private static final class ImportableCalendar implements Importable {

        @Override
        public String getDomain() {
            return CalendarConfig.DOMAIN;
        }

        @Override
        public void importConfig(Config config) throws IllegalArgumentException {
            CalendarDefinition cal = CONVERTER.doForward(config);
            Workspace ws = WorkspaceFactory.getInstance().getActiveWorkspace();
            if (ws.searchDocumentByElement(cal) == null) {
                String name = ModellingContext.getActiveContext().getCalendars().get(cal);
                ws.add(WorkspaceItem.system(CalendarDocumentManager.ID, name, cal));
            }
        }
    }
}
