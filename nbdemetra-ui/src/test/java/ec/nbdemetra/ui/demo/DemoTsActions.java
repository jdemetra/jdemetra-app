/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.demo;

import ec.nbdemetra.ui.DemetraUiIcon;
import ec.tss.Ts;
import java.awt.Image;
import javax.swing.JOptionPane;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;
import demetra.ui.TsActionSpi;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = TsActionSpi.class)
public final class DemoTsActions implements TsActionSpi {

    public static final String NAME = "Demo";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    @Override
    public void open(demetra.timeseries.Ts ts) {
        JOptionPane.showMessageDialog(null, "This would have opened the selected time series");
    }
}
