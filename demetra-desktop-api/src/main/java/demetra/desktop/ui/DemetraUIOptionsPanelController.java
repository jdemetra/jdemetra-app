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
package demetra.desktop.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

@OptionsPanelController.SubRegistration(location = "Demetra",
displayName = "#AdvancedOption_DisplayName_DemetraUI",
keywords = "#AdvancedOption_Keywords_DemetraUI",
keywordsCategory = "Demetra/DemetraUI",
id = DemetraUIOptionsPanelController.ID, position = 2)
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_DemetraUI=Demetra UI", "AdvancedOption_Keywords_DemetraUI=demetraui"})
public final class DemetraUIOptionsPanelController extends OptionsPanelController {

    public static final String ID = "Demetra/DemetraUI";
    
    private DemetraUIPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        getPanel().getDataFormatComponent().setPreviewVisible(true);
        getPanel().load();
        changed = false;
    }

    @Override
    public void applyChanges() {
        getPanel().getDataFormatComponent().setPreviewVisible(false);
        getPanel().store();
        changed = false;
    }

    @Override
    public void cancel() {
        getPanel().getDataFormatComponent().setPreviewVisible(false);
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private DemetraUIPanel getPanel() {
        if (panel == null) {
            panel = new DemetraUIPanel(this);
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
