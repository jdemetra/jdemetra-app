/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package internal.ui;

import org.openide.util.lookup.ServiceProvider;
import demetra.ui.TsActionSpi;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = TsActionSpi.class)
public final class NullTsAction implements TsActionSpi {

    @Override
    public String getName() {
        return "NullTsAction";
    }

    @Override
    public String getDisplayName() {
        return "Do nothing";
    }

    @Override
    public void open(demetra.timeseries.Ts ts) {
    }
}
