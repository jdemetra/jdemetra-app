/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.demo;

import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.tsaction.ITsAction;
import ec.tss.Ts;
import java.awt.Image;
import javax.swing.JOptionPane;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Philippe Charles
 */
public enum DemoTsActions implements ITsAction {

    SHOW_DIALOG {
        @Override
        public void open(Ts ts) {
            JOptionPane.showMessageDialog(null, "This would have opened the selected time series");
        }
    },
    DO_NOTHING {
        @Override
        public void open(Ts ts) {
        }
    };

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    @Override
    public Sheet createSheet() {
        return new Sheet();
    }
}
