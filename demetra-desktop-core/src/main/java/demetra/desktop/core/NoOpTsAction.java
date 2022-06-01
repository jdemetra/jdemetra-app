/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.core;

import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import demetra.desktop.TsActionOpenSpi;

/**
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class NoOpTsAction implements TsActionOpenSpi {

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
