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

import ec.util.chart.ColorScheme;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Map;
import org.openide.util.lookup.ServiceProvider;

/**
 * Solarized - Precision colors for machines and people.
 * <p>
 * Solarized is a sixteen color palette (eight monotones, eight accent colors)
 * designed for use with terminal and gui applications. It has several unique
 * properties. I designed this colorscheme with both precise CIELAB lightness
 * relationships and a refined set of hues based on fixed color wheel
 * relationships. It has been tested extensively in real world use on color
 * calibrated displays (as well as uncalibrated/intentionally miscalibrated
 * displays) and in a variety of lighting conditions
 *
 * @see http://ethanschoonover.com/solarized
 * @version 1.0.0beta2
 * @author Philippe Charles
 * @author Jeremy Demortier
 */
public abstract class SolarizedColorScheme extends AbstractColorScheme {

    public static final int BASE03 = 0x002b36;
    public static final int BASE02 = 0x073642;
    public static final int BASE01 = 0x586e75;
    public static final int BASE00 = 0x657b83;
    public static final int BASE0 = 0x839496;
    public static final int BASE1 = 0x93a1a1;
    public static final int BASE2 = 0xeee8d5;
    public static final int BASE3 = 0xfdf6e3;
    public static final int YELLOW = 0xb58900;
    public static final int ORANGE = 0xcb4b16;
    public static final int RED = 0xdc322f;
    public static final int MAGENTA = 0xd33682;
    public static final int VIOLET = 0x6c71c4;
    public static final int BLUE = 0x268bd2;
    public static final int CYAN = 0x2aa198;
    public static final int GREEN = 0x859900;

    @Override
    public List<Integer> getAreaColors() {
        return asList(GREEN, RED, BLUE, VIOLET, YELLOW, MAGENTA, CYAN, ORANGE);
    }

    @Override
    public Map<KnownColor, Integer> getAreaKnownColors() {
        return knownColors(BLUE, VIOLET, BASE1, GREEN, ORANGE, RED, YELLOW);
    }

    @Override
    public int getGridColor() {
        return BASE1;
    }

    public static List<Integer> getBackgroundTones() {
        return asList(BASE03, BASE02, BASE2, BASE3);
    }

    public static List<Integer> getContentTones() {
        return asList(BASE01, BASE00, BASE0, BASE1);
    }

    public static List<Integer> getAccentColors() {
        return asList(YELLOW, ORANGE, RED, MAGENTA, VIOLET, BLUE, CYAN, GREEN);
    }

    /**
     * Solarized dark color scheme.
     *
     * @see SolarizedColorScheme
     * @author Philippe Charles
     * @author Jeremy Demortier
     */
    @ServiceProvider(service = ColorScheme.class)
    public static class SolarizedDarkColorScheme extends SolarizedColorScheme {

        @Override
        public String getName() {
            return "Solarized - Dark";
        }

        @Override
        public int getBackColor() {
            return BasicColor.WHITE;
        }

        @Override
        public int getPlotColor() {
            return BASE03;
        }
    }

    /**
     * Solarized light color scheme.
     *
     * @see SolarizedColorScheme
     * @author Philippe Charles
     * @author Jeremy Demortier
     */
    @ServiceProvider(service = ColorScheme.class)
    public static class SolarizedLightColorScheme extends SolarizedColorScheme {

        @Override
        public String getName() {
            return "Solarized - Light";
        }

        @Override
        public int getBackColor() {
            return BasicColor.WHITE;
        }

        @Override
        public int getPlotColor() {
            return BASE3;
        }
    }
}
