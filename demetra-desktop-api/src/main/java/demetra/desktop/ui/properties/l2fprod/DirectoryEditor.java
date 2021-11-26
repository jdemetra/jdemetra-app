/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;

/**
 *
 * @author Kristof Bayens
 */
public class DirectoryEditor extends AbstractPropertyEditor {
    private File directory_;

    public DirectoryEditor() {
        editor = new JButton(new AbstractAction("...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser comp = new JFileChooser();
                comp.setCurrentDirectory(new File("."));
                comp.setDialogTitle("Choose Folder");
                comp.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                comp.setAcceptAllFileFilterUsed(false);
                if (comp.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                    setValue(comp.getCurrentDirectory());
            }
        });
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof File) {
            File old = directory_;
            directory_ = (File) value;
            firePropertyChange(old, directory_);
        }
    }

    @Override
    public File getValue() {
        return directory_;
    }
}
