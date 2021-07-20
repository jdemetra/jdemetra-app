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
package ec.nbdemetra.ui.chart3d;

import demetra.ui.actions.Actions;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * Main panel to display a surface plot.
 *
 * @author Mats Maggi
 */
public class JSurfacePanel extends JPanel {

    private JSurface surface;
    public static final String EXPORT_ACTION = "export_image";

    public JSurfacePanel() {
        initComponents();
    }

    public JSurfacePanel(SurfaceModel model) {
        super(new BorderLayout());
        initComponents();
    }

    public void setModel(SurfaceModel model) {
        surface.setModel(model);
    }

    public JSurface getSurface() {
        return surface;
    }

    private void initComponents() {
        surface = new JSurface();
        setLayout(new BorderLayout());
        add(surface, BorderLayout.CENTER);
        
        getSurface().setComponentPopupMenu(buildMenu().getPopupMenu());
    }
    
    private JMenu buildMenu() {
        JMenu result = new JMenu();
        JMenuItem item;
        item = new JMenuItem(new ExportJPGAction());
        item.setText("Save as image");
        Actions.hideWhenDisabled(item);
        result.add(item);
        return result;
    }

    private class ExportJPGAction extends AbstractAction {

        public ExportJPGAction() {
            super(EXPORT_ACTION);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jfc = new JFileChooser();
            int retVal = jfc.showSaveDialog(null);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = jfc.getSelectedFile();
                    surface.doExportJPG(f);
                } catch (IOException ex) {
                    
                }
            }
        }
    }
}
