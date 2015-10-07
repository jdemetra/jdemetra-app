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
package ec.util.chart.impl;

import ec.util.chart.ColorSchemeSupport;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Android 4 color scheme.
 *
 * @see http://developer.android.com/design/style/color.html
 * @author Philippe Charles
 * @author Jeremy Demortier
 */
public abstract class AndroidColorScheme extends AbstractColorScheme {

    // Android 4 colors
    public static final int BLUE = 0x33B5E5;
    public static final int PURPLE = 0xAA66CC;
    public static final int GREEN = 0x99CC00;
    public static final int ORANGE = 0xFFBB33;
    public static final int RED = 0xFF4444;
    public static final int DARK_BLUE = 0x0099CC;
    public static final int DARK_PURPLE = 0x9933CC;
    public static final int DARK_GREEN = 0x669900;
    public static final int DARK_ORANGE = 0xFF8800;
    public static final int DARK_RED = 0xCC0000;

    @Override
    public List<Integer> getAreaColors() {
        return Arrays.asList(BLUE,
                PURPLE,
                GREEN,
                ORANGE,
                RED,
                DARK_BLUE,
                DARK_PURPLE,
                DARK_GREEN,
                DARK_ORANGE,
                DARK_RED);
    }

    @Override
    public Map<KnownColor, Integer> getAreaKnownColors() {
        return knownColors(BLUE,
                DARK_RED,
                BasicColor.GRAY,
                GREEN,
                DARK_ORANGE,
                RED,
                ORANGE);
    }

    /**
     * Android 4 dark color scheme.
     *
     * @see AndroidColorScheme
     * @author Philippe Charles
     * @author Jeremy Demortier
     */
    public static class AndroidDarkColorScheme extends AndroidColorScheme {

        @Override
        public String getName() {
            return "Android - Dark";
        }
        
        @Override
        public String getDisplayName() {
            return "Ice Cream Sandwich - Dark";
        }

        @Override
        public int getBackColor() {
            return BasicColor.WHITE;
        }

        @Override
        public int getPlotColor() {
            return ColorSchemeSupport.rgb(10, 10, 10);
        }

        @Override
        public int getGridColor() {
            return BasicColor.GRAY;
        }
    }

    /**
     * Android 4 light color scheme.
     *
     * @see AndroidColorScheme
     * @author Philippe Charles
     * @author Jeremy Demortier
     */
    public static class AndroidLightColorScheme extends AndroidColorScheme {

        @Override
        public String getName() {
            return "Android - Light";
        }

        @Override
        public String getDisplayName() {
            return "Ice Cream Sandwich - Light";
        }

        @Override
        public int getBackColor() {
            return BasicColor.WHITE;
        }

        @Override
        public int getPlotColor() {
            return BasicColor.WHITE;
        }

        @Override
        public int getGridColor() {
            return BasicColor.LIGHT_GRAY;
        }
    }
}
