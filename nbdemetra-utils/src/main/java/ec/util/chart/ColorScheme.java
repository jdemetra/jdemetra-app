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
package ec.util.chart;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * Defines a color scheme for charts.
 * <p>
 * In color theory, a <strong>color scheme</strong> is the choice of colors used
 * in design for a range of media. For example, the use of a white background
 * with black text is an example of a basic and commonly default color scheme in
 * web design.
 * <p>
 * Color schemes are used to create style and appeal. Colors that create an
 * aesthetic feeling when used together will commonly accompany each other in
 * color schemes. A basic color scheme will use two colors that look appealing
 * together. More advanced color schemes involve several colors in combination,
 * usually based around a single color; for example, text with such colors as
 * red, yellow, orange and light blue arranged together on a black background in
 * a magazine article.
 * <p>
 * Color schemes can also contain different shades of a single color; for
 * example, a color scheme that mixes different shades of green, ranging from
 * very light (almost white) to very dark.
 *
 * @see http://en.wikipedia.org/wiki/Color_scheme
 * @author Philippe Charles
 * @author Demortier Jeremy
 */
public interface ColorScheme {

    /**
     * Returns the color scheme identifier.
     *
     * @return a non-null identifier
     */
    @Nonnull
    String getName();

    /**
     * Returns the color scheme label.
     *
     * @return a non-null label
     */
    @Nonnull
    String getDisplayName();

    @Nonnull
    List<Integer> getAreaColors();

    @Nonnull
    List<Integer> getLineColors();

    @Nonnull
    Map<KnownColor, Integer> getAreaKnownColors();

    @Nonnull
    Map<KnownColor, Integer> getLineKnownColors();

    /**
     * Returns the RGB value representing the background color.
     *
     * @return the RGB value of the color
     */
    int getBackColor();

    /**
     * Returns the RGB value representing the plot color.
     *
     * @return the RGB value of the color
     */
    int getPlotColor();

    /**
     * Returns the RGB value representing the grid color.
     *
     * @return the RGB value of the color
     */
    int getGridColor();

    /**
     * Returns the RGB value representing the text color.
     *
     * @return the RGB value of the color
     */
    int getTextColor();

    /**
     * Returns the RGB value representing the axis color.
     *
     * @return the RGB value of the color
     */
    int getAxisColor();

    /**
     * Defines a basic set of colors to be used in customized charts.
     *
     * @author Demortier Jeremy
     */
    public enum KnownColor {

        YELLOW, GREEN, BLUE, BROWN, ORANGE, GRAY, RED
    }
}
