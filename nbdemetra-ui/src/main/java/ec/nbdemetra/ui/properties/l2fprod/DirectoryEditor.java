/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import ec.tstoolkit.utilities.Directory;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Paths;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;

/**
 *
 * @author Kristof Bayens
 */
public class DirectoryEditor extends AbstractPropertyEditor {
    private Directory directory_;

    public DirectoryEditor() {
        editor = new JButton(new AbstractAction("...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser comp = new JFileChooser();
                comp.setCurrentDirectory(Paths.get(".").toFile());
                comp.setDialogTitle("Choose Folder");
                comp.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                comp.setAcceptAllFileFilterUsed(false);
                if (comp.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                    setValue(new Directory(comp.getCurrentDirectory().getPath()));
            }
        });
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof Directory) {
            Directory old = directory_;
            directory_ = (Directory) value;
            firePropertyChange(old, directory_);
        }
    }

    @Override
    public Object getValue() {
        return directory_;
    }
}
