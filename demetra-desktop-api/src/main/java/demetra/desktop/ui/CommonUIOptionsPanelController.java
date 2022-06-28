/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/template_mypluginOptionsPanelController.java to edit this template
 */
package demetra.desktop.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

@OptionsPanelController.SubRegistration(
        location = "Demetra",
        displayName = "#AdvancedOption_DisplayName_CommonUI",
        keywords = "#AdvancedOption_Keywords_CommonUI",
        keywordsCategory = "Demetra/CommonUI",
        position=10
)
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_CommonUI=CommonUI", "AdvancedOption_Keywords_CommonUI=UI"})
public final class CommonUIOptionsPanelController extends OptionsPanelController {

    private CommonUIPanel panel;
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

    private CommonUIPanel getPanel() {
        if (panel == null) {
            panel = new CommonUIPanel(this);
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
