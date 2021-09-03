package demetra.desktop.core.components;

import demetra.desktop.components.ComponentBackendSpi;
import demetra.desktop.components.JTsGrid;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;

import javax.swing.*;

@DirectImpl
@ServiceProvider
public final class TsGridBackend implements ComponentBackendSpi {

    @Override
    public boolean handles(Class<? extends JComponent> type) {
        return JTsGrid.class.equals(type);
    }

    @Override
    public void install(JComponent component) {
        new TsGridUI().install((JTsGrid) component);
    }
}
