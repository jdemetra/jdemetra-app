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

import static ec.util.chart.ColorSchemeSupport.rgb;

/**
 *
 * @author Philippe Charles
 */
public final class BasicColor {

    private BasicColor() {
        // static class
    }
    public static final int BLACK = 0x000000;
    public static final int WHITE = 0xFFFFFF;
    public static final int LIGHT_GRAY = rgb(192, 192, 192);
    public static final int GRAY = rgb(128, 128, 128);
    public static final int DARK_GRAY = rgb(64, 64, 64);
    public static final int YELLOW = rgb(255, 255, 0);
    public static final int RED = rgb(255, 0, 0);
    public static final int GREEN = rgb(0, 255, 0);
    public static final int BLUE = rgb(0, 0, 255);
}
