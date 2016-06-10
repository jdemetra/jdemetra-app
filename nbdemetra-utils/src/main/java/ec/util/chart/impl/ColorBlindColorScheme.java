/*
 * Copyright 2015 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
 * Color Blindness Color Scheme based on
 * http://edwardtufte.com.s3.amazonaws.com/colorblind_palette.png
 *
 * @author Laurent Jadoul
 */
@ServiceProvider(service = ColorScheme.class)
public final class ColorBlindColorScheme extends AbstractColorScheme {

    private final int ORANGE = 0xE69F00;
    private final int SKY_BLUE = 0x56B4E9;
    private final int BLUISH_GREEN = 0x009E73;
    private final int BLUE = 0x0072B2;
    private final int VERMILION = 0xD55E00;
    private final int REDDISH_PURPLE = 0xCC79A7;
    private final int BLACK = 0x000000;
    private final int YELLOW = 0xF0E442;

    @Override
    public String getName() {
        return "Color Blindness";
    }

    @Override
    public List<Integer> getAreaColors() {
        return Arrays.asList(ORANGE,
                SKY_BLUE,
                BLUISH_GREEN,
                BLUE,
                VERMILION,
                REDDISH_PURPLE,
                BLACK,
                YELLOW
        );
    }

    @Override
    public Map<KnownColor, Integer> getAreaKnownColors() {
        return knownColors(BLUE, VERMILION, BLACK, BLUISH_GREEN, ORANGE, REDDISH_PURPLE, YELLOW);
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
