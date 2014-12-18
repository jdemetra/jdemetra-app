package ec.ui.interfaces;

import ec.nbdemetra.ui.awt.IPropertyChangeSource;
import ec.tss.tsproviders.utils.DataFormat;
import javax.annotation.Nullable;

/**
 * Interface that should be implemented by every control that has to display
 * time series.
 *
 * @author Jeremy Demortier
 */
public interface ITsControl extends IDisposable, IPropertyChangeSource {

    public static final String DATA_FORMAT_PROPERTY = "dataFormat";

    boolean isToolWindowLayout();

    void setToolWindowLayout(boolean behavior);

    @Nullable
    TooltipType getTsTooltip();

    void setTsTooltip(@Nullable TooltipType tooltipType);

    @Nullable
    ITsPrinter getPrinter();

    @Nullable
    ITsHelper getHelper();

    @Nullable
    DataFormat getDataFormat();

    void setDataFormat(@Nullable DataFormat dataFormat);

    public enum TooltipType {

        None, SmallChart, LastObs, Name, Identifier
    }
}
