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
package ec.nbdemetra.ui.mru;

import demetra.bridge.TsConverter;
import ec.nbdemetra.ws.FileRepository;
import ec.nbdemetra.ws.IWorkspaceRepository;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.tss.tsproviders.DataSource;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(category = "File",
        id = "ec.nbdemetra.ui.mru.WorkspaceMruAction")
@ActionRegistration(displayName = "#CTL_WorkspaceMruAction", lazy = false)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 210)
})
@Messages("CTL_WorkspaceMruAction=Open Recent Workspace")
public final class WorkspaceMruAction extends AbstractAction implements Presenter.Popup, Presenter.Menu {

    @Override
    public JMenuItem getMenuPresenter() {
        return new WorkspaceMruAction.MruMenu(Bundle.CTL_WorkspaceMruAction());
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return getMenuPresenter();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
    }

    private static final class MruMenu extends JMenu implements DynamicMenuContent, PropertyChangeListener {

        public MruMenu(String s) {
            super(s);
            MruList.getWorkspacesInstance().addWeakPropertyChangeListener(this);
            updateMenu();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource().equals(MruList.getWorkspacesInstance())) {
                updateMenu();
            }
        }

        @Override
        public JComponent[] getMenuPresenters() {
            updateMenu();
            return new JComponent[]{this};
        }

        @Override
        public JComponent[] synchMenuPresenters(JComponent[] items) {
            return getMenuPresenters();
        }

        private void updateMenu() {
            removeAll();
            if (MruList.getWorkspacesInstance().isEmpty()) {
                setEnabled(false);
                return;
            }
            setEnabled(true);
            for (SourceId item : MruList.getWorkspacesInstance()) {
                WorkspaceStatus status = WorkspaceStatus.get(TsConverter.fromDataSource(item.getDataSource()));
                JMenuItem menuItem = new JMenuItem(new OpenAction(item));
                menuItem.setText(item.getLabel());
                menuItem.setEnabled(status.equals(WorkspaceStatus.LOADABLE));
                menuItem.setToolTipText(status.name());
                add(menuItem);
            }
            add(new JSeparator());
            add(new JMenuItem(ClearAction.INSTANCE)).setText("Clear");
        }
    }

    private static final class OpenAction extends AbstractAction {

        private final SourceId item;

        OpenAction(SourceId item) {
            this.item = item;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!WorkspaceFactory.getInstance().closeWorkspace(true)) {
                return;
            }
            IWorkspaceRepository repository = WorkspaceFactory.getInstance().getRepository(item.getDataSource().getProviderName());
            if (repository != null) {
                Workspace ws = new Workspace(item.dataSource, item.label);
                if (repository.load(ws)) {
                    WorkspaceFactory.getInstance().setActiveWorkspace(ws, WorkspaceFactory.Event.OPEN);
                }
            }
        }
    }

    private static final class ClearAction extends AbstractAction {

        static final ClearAction INSTANCE = new ClearAction();

        @Override
        public void actionPerformed(ActionEvent ae) {
            MruList.getWorkspacesInstance().clear();
        }
    }

    private enum WorkspaceStatus {
        LOADABLE, FILE_MISSING, LOADED, UNLOADABLE;

        static WorkspaceStatus get(DataSource dataSource) {
            if (WorkspaceFactory.getInstance().getRepository(dataSource.getProviderName()) == null) {
                return UNLOADABLE;
            }
            File file = FileRepository.decode(TsConverter.toDataSource(dataSource));
            if (file == null) {
                return UNLOADABLE;
            }
            if (dataSource.equals(WorkspaceFactory.getInstance().getActiveWorkspace().getDataSource())) {
                return LOADED;
            }
            if (!file.exists()) {
                return FILE_MISSING;
            }
            return LOADABLE;
        }
    }
}
