package internal.ui;

import demetra.timeseries.TsMoniker;
import demetra.ui.IconManagerSpi;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import java.beans.BeanInfo;
import javax.swing.Icon;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import org.openide.util.ImageUtilities;

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
}
