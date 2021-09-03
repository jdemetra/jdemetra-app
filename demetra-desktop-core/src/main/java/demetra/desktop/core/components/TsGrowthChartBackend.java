package demetra.desktop.core.components;

import demetra.desktop.components.ComponentBackendSpi;
import demetra.desktop.components.JTsGrowthChart;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;

import javax.swing.*;

@DirectImpl
@ServiceProvider
public final class TsGrowthChartBackend implements ComponentBackendSpi {

    @Override
    public boolean handles(Class<? extends JComponent> type) {
        return JTsGrowthChart.class.equals(type);
    }

    @Override
    public void install(JComponent component) {
        new TsGrowthChartUI().install((JTsGrowthChart) component);
    }
}
