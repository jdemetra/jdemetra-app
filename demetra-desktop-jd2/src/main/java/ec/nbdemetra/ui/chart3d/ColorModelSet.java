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
package ec.nbdemetra.ui.chart3d;

import ec.nbdemetra.ui.chart3d.SurfaceModel.PlotColor;
import ec.nbdemetra.ui.chart3d.SurfaceModel.PlotType;
import java.awt.Color;

/**
 * A simple {@link SurfaceColor} implementations that uses two ColorMode per
 * plot type.
 *
 *
 * @see Eric aro
 * @see Yanto Suryono <yanto@fedu.uec.ac.jp>
 * @author Mats Maggi
 */
public class ColorModelSet implements SurfaceColor {

    public static float RED_H = 0.941896f;
    public static float RED_S = 0.7517241f;
    public static float RED_B = 0.5686275f;
    public static float GOLD_H = 0.1f;
    public static float GOLD_S = 0.9497207f;
    public static float GOLD_B = 0.7019608f;
    protected ColorModel dualshade;
    protected ColorModel grayscale;
    protected ColorModel spectrum;
    protected ColorModel fog;
    protected ColorModel opaque;
    protected ColorModel alt_dualshade;
    protected ColorModel alt_grayscale;
    protected ColorModel alt_spectrum;
    protected ColorModel alt_fog;
    protected ColorModel alt_opaque;
    protected Color lineColor = Color.DARK_GRAY;
    protected Color lineboxColor = Color.getHSBColor(0f, 0f, 0.5f);
    protected Color lightColor = Color.WHITE;
    protected Color boxColor = Color.getHSBColor(0f, 0f, 0.95f);//Color.getHSBColor(226f/240f,145f/240f,1f);

    public ColorModelSet() {
        dualshade = new ColorModel(ColorModel.DUALSHADE, RED_H, RED_S, RED_B, 0.4f, 1f);
        grayscale = new ColorModel(ColorModel.DUALSHADE, 0f, 0f, 0f, 0f, 1f);
        spectrum = new ColorModel(ColorModel.SPECTRUM, 0f, 1f, 1f, 0f, .6666f);
        fog = new ColorModel(ColorModel.FOG, RED_H, RED_S, RED_B, 0f, 1f);
        opaque = new ColorModel(ColorModel.OPAQUE, RED_H, 0.1f, 1f, 0f, 0f);

        alt_dualshade = new ColorModel(ColorModel.DUALSHADE, GOLD_H, GOLD_S, GOLD_B, 0.4f, 1f);
        alt_grayscale = new ColorModel(ColorModel.DUALSHADE, 0f, 0f, 0f, 0f, 1f);
        alt_spectrum = new ColorModel(ColorModel.SPECTRUM, 0f, 1f, 0.8f, 0f, .6666f);
        alt_fog = new ColorModel(ColorModel.FOG, GOLD_H, 0f, GOLD_B, 0f, 1f);
        alt_opaque = new ColorModel(ColorModel.OPAQUE, GOLD_H, 0.1f, 1f, 0f, 0f);

    }
    protected PlotColor color_mode = PlotColor.SPECTRUM;

    public void setPlotColor(PlotColor v) {
        this.color_mode = v;
    }
    protected PlotType plot_mode = PlotType.CONTOUR;

    public void setPlotType(PlotType type) {
        this.plot_mode = type;
    }

    @Override
    public Color getBackgroundColor() {
        return lightColor;
    }

    @Override
    public Color getLineBoxColor() {
        return lineboxColor;
    }

    @Override
    public Color getBoxColor() {
        return boxColor;
    }

    @Override
    public Color getLineColor() {
        return lineColor;
    }

    @Override
    public Color getTextColor() {
        return lineColor;
    }

    @Override
    public Color getLineColor(int curve, float z) {
        return Color.BLACK;
    }

    @Override
    public Color getPolygonColor(int curve, float z) {
        if (curve == 1) {
            return getFirstPolygonColor(z);
        }
        if (curve == 2) {
            return getSecondPolygonColor(z);
        }
        return Color.blue;
    }

    @Override
    public Color getFirstPolygonColor(float z) {
        //contour,density  plot does not fit with opaque color 
        if (plot_mode == PlotType.CONTOUR || plot_mode == PlotType.DENSITY) {
            if (color_mode == PlotColor.OPAQUE) {
                return dualshade.getPolygonColor(z);
            }
        }

        switch (color_mode) {
            case OPAQUE:
                return opaque.getPolygonColor(z);
            case GRAYSCALE:
                return grayscale.getPolygonColor(z);
            case SPECTRUM:
                return spectrum.getPolygonColor(z);
            case DUALSHADE:
                return dualshade.getPolygonColor(z);
            case FOG:
                return fog.getPolygonColor(z);
            default:
                return Color.blue;
        }
    }

    @Override
    public Color getSecondPolygonColor(float z) {
        //contour,density  plot does not fit with opaque color 
        if (plot_mode == PlotType.CONTOUR || plot_mode == PlotType.DENSITY) {
            if (color_mode == PlotColor.OPAQUE) {
                return alt_dualshade.getPolygonColor(z);
            }
        }
        switch (color_mode) {
            case OPAQUE:
                return alt_opaque.getPolygonColor(z);
            case GRAYSCALE:
                return alt_grayscale.getPolygonColor(z);
            case SPECTRUM:
                return alt_spectrum.getPolygonColor(z);
            case DUALSHADE:
                return alt_dualshade.getPolygonColor(z);
            case FOG:
                return alt_fog.getPolygonColor(z);
            default:
                return Color.blue;
        }
    }
}
