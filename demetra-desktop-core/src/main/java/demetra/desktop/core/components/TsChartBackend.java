package demetra.desktop.core.components;

import demetra.desktop.components.ComponentBackendSpi;
import demetra.desktop.components.JTsChart;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;

import javax.swing.*;

@DirectImpl
@ServiceProvider
public final class TsChartBackend implements ComponentBackendSpi {

    @Override
    public boolean handles(Class<? extends JComponent> type) {
        return JTsChart.class.equals(type);
    }

    @Override
    public void install(JComponent component) {
        new TsChartUI().install((JTsChart) component);
    }
}
