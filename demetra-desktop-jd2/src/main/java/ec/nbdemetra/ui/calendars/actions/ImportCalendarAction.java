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
package ec.nbdemetra.ui.calendars.actions;

import com.google.common.collect.ImmutableList;
import demetra.ui.Config;
import ec.nbdemetra.ui.calendars.CalendarDocumentManager;
import ec.nbdemetra.ui.interchange.ImportNodeAction;
import ec.nbdemetra.ui.interchange.Importable;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.calendars.IGregorianCalendarProvider;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;
import demetra.ui.Converter;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "Edit", id = "ec.nbdemetra.ui.calendars.actions.ImportCalendarAction")
@ActionRegistration(displayName = "#CTL_ImportCalendarAction", lazy = false)
@ActionReferences({
    @ActionReference(path = CalendarDocumentManager.PATH, position = 1430)
})
@Messages("CTL_ImportCalendarAction=Import from")
public final class ImportCalendarAction extends SingleNodeAction<ItemWsNode> implements Presenter.Popup {

    private static final Converter<Config, IGregorianCalendarProvider> CONVERTER = new CalendarConfig().reverse();
    private static final ImmutableList<Importable> IMPORTABLES = ImmutableList.of(new ImportableCalendar());

    public ImportCalendarAction() {
        super(ItemWsNode.class);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = ImportNodeAction.getPopupPresenter(IMPORTABLES);
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
            IGregorianCalendarProvider cal = CONVERTER.doForward(config);
            Workspace ws = WorkspaceFactory.getInstance().getActiveWorkspace();
            if (ws.searchDocument(cal) == null) {
                String name = ProcessingContext.getActiveContext().getGregorianCalendars().get(cal);
                ws.add(WorkspaceItem.system(CalendarDocumentManager.ID, name, cal));
            }
        }
    }
}
