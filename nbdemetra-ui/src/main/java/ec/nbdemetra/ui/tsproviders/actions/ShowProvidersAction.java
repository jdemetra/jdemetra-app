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
package ec.nbdemetra.ui.tsproviders.actions;

import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.tsproviders.ProvidersNode;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;
import org.openide.util.actions.Presenter;

@ActionID(category = "Edit", id = "ec.nbdemetra.ui.tsproviders.actions.ShowProvidersAction")
@ActionRegistration(displayName = "#CTL_ShowProvidersAction", lazy = false)
@ActionReferences({
    @ActionReference(path = ProvidersNode.ACTION_PATH, position = 1460, separatorBefore = 1450)
})
@Messages("CTL_ShowProvidersAction=Show provider nodes")
public final class ShowProvidersAction extends AbstractAction implements Presenter.Popup, PropertyChangeListener {

    private final JCheckBoxMenuItem item;

    public ShowProvidersAction() {
        this.item = new JCheckBoxMenuItem(this);
        item.setText(Bundle.CTL_ShowProvidersAction());
        DemetraUI demetraUI = DemetraUI.getDefault();
        item.setSelected(demetraUI.isShowTsProviderNodes());
        demetraUI.addPropertyChangeListener(WeakListeners.propertyChange(this, demetraUI));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DemetraUI demetraUI = DemetraUI.getDefault();
        demetraUI.setShowTsProviderNodes(!demetraUI.isShowTsProviderNodes());
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return item;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case DemetraUI.SHOW_TSPROVIDER_NODES_PROPERTY:
                item.setSelected((Boolean) evt.getNewValue());
                break;
        }
    }
}
