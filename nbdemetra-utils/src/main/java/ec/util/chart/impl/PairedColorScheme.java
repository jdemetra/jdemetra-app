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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.openide.util.lookup.ServiceProvider;

/**
 * Paired color scheme.
 *
 * @see http://colorbrewer2.org/
 * @author Philippe Charles
 * @author Jeremy Demortier
 */
@ServiceProvider(service = ColorScheme.class)
public class PairedColorScheme extends AbstractColorScheme {

    public static final int C0 = 0xA6CEE3; // LIGHT_BLUE
    public static final int C1 = 0x1F78B4; // BLUE
    public static final int C2 = 0xB2DF8A; // LIGHT_GREEN
    public static final int C3 = 0x33A02C; // GREEN
    public static final int C4 = 0xFB9A99; // LIGHT_RED
    public static final int C5 = 0xE31A1C; // RED
    public static final int C6 = 0xFDBF6F; // LIGHT_ORANGE
    public static final int C7 = 0xFF7F00; // ORANGE
    public static final int C8 = 0xCAB2D6; // LIGHT_PURPLE
    public static final int C9 = 0x6A3D9A; // PURPLE

    @Override
    public String getName() {
        return "Paired";
    }

    @Override
    public List<Integer> getAreaColors() {
        return Arrays.asList(C0, C2, C4, C6, C8);
    }

    @Override
    public List<Integer> getLineColors() {
        return Arrays.asList(C1, C3, C5, C7, C9);
    }

    @Override
    public Map<KnownColor, Integer> getAreaKnownColors() {
        return knownColors(C0, C8, BasicColor.GRAY, C2, C6, C4, BasicColor.YELLOW);
    }

    @Override
    public Map<KnownColor, Integer> getLineKnownColors() {
        return knownColors(C1, C9, BasicColor.DARK_GRAY, C3, C7, C5, BasicColor.YELLOW);
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
