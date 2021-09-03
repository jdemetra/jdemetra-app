package demetra.desktop.core.components;

import demetra.desktop.components.ComponentBackendSpi;
import demetra.desktop.components.JTsTable;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;

import javax.swing.*;

@DirectImpl
@ServiceProvider
public final class TsTableBackend implements ComponentBackendSpi {

    @Override
    public boolean handles(Class<? extends JComponent> type) {
        return JTsTable.class.equals(type);
    }

    @Override
    public void install(JComponent component) {
        new TsTableUI().install((JTsTable) component);
    }
}
