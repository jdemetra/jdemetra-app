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

import java.awt.Color;

/**
 * Stands for a Color Model
 *
 * @see Eric aro
 * @see Yanto Suryono <yanto@fedu.uec.ac.jp>
 * @author Mats Maggi
 */
public class ColorModel {

    public static final byte DUALSHADE = 0;
    public static final byte SPECTRUM = 1;
    public static final byte FOG = 2;
    public static final byte OPAQUE = 3;
    float hue;
    float sat;
    float bright;
    float min; // hue|sat|bright of z=0
    float max; // Hue|sat|bright  of z=1
    byte mode = 0;
    Color ocolor; // fixed color for opaque mode

    public ColorModel(byte mode, float hue, float sat, float bright, float min, float max) {
        this.mode = mode;
        this.hue = hue;
        this.sat = sat;
        this.bright = bright;
        this.min = min;
        this.max = max;
    }

    public Color getPolygonColor(float z) {
        if (z < 0 || z > 1) {
            return Color.WHITE;
        }
        switch (mode) {
            case DUALSHADE: {
                return color(hue, sat, norm(z));
            }
            case SPECTRUM: {
                return color(norm(1 - z), sat, bright);
            }
            case FOG: {
                return color(hue, norm(z), bright);
            }
            case OPAQUE: {
                if (ocolor == null) {
                    ocolor = color(hue, sat, bright);
                }
                return ocolor;
            }
        }
        return Color.WHITE;
    }

    private Color color(float hue, float sat, float bright) {
        return Color.getHSBColor(hue, sat, bright);
    }

    private float norm(float z) {
        if (min == max) {
            return min;
        }
        return min + z * (max - min);
    }
}//end of class
