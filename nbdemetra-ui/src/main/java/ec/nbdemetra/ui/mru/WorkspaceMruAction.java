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

import ec.nbdemetra.ws.IWorkspaceRepository;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.tss.tsproviders.DataSource;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
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
import org.openide.util.WeakListeners;
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
            MruList.getWorkspacesInstance().addPropertyChangeListener(WeakListeners.propertyChange(this, MruList.getWorkspacesInstance()));
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
            return new JComponent[]{this};
        }

        @Override
        public JComponent[] synchMenuPresenters(JComponent[] items) {
            return getMenuPresenters();
        }

        private void updateMenu() {
            removeAll();
            if (MruList.getWorkspacesInstance().isEmpty()) {
                this.setEnabled(false);
                return;
            }
            this.setEnabled(true);
            for (final SourceId item : MruList.getWorkspacesInstance()) {
                Action action = new AbstractAction() {
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
                };
                action.putValue(Action.NAME, item.getLabel());
                //            action.putValue(Action.SMALL_ICON, DemetraUI.getInstance().getMonikerUI().getIcon(item.getDataSource()));
                JMenuItem jMenuItem = new JMenuItem(action);
                jMenuItem.setEnabled(isLoadable(item.getDataSource()));
                add(jMenuItem);
            }
            add(new JSeparator());
            add(new JMenuItem(new AbstractAction("Clear") {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    MruList.getWorkspacesInstance().clear();
                }
            }));
        }

        boolean isLoadable(DataSource dataSource) {
            return WorkspaceFactory.getInstance().getRepository(dataSource.getProviderName()) != null;
        }
    }
}
