package internal.ui;

import demetra.timeseries.TsMoniker;
import demetra.desktop.IconManagerSpi;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import org.openide.util.ImageUtilities;

import javax.swing.*;
import java.awt.*;
import java.beans.BeanInfo;

@DirectImpl
@ServiceProvider
public final class DefaultIconManagerSpi implements IconManagerSpi {

    @Override
    public Icon getIconOrNull(TsMoniker moniker) {
        return DataSourceProviderBuddySupport.getDefault()
                .getIcon(moniker, BeanInfo.ICON_COLOR_16x16, false)
                .map(ImageUtilities::image2Icon)
                .orElse(null);
    }

    @Override
    public Image getImageOrNull(TsMoniker moniker, int type, boolean opened) {
        return DataSourceProviderBuddySupport.getDefault()
                .getIcon(moniker, type, opened)
                .orElse(null);
    }
}
