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
package demetra.desktop.core.tsproviders;

import demetra.desktop.DemetraBehaviour;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@ActionID(category = "Edit", id = ShowProvidersNodeAction.ID)
@ActionRegistration(displayName = "#CTL_ShowProvidersAction", lazy = false)
@Messages("CTL_ShowProvidersAction=Show provider nodes")
public final class ShowProvidersNodeAction extends AbstractAction implements Presenter.Popup, PropertyChangeListener {

    public static final String ID = "demetra.desktop.core.tsproviders.ShowProvidersAction";

    private final JCheckBoxMenuItem item;

    public ShowProvidersNodeAction() {
        this.item = new JCheckBoxMenuItem(this);
        item.setText(Bundle.CTL_ShowProvidersAction());
        DemetraBehaviour options = DemetraBehaviour.get();
        item.setSelected(options.isShowTsProviderNodes());
        options.addWeakPropertyChangeListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DemetraBehaviour options = DemetraBehaviour.get();
        options.setShowTsProviderNodes(!options.isShowTsProviderNodes());
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return item;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case DemetraBehaviour.SHOW_TS_PROVIDER_NODES_PROPERTY:
                item.setSelected((Boolean) evt.getNewValue());
                break;
        }
    }
}
