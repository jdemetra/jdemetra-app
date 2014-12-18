/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.ui.commands;

import ec.tstoolkit.design.UtilityClass;
import ec.ui.interfaces.IZoomableGrid;
import static ec.ui.interfaces.IZoomableGrid.ZOOM_PROPERTY;
import ec.util.various.swing.JCommand;
import javax.annotation.Nonnull;

/**
 *
 * @author Mats Maggi
 */
@UtilityClass(IZoomableGrid.class)
public class GridCommand {
    
    @Nonnull
    public static JCommand<IZoomableGrid> applyZoomRatio(int zoomRatio) {
        return new ZoomRatioCommand(zoomRatio);
    }
    
    @Nonnull
    public static JCommand<IZoomableGrid> applyColorScale(double scale) {
        return new ColorScaleCommand(scale);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    public static final class ZoomRatioCommand extends ComponentCommand<IZoomableGrid> {

        private final int zoomRatio;

        public ZoomRatioCommand(int zoomRatio) {
            super(ZOOM_PROPERTY);
            this.zoomRatio = zoomRatio;
        }

        @Override
        public boolean isSelected(IZoomableGrid component) {
            return zoomRatio == component.getZoomRatio();
        }

        @Override
        public void execute(IZoomableGrid component) throws Exception {
            component.setZoomRatio(zoomRatio);
        }
    }
    
    public static final class ColorScaleCommand extends ComponentCommand<IZoomableGrid> {

        private final double colorScale;

        public ColorScaleCommand(double colorScale) {
            super(IZoomableGrid.COLOR_SCALE_PROPERTY);
            this.colorScale = colorScale;
        }

        @Override
        public boolean isSelected(IZoomableGrid component) {
            return colorScale == component.getColorScale();
        }

        @Override
        public void execute(IZoomableGrid component) throws Exception {
            component.setColorScale(colorScale);
        }
    }
    
    //</editor-fold>
}
