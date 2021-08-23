package internal.ui;

import demetra.timeseries.TsMoniker;
import demetra.ui.TsMonikerUISpi;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import java.beans.BeanInfo;
import javax.swing.Icon;
import nbbrd.service.ServiceProvider;
import org.openide.util.ImageUtilities;

@ServiceProvider
public final class DefaultTsMonikerUISpi implements TsMonikerUISpi {

    @Override
    public Icon getIconOrNull(TsMoniker moniker) {
        return DataSourceProviderBuddySupport.getDefault()
                .getIcon(moniker, BeanInfo.ICON_COLOR_16x16, false)
                .map(ImageUtilities::image2Icon)
                .orElse(null);
    }
}
