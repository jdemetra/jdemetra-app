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

import demetra.desktop.Config;
import ec.nbdemetra.ui.calendars.CalendarDocumentManager;
import demetra.desktop.interchange.Exportable;
import demetra.desktop.interchange.Interchange;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.tstoolkit.timeseries.calendars.GregorianCalendarManager;
import ec.tstoolkit.timeseries.calendars.IGregorianCalendarProvider;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.Presenter;
import demetra.desktop.Converter;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "Edit", id = "ec.nbdemetra.ui.calendars.actions.ExportCalendarAction")
@ActionRegistration(displayName = "#CTL_ExportCalendarAction", lazy = false)
@ActionReferences({
    @ActionReference(path = CalendarDocumentManager.ITEMPATH, position = 1430)
})
@Messages("CTL_ExportCalendarAction=Export to")
public final class ExportCalendarAction extends NodeAction implements Presenter.Popup {

    private static final Converter<IGregorianCalendarProvider, Config> CONVERTER = new CalendarConfig();

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = Interchange.getDefault().newExportMenu(getExportables(getActivatedNodes()));
        result.setText(Bundle.CTL_ExportCalendarAction());
        return result;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return Stream.of(activatedNodes).anyMatch(ExportCalendarAction::isExportable);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    private static boolean isExportable(Node o) {
        return o instanceof ItemWsNode && isExportable((ItemWsNode) o);
    }

    private static boolean isExportable(ItemWsNode o) {
        return !o.getDisplayName().equals(GregorianCalendarManager.DEF);
    }

    private static List<Exportable> getExportables(Node[] activatedNodes) {
        return Stream.of(activatedNodes)
                .filter(ExportCalendarAction::isExportable)
                .map(ItemWsNode.class::cast)
                .map(ExportableCalendar::new)
                .collect(Collectors.toList());
    }

    private static final class ExportableCalendar implements Exportable {

        private final ItemWsNode input;

        public ExportableCalendar(ItemWsNode input) {
            this.input = input;
        }

        @Override
        public Config exportConfig() {
            IGregorianCalendarProvider cal = AddCalendarAction.getProvider(input);
            return CONVERTER.doForward(cal);
        }
    }
}
