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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Tango color scheme.
 *
 * @see http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines
 * @author Philippe Charles
 * @author Jeremy Demortier
 */
public class TangoColorScheme extends AbstractColorScheme {

    // Tango color palette
    public static final int LIGHT_BUTTER = 0xfce94f;
    public static final int BUTTER = 0xedd400;
    public static final int DARK_BUTTER = 0xc4a000;
    public static final int LIGHT_ORANGE = 0xfcaf3e;
    public static final int ORANGE = 0xf57900;
    public static final int DARK_ORANGE = 0xce5c00;
    public static final int LIGHT_CHOCOLATE = 0xe9b96e;
    public static final int CHOCOLATE = 0xc17d11;
    public static final int DARK_CHOCOLATE = 0x8f5902;
    public static final int LIGHT_CHAMELEON = 0x8ae234;
    public static final int CHAMELEON = 0x73d216;
    public static final int DARK_CHAMELEON = 0x4e9a06;
    public static final int LIGHT_SKY_BLUE = 0x729fcf;
    public static final int SKY_BLUE = 0x3465a4;
    public static final int DARK_SKY_BLUE = 0x204a87;
    public static final int LIGHT_PLUM = 0xad7fa8;
    public static final int PLUM = 0x75507b;
    public static final int DARK_PLUM = 0x5c3566;
    public static final int LIGHT_SCARLET_RED = 0xef2929;
    public static final int SCARLET_RED = 0xcc0000;
    public static final int DARK_SCARLET_RED = 0xa40000;
    public static final int ALUMINIUM1 = 0xeeeeec;
    public static final int ALUMINIUM2 = 0xd3d7cf;
    public static final int ALUMINIUM3 = 0xbabdb6;
    public static final int ALUMINIUM4 = 0x888a85;
    public static final int ALUMINIUM5 = 0x555753;
    public static final int ALUMINIUM6 = 0x2e3436;

    @Override
    public String getName() {
        return "Tango";
    }

    @Override
    public List<Integer> getAreaColors() {
        return Arrays.asList(
                LIGHT_ORANGE,
                LIGHT_SKY_BLUE,
                DARK_CHAMELEON,
                LIGHT_PLUM,
                DARK_CHOCOLATE,
                SCARLET_RED,
                BUTTER,
                ORANGE,
                SKY_BLUE,
                CHAMELEON,
                PLUM,
                CHOCOLATE,
                DARK_SCARLET_RED,
                DARK_BUTTER,
                DARK_ORANGE,
                DARK_SKY_BLUE,
                LIGHT_CHAMELEON,
                DARK_PLUM,
                LIGHT_CHOCOLATE,
                LIGHT_SCARLET_RED,
                LIGHT_BUTTER);
    }

    @Override
    public Map<KnownColor, Integer> getAreaKnownColors() {
        return knownColors(
                LIGHT_SKY_BLUE,
                LIGHT_CHAMELEON,
                ALUMINIUM2,
                LIGHT_CHAMELEON,
                LIGHT_ORANGE,
                LIGHT_SCARLET_RED,
                LIGHT_BUTTER);
    }

    @Override
    public Map<KnownColor, Integer> getLineKnownColors() {
        return knownColors(
                DARK_SKY_BLUE,
                DARK_CHAMELEON,
                ALUMINIUM4,
                DARK_CHAMELEON,
                DARK_ORANGE,
                DARK_SCARLET_RED,
                DARK_BUTTER);
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
