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

import com.google.common.base.Optional;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.nbdemetra.ui.tsproviders.ProvidersNode;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceLoader;
import ec.tss.tsproviders.TsProviders;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;
import org.openide.util.actions.Presenter;

@ActionID(category = "File",
        id = "ec.nbdemetra.ui.mru.ProviderMruAction")
@ActionRegistration(displayName = "#CTL_ProviderMruAction", lazy = false)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 810),
    @ActionReference(path = ProvidersNode.ACTION_PATH, position = 1320, separatorBefore = 1300)
})
@Messages("CTL_ProviderMruAction=Open recent")
public final class ProviderMruAction extends AbstractAction implements Presenter.Popup, Presenter.Menu {

    @Override
    public JMenuItem getMenuPresenter() {
        return new MruMenu(Bundle.CTL_ProviderMruAction());
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return getMenuPresenter();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static final class MruMenu extends JMenu implements DynamicMenuContent, PropertyChangeListener {

        public MruMenu(String s) {
            super(s);
            MruList.getProvidersInstance().addPropertyChangeListener(WeakListeners.propertyChange(this, MruList.getProvidersInstance()));
            updateMenu();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource().equals(MruList.getProvidersInstance())) {
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
            if (MruList.getProvidersInstance().isEmpty()) {
                this.setEnabled(false);
                return;
            }
            this.setEnabled(true);
            DataSourceProviderBuddySupport support = DataSourceProviderBuddySupport.getDefault();
            for (SourceId o : MruList.getProvidersInstance()) {
                JMenuItem item = new JMenuItem(new OpenDataSource(o.getDataSource()));
                item.setText(o.getLabel());
                item.setIcon(getIcon(support, o.getDataSource()));
                item.setEnabled(isLoadable(o.getDataSource()));
                add(item);
            }
            add(new JSeparator());
            add(new JMenuItem(new AbstractAction("Clear") {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    MruList.getProvidersInstance().clear();
                }
            }));
        }

        Icon getIcon(DataSourceProviderBuddySupport support, DataSource dataSource) {
            return ImageUtilities.image2Icon(support.get(dataSource).getIcon(dataSource, BeanInfo.ICON_COLOR_16x16, false));
        }

        boolean isLoadable(DataSource dataSource) {
            Optional<IDataSourceLoader> loader = TsProviders.lookup(IDataSourceLoader.class, dataSource);
            return loader.isPresent() && !loader.get().getDataSources().contains(dataSource);
        }
    }

    private static final class OpenDataSource extends AbstractAction {

        private final DataSource dataSource;

        public OpenDataSource(DataSource DataSource) {
            this.dataSource = DataSource;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<IDataSourceLoader> loader = TsProviders.lookup(IDataSourceLoader.class, dataSource);
            if (loader.isPresent()) {
                loader.get().open(dataSource);
            }
        }
    }
}
