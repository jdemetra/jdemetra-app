/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package internal.ui;

import org.openide.util.lookup.ServiceProvider;
import demetra.ui.TsActionsOpenSpi;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = TsActionsOpenSpi.class)
public final class NullTsAction implements TsActionsOpenSpi {

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
