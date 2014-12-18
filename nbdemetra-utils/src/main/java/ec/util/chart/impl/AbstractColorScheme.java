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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Philippe Charles
 */
public abstract class AbstractColorScheme implements ColorScheme {

    @Override
    public int getTextColor() {
        // default value for backward compatibility
        return BasicColor.BLACK;
    }

    @Override
    public int getAxisColor() {
        // default value for backward compatibility
        return BasicColor.GRAY;
    }

    @Override
    public List<Integer> getLineColors() {
        return getAreaColors();
    }

    @Override
    public Map<KnownColor, Integer> getLineKnownColors() {
        return getAreaKnownColors();
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof AbstractColorScheme && equals((AbstractColorScheme) obj));
    }

    private boolean equals(AbstractColorScheme other) {
        return this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public static Map<KnownColor, Integer> knownColors(int blue, int brown, int gray, int green, int orange, int red, int yellow) {
        EnumMap<KnownColor, Integer> result = new EnumMap<>(KnownColor.class);
        result.put(KnownColor.BLUE, blue);
        result.put(KnownColor.BROWN, brown);
        result.put(KnownColor.GRAY, gray);
        result.put(KnownColor.GREEN, green);
        result.put(KnownColor.ORANGE, orange);
        result.put(KnownColor.RED, red);
        result.put(KnownColor.YELLOW, yellow);
        return result;
    }
}
