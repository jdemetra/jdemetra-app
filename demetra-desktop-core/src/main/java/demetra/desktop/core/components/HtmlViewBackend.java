package demetra.desktop.core.components;

import demetra.desktop.components.ComponentBackendSpi;
import demetra.desktop.components.JHtmlView;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;

@DirectImpl
@ServiceProvider
public final class HtmlViewBackend implements ComponentBackendSpi {

    @Override
    public boolean handles(@NonNull Class<? extends JComponent> type) {
        return JHtmlView.class.equals(type);
    }

    @Override
    public void install(@NonNull JComponent component) {
        new HtmlViewUI().install((JHtmlView) component);
    }
}
