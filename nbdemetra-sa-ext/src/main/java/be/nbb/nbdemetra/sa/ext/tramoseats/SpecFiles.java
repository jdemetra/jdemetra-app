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
package be.nbb.nbdemetra.sa.ext.tramoseats;

import ec.nbdemetra.tramoseats.TramoSeatsDocumentManager;
import ec.nbdemetra.ui.Menus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "SaExtensions",
        id = "be.nbb.nbdemetra.sa.ext.tramoseats.SpecFiles"
)
@ActionRegistration(
        displayName = "#CTL_SpecFiles", lazy=false
)
@ActionReferences({
    @ActionReference(path = TramoSeatsDocumentManager.CONTEXTPATH, position=9010, separatorBefore=9000),
    @ActionReference(path = "Shortcuts", name = "S")
})
@Messages("CTL_SpecFiles=Spec file")
public final class SpecFiles extends AbstractAction implements Presenter.Popup {
    
    public static final String PATH="/SpecFiles";
    public static FileChooserBuilder fileChooserBuilder = new FileChooserBuilder(SpecFiles.class);
 
    @Override
    public JMenuItem getPopupPresenter() {
        JMenu menu=new JMenu(Bundle.CTL_SpecFiles());
        Menus.fillMenu(menu, TramoSeatsDocumentManager.CONTEXTPATH+PATH);
        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

}
